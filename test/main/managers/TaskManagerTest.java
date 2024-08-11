package main.managers;

import main.enums.TaskStatus;
import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    abstract void setUp();

    @Test
    void addNewTask() {
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.now());

        final int taskId = taskManager.createTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description 1", 1, TaskStatus.NEW);

        final int epicId = taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Epic 1", "Epic Description 1", 1, TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description 1", 2, TaskStatus.NEW, epicId);
        subtask.setDuration(Duration.ofMinutes(90));
        subtask.setStartTime(LocalDateTime.now());

        final int subtaskId = taskManager.createSubtask(subtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.now());
        final int taskId = taskManager.createTask(task);

        Task updatedTask = new Task("Updated Task", "Updated Description", taskId, TaskStatus.IN_PROGRESS);
        updatedTask.setDuration(Duration.ofMinutes(120));
        updatedTask.setStartTime(LocalDateTime.now().plusHours(1));

        taskManager.updateTask(updatedTask);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertEquals(updatedTask, savedTask, "Задачи не совпадают после обновления.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Epic 1", "Epic Description 1", 1, TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);

        Epic updatedEpic = new Epic("Updated Epic", "Updated Epic Description", epicId, TaskStatus.IN_PROGRESS);

        taskManager.updateEpic(updatedEpic);
        final Epic savedEpic = taskManager.getEpicById(epicId);

        assertEquals(updatedEpic, savedEpic, "Эпики не совпадают после обновления.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Epic 1", "Epic Description 1", 1, TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description 1", 2, TaskStatus.NEW, epicId);
        subtask.setDuration(Duration.ofMinutes(90));
        subtask.setStartTime(LocalDateTime.now());
        final int subtaskId = taskManager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Updated Subtask", "Updated Subtask Description", subtaskId, TaskStatus.DONE, epicId);
        updatedSubtask.setDuration(Duration.ofMinutes(120));
        updatedSubtask.setStartTime(LocalDateTime.now().plusHours(2));

        taskManager.updateSubtask(updatedSubtask);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertEquals(updatedSubtask, savedSubtask, "Подзадачи не совпадают после обновления.");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        task.setDuration(Duration.ofMinutes(60));
        task.setStartTime(LocalDateTime.now());
        final int taskId = taskManager.createTask(task);

        taskManager.deleteTaskById(taskId);
        assertNull(taskManager.getTaskById(taskId), "Задача не удалена.");
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Epic 1", "Epic Description 1", 1, TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);

        taskManager.deleteEpicById(epicId);
        assertNull(taskManager.getEpicById(epicId), "Эпик не удален.");
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic("Epic 1", "Epic Description 1", 1, TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask Description 1", 2, TaskStatus.NEW, epicId);
        subtask.setDuration(Duration.ofMinutes(90));
        subtask.setStartTime(LocalDateTime.now());
        final int subtaskId = taskManager.createSubtask(subtask);

        taskManager.deleteSubtaskById(subtaskId);
        assertNull(taskManager.getSubtaskById(subtaskId), "Подзадача не удалена.");
    }

    @Test
    void getHistory() {
        // Создаем задачу с уникальным временем начала
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        taskManager.createTask(task);

        // Создаем эпик
        Epic epic = new Epic("Epic 1", "Epic Description 1", 2, TaskStatus.NEW);
        taskManager.createEpic(epic);

        // Создаем подзадачу с временем начала, которое не пересекается с задачей
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description 1", 3, TaskStatus.NEW, epic.getId(), Duration.ofMinutes(90), task.getEndTime().plusMinutes(1));
        taskManager.createSubtask(subtask);

        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        final List<Task> history = taskManager.getHistory();

        assertEquals(3, history.size(), "История не возвращена корректно.");
        assertEquals(task, history.get(0), "Первый элемент истории не совпадает.");
        assertEquals(epic, history.get(1), "Второй элемент истории не совпадает.");
        assertEquals(subtask, history.get(2), "Третий элемент истории не совпадает.");
    }

    @Test
    void checkEpicStatusCalculation() {
        Epic epic = new Epic("Epic 1", "Epic Description 1", 1, TaskStatus.NEW);
        int epicId = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", 2, TaskStatus.NEW, epicId);
        subtask1.setDuration(Duration.ofMinutes(60));
        subtask1.setStartTime(LocalDateTime.now());

        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description 2", 3, TaskStatus.DONE, epicId);
        subtask2.setDuration(Duration.ofMinutes(90));
        subtask2.setStartTime(subtask1.getEndTime().plusMinutes(1));  // Время начала subtask2 после окончания subtask1

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        epic = taskManager.getEpicById(epicId);
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus(), "Статус эпика рассчитан неверно.");
    }

    @Test
    void testTaskTimeOverlap() {
        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        task1.setDuration(Duration.ofMinutes(60));
        task1.setStartTime(LocalDateTime.now());
        taskManager.createTask(task1);

        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.NEW);
        task2.setDuration(Duration.ofMinutes(60));
        task2.setStartTime(task1.getStartTime().plusMinutes(30));  // Пересекается с task1

        assertThrows(IllegalArgumentException.class, () -> taskManager.createTask(task2), "Пересечение задач не должно быть допустимым.");
    }
}
