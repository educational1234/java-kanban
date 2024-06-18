package main.managers;

import main.managers.HistoryManager;
import main.managers.Managers;
import main.models.Task;
import main.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        // Создаем задачу для теста
        Task task = new Task("Тестовая задача", "Описание задачи", 0, TaskStatus.NEW);
        historyManager.add(task);

        // Получаем историю просмотров
        final List<Task> history = historyManager.getHistory();

        // Проверяем, что история не пуста
        assertNotNull(history, "История не возвращена.");
        // Проверяем, что размер истории равен 1
        assertEquals(1, history.size(), "Неверный размер истории.");
        // Проверяем, что добавленная задача соответствует задаче в истории
        assertEquals(task, history.get(0), "Задачи не совпадают.");
    }

    @Test
    void addMultipleTasks() {
        // Создаем несколько задач для теста
        Task task1 = new Task("Задача 1", "Описание задачи 1", 1, TaskStatus.IN_PROGRESS);
        Task task2 = new Task("Задача 2", "Описание задачи 2", 2, TaskStatus.DONE);

        // Добавляем задачи в историю
        historyManager.add(task1);
        historyManager.add(task2);

        // Получаем текущую историю просмотров
        final List<Task> history = historyManager.getHistory();

        // Проверяем, что история не пуста
        assertNotNull(history, "История не возвращена.");
        // Проверяем, что размер истории равен 2
        assertEquals(2, history.size(), "Неверный размер истории.");

        // Проверяем порядок (самая последняя задача должна быть первой)
        assertEquals(task1, history.get(0), "Задачи не совпадают.");
        assertEquals(task2, history.get(1), "Задачи не совпадают.");
    }

    @Test
    void addMoreThanLimit() {
        // Добавляем более 10 задач
        for (int i = 1; i <= 15; i++) {
            Task task = new Task("Задача " + i, "Описание задачи " + i, i, TaskStatus.NEW);
            historyManager.add(task);
        }

        // Получаем текущую историю просмотров
        final List<Task> history = historyManager.getHistory();

        // Проверяем, что история не пуста
        assertNotNull(history, "История не возвращена.");
        // Проверяем, что размер истории равен 10 (максимальное ограничение)
        assertEquals(10, history.size(), "Неверный размер истории.");

        // Проверяем порядок (самая последняя задача должна быть первой)
        assertEquals("Задача 6", history.get(0).getTitle(), "Задачи не совпадают.");
        assertEquals("Задача 15", history.get(9).getTitle(), "Задачи не совпадают.");
    }
}