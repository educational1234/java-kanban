package main.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.managers.TaskManager;
import main.models.Task;

import java.io.IOException;
import java.util.logging.Logger;

public class TasksHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(TasksHandler.class.getName());

    public TasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        logger.info("Обработка GET-запроса для задач");
        String[] uriParts = exchange.getRequestURI().getPath().split("/");
        if (uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                Task task = manager.getTaskById(id);
                if (task == null) {
                    sendNotFound(exchange);
                    return;
                }
                String response = gson.toJson(task);
                sendText(exchange, response);
            } catch (NumberFormatException e) {
                logger.severe("Неверный формат ID в GET-запросе: " + e.getMessage());
                sendBadRequest(exchange);
            }
        } else {
            String response = gson.toJson(manager.getAllTasks());
            sendText(exchange, response);
        }
    }

    @Override
    protected void handlePost(HttpExchange exchange) throws IOException {
        logger.info("Обработка POST-запроса для задач");
        String body = new String(exchange.getRequestBody().readAllBytes());
        logger.info("Получен POST-тело: " + body);
        try {
            Task task = gson.fromJson(body, Task.class);
            logger.info("Десериализованная задача: " + task);

            if (checkForConflict(exchange, task)) {
                return;
            }

            if (task.getId() == 0 || manager.getTaskById(task.getId()) == null) {
                manager.createTask(task);
                sendResponse(exchange, 201, "Задача создана");
            } else {
                manager.updateTask(task);
                sendText(exchange, "Задача обновлена");
            }
        } catch (Exception e) {
            logger.severe("Ошибка обработки POST-запроса: " + e.getMessage());
            sendBadRequest(exchange);
        }
    }

    @Override
    protected void handleDelete(HttpExchange exchange) throws IOException {
        logger.info("Обработка DELETE-запроса для задач");
        String[] uriParts = exchange.getRequestURI().getPath().split("/");
        if (uriParts.length == 3) {
            try {
                int id = Integer.parseInt(uriParts[2]);
                Task task = manager.getTaskById(id);
                if (task == null) {
                    sendNotFound(exchange);
                    return;
                }
                manager.deleteTaskById(id);
                sendText(exchange, "Задача удалена");
            } catch (NumberFormatException e) {
                logger.severe("Неверный формат ID в DELETE-запросе: " + e.getMessage());
                sendBadRequest(exchange);
            }
        } else {
            manager.deleteAllTasks();
            sendText(exchange, "Все задачи удалены");
        }
    }

}
