package main.managers;

import main.models.Task;
import main.models.Epic;
import main.models.Subtask;
import main.enums.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Тест добавления новой задачи", "Описание новой задачи", 0, TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Список задач не возвращен.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addNewEpic() {
        Epic epic = new Epic("Тест добавления нового эпика", "Описание нового эпика", 0, TaskStatus.NEW);
        taskManager.createEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Список эпиков не возвращен.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void addNewSubtask() {
        Epic epic = new Epic("Тестовый эпик для подзадачи", "Описание эпика", 0, TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тест добавления новой подзадачи", "Описание новой подзадачи", 0, TaskStatus.NEW, epic.getId());
        final int subtaskId = taskManager.createSubtask(subtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtasks();

        assertNotNull(subtasks, "Список подзадач не возвращен.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void updateTask() {
        Task task = new Task("Тест добавления новой задачи", "Описание новой задачи", 0, TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);

        Task updatedTask = new Task("Обновленная задача", "Обновленное описание задачи", taskId, TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(updatedTask, savedTask, "Задачи не совпадают после обновления.");
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Тест добавления нового эпика", "Описание нового эпика", 0, TaskStatus.NEW);
        taskManager.createEpic(epic);

        Epic updatedEpic = new Epic("Обновленный эпик", "Обновленное описание эпика", epic.getId(), TaskStatus.IN_PROGRESS);
        taskManager.updateEpic(updatedEpic);

        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Эпик не найден.");
        assertEquals(updatedEpic, savedEpic, "Эпики не совпадают после обновления.");
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Тестовый эпик для подзадачи", "Описание эпика", 0, TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тест добавления новой подзадачи", "Описание новой подзадачи", 0, TaskStatus.NEW, epic.getId());
        final int subtaskId = taskManager.createSubtask(subtask);

        Subtask updatedSubtask = new Subtask("Обновленная подзадача", "Обновленное описание подзадачи", subtaskId, TaskStatus.DONE, epic.getId());
        taskManager.updateSubtask(updatedSubtask);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(updatedSubtask, savedSubtask, "Подзадачи не совпадают после обновления.");
    }

    @Test
    void deleteTaskById() {
        Task task = new Task("Тест добавления новой задачи", "Описание новой задачи", 0, TaskStatus.NEW);
        final int taskId = taskManager.createTask(task);
        taskManager.deleteTaskById(taskId);

        final Task savedTask = taskManager.getTaskById(taskId);
        assertNull(savedTask, "Задача не удалена.");
    }

    @Test
    void deleteEpicById() {
        Epic epic = new Epic("Тест добавления нового эпика", "Описание нового эпика", 0, TaskStatus.NEW);
        taskManager.createEpic(epic);
        taskManager.deleteEpicById(epic.getId());

        final Epic savedEpic = taskManager.getEpicById(epic.getId());
        assertNull(savedEpic, "Эпик не удален.");
    }

    @Test
    void deleteSubtaskById() {
        Epic epic = new Epic("Тестовый эпик для подзадачи", "Описание эпика", 0, TaskStatus.NEW);
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Тест добавления новой подзадачи", "Описание новой подзадачи", 0, TaskStatus.NEW, epic.getId());
        final int subtaskId = taskManager.createSubtask(subtask);
        taskManager.deleteSubtaskById(subtaskId);

        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNull(savedSubtask, "Подзадача не удалена.");
    }


    @Test
    void getHistory() {
        Task task = new Task("Тестовая задача", "Описание задачи", 0, TaskStatus.NEW);
        taskManager.createTask(task);

        Epic epic = new Epic("Тестовый эпик", "Описание эпика", 0, TaskStatus.NEW);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Тестовая подзадача", "Описание подзадачи", 0, TaskStatus.NEW, epic.getId());
        taskManager.createSubtask(subtask);

        // Получаем задачи по их идентификаторам, чтобы добавить их в историю просмотров
        taskManager.getTaskById(task.getId());
        taskManager.getEpicById(epic.getId());
        taskManager.getSubtaskById(subtask.getId());

        final List<Task> history = taskManager.getHistory();

        assertNotNull(history, "История не возвращена.");
        assertEquals(3, history.size(), "Неверный размер истории.");
        assertEquals(task, history.get(0), "Первый элемент истории не совпадает.");
        assertEquals(epic, history.get(1), "Второй элемент истории не совпадает.");
        assertEquals(subtask, history.get(2), "Третий элемент истории не совпадает.");
    }

    // Реализация тестов ТЗ 6
    @Test
    public void testAddAndGetTask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        taskManager.createTask(task);

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task, retrievedTask);
    }

    @Test
    public void testRemoveTask() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        taskManager.createTask(task);
        taskManager.deleteTaskById(task.getId());

        Task retrievedTask = taskManager.getTaskById(task.getId());
        assertNull(retrievedTask);
        List<Task> history = taskManager.getHistory();
        assertFalse(history.contains(task));
    }

    @Test
    public void testGetHistory() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.IN_PROGRESS);
        taskManager.createTask(task1);
        taskManager.createTask(task2);

        taskManager.getTaskById(task1.getId());
        taskManager.getTaskById(task2.getId());
        taskManager.getTaskById(task1.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task2, history.get(0)); // Task 2 should be first as it was added to history after task 1
        assertEquals(task1, history.get(1)); // Task 1 should be last as it was added again after task 2
    }

    @Test
    public void testRemoveSubtaskUpdatesEpic() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Epic epic = new Epic("Epic 1", "Epic Description", 1, TaskStatus.NEW);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", 2, TaskStatus.NEW, epic.getId());
        taskManager.createEpic(epic);
        taskManager.createSubtask(subtask);

        taskManager.deleteSubtaskById(subtask.getId());

        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertFalse(epic.getSubtasks().contains(subtask));
    }

    @Test
    public void testUpdateTaskFields() {
        TaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        taskManager.createTask(task);

        task.setTitle("Updated Task 1");
        task.setDescription("Updated Description 1");
        taskManager.updateTask(task); // Используем updateTask для обновления задачи

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals("Updated Task 1", updatedTask.getTitle());
        assertEquals("Updated Description 1", updatedTask.getDescription());
    }
}
