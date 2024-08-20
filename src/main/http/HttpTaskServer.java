package main.http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import main.adapters.GsonProvider;
import main.http.handlers.*;
import main.managers.Managers;
import main.managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.gson = GsonProvider.getGson();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        initContexts();
    }

    private void initContexts() {
        server.createContext("/tasks", new TasksHandler(manager, gson));
        server.createContext("/subtasks", new SubtasksHandler(manager, gson));
        server.createContext("/epics", new EpicsHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public void start() {
        try {
            server.start();
            System.out.println("Сервер запущен на порту " + PORT);
        } catch (Exception e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен");
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}
