package main.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import main.managers.TaskManager;
import main.models.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

public abstract class BaseHttpHandler implements HttpHandler {
    protected final TaskManager manager;
    protected final Gson gson;
    private static final Logger logger = Logger.getLogger(BaseHttpHandler.class.getName());

    protected BaseHttpHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        logger.info("Обработка запроса: " + method + " " + exchange.getRequestURI());

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    logger.warning("Метод не разрешен: " + method);
                    sendResponse(exchange, 405, "Метод не разрешен");
            }
        } catch (Exception e) {
            logger.severe("Исключение при обработке запроса: " + e.getMessage());
            e.printStackTrace();
            sendResponse(exchange, 500, "Внутренняя ошибка сервера");
        }
    }

    protected void handleGet(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Метод GET не поддерживается");
    }

    protected void handlePost(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Метод POST не поддерживается");
    }

    protected void handleDelete(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 405, "Метод DELETE не поддерживается");
    }

    //
    protected void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(200, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 404, "Ресурс не найден");
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 406, "Конфликт с существующей задачей");
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 400, "Неверный запрос");
    }

    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        logger.info("Отправка ответа со статусом " + statusCode);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    protected boolean checkForConflict(HttpExchange exchange, Task newTask) throws IOException {
        List<Task> allTasks = manager.getAllTasks();
        for (Task existingTask : allTasks) {
            if (tasksConflict(existingTask, newTask)) {
                sendHasInteractions(exchange);
                return true;
            }
        }
        return false;
    }

    private boolean tasksConflict(Task task1, Task task2) {
        return task1.getStartTime().isBefore(task2.getEndTime()) &&
                task2.getStartTime().isBefore(task1.getEndTime());
    }
}
