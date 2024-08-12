package main.managers;

import main.enums.TaskStatus;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void testEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой.");
    }

    @Test
    void testAddAndRemoveHistory() {
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.now());
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Задача должна быть добавлена в историю.");

        historyManager.remove(task.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "Задача должна быть удалена из истории.");
    }

    @Test
    void testDuplicateInHistory() {
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.now());
        historyManager.add(task);
        historyManager.add(task);

        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size(), "Дубликаты задач не должны добавляться в историю.");
    }

    @Test
    void testRemoveFromHistoryBeginningMiddleEnd() {
        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.IN_PROGRESS);
        Task task3 = new Task("Task 3", "Description 3", 3, TaskStatus.DONE);

        task1.setDuration(Duration.ofMinutes(60));
        task1.setStartTime(LocalDateTime.now());

        task2.setDuration(Duration.ofMinutes(90));
        task2.setStartTime(LocalDateTime.now().plusHours(1));

        task3.setDuration(Duration.ofMinutes(120));
        task3.setStartTime(LocalDateTime.now().plusHours(2));

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Удаление из начала
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Неверное количество задач после удаления из начала.");
        assertEquals(task2, history.get(0), "Неверный порядок задач после удаления из начала.");

        // Удаление из середины
        historyManager.remove(task2.getId());
        history = historyManager.getHistory();
        assertEquals(1, history.size(), "Неверное количество задач после удаления из середины.");
        assertEquals(task3, history.get(0), "Неверный порядок задач после удаления из середины.");

        // Удаление из конца
        historyManager.remove(task3.getId());
        history = historyManager.getHistory();
        assertTrue(history.isEmpty(), "История должна быть пустой после удаления из конца.");
    }
}
