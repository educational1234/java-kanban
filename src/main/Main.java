package main;

import main.http.HttpTaskServer;
import main.managers.InMemoryTaskManager;
import main.managers.TaskManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager manager = new InMemoryTaskManager();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();

    }
}
