package main.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.managers.TaskManager;
import main.models.Subtask;

import java.io.IOException;
import java.util.logging.Logger;

public class SubtasksHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(SubtasksHandler.class.getName());

    public SubtasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        logger.info("Обработка GET-запроса для подзадач");
        String[] uriParts = exchange.getRequestURI().getPath().split("/");
        if (uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                Subtask subtask = manager.getSubtaskById(id);
                if (subtask == null) {
                    sendNotFound(exchange);
                    return;
                }
                String response = gson.toJson(subtask);
                sendText(exchange, response);
            } catch (NumberFormatException e) {
                logger.severe("Неверный формат ID в GET-запросе: " + e.getMessage());
                sendBadRequest(exchange);
            }
        } else {
            String response = gson.toJson(manager.getAllSubtasks());
            sendText(exchange, response);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        logger.info("Обработка POST-запроса для подзадач");
        String body = new String(exchange.getRequestBody().readAllBytes());
        logger.info("Получен POST-тело: " + body);
        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            logger.info("Десериализованная подзадача: " + subtask);

            if (checkForConflict(exchange, subtask)) {
                return;
            }

            if (subtask.getId() == 0 || manager.getSubtaskById(subtask.getId()) == null) {
                manager.createSubtask(subtask);
                sendResponse(exchange, 201, "Подзадача создана");
            } else {
                manager.updateSubtask(subtask);
                sendText(exchange, "Подзадача обновлена");
            }
        } catch (Exception e) {
            logger.severe("Ошибка обработки POST-запроса: " + e.getMessage());
            sendBadRequest(exchange);
        }
    }


    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        logger.info("Обработка DELETE-запроса для подзадач");
        String[] uriParts = exchange.getRequestURI().getPath().split("/");
        if (uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                Subtask subtask = manager.getSubtaskById(id);
                if (subtask == null) {
                    sendNotFound(exchange);
                    return;
                }
                manager.deleteSubtaskById(id);
                sendText(exchange, "Подзадача удалена");
            } catch (NumberFormatException e) {
                logger.severe("Неверный формат ID в DELETE-запросе: " + e.getMessage());
                sendBadRequest(exchange);
            }
        } else {
            manager.deleteAllSubtasks();
            sendText(exchange, "Все подзадачи удалены");
        }
    }
}
