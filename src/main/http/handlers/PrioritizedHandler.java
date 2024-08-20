package main.http.handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import main.managers.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    protected void handleGet(HttpExchange exchange) throws IOException {
        String response = gson.toJson(manager.getPrioritizedTasks());
        sendText(exchange, response);
    }
}
