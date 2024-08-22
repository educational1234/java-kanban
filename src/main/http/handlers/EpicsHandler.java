package main.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.managers.TaskManager;
import main.models.Epic;

import java.io.IOException;
import java.util.logging.Logger;

public class EpicsHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(EpicsHandler.class.getName());

    public EpicsHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        logger.info("Обработка GET-запроса для эпиков");
        String[] uriParts = exchange.getRequestURI().getPath().split("/");
        if (uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                Epic epic = manager.getEpicById(id);
                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }
                String response = gson.toJson(epic);
                sendText(exchange, response);
            } catch (NumberFormatException e) {
                logger.severe("Неверный формат ID в GET-запросе: " + e.getMessage());
                sendBadRequest(exchange);
            }
        } else if (uriParts.length == 4 && "subtasks".equals(uriParts[3])) {  // Новый эндпоинт
            try {
                int id = Integer.parseInt(uriParts[2]);
                Epic epic = manager.getEpicById(id);
                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }
                String response = gson.toJson(manager.getSubtasksByEpicId(id));
                sendText(exchange, response);
            } catch (NumberFormatException e) {
                logger.severe("Неверный формат ID в GET-запросе: " + e.getMessage());
                sendBadRequest(exchange);
            }
        } else {
            String response = gson.toJson(manager.getAllEpics());
            sendText(exchange, response);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        logger.info("Обработка POST-запроса для эпиков");
        String body = new String(exchange.getRequestBody().readAllBytes());
        if (body.isEmpty()) {
            logger.warning("Пустое тело запроса");
            sendBadRequest(exchange);
            return;
        }
        logger.info("Получен POST-тело: " + body);
        try {
            Epic epic = gson.fromJson(body, Epic.class);
            logger.info("Десериализованный эпик: " + epic);
            manager.createEpic(epic);
            sendResponse(exchange, 201, "Эпик создан");
        } catch (Exception e) {
            logger.severe("Ошибка обработки POST-запроса: " + e.getMessage());
            sendBadRequest(exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        logger.info("Обработка DELETE-запроса для эпиков");
        String[] uriParts = exchange.getRequestURI().getPath().split("/");
        if (uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                Epic epic = manager.getEpicById(id);
                if (epic == null) {
                    sendNotFound(exchange);
                    return;
                }
                manager.deleteEpicById(id);
                sendText(exchange, "Эпик удален");
            } catch (NumberFormatException e) {
                logger.severe("Неверный формат ID в DELETE-запросе: " + e.getMessage());
                sendBadRequest(exchange);
            }
        } else {
            manager.deleteAllEpics();
            sendText(exchange, "Все эпики удалены");
        }
    }
}
