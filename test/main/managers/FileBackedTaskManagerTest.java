package main.managers;

import main.enums.TaskStatus;
import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    @TempDir //создает временный каталог для тестов
    File tempDir;

    @BeforeEach //создает экземпляр FileBackedTaskManager, используя временный файл
    void setUp() {
        File tempFile = new File(tempDir, "tasks.csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test //Проверяет сохранение и загрузку пустого менеджера.
    void testSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test //Проверяет создание и сохранение задач, а затем загрузку и проверку их наличия.
    void testCreateAndSaveTasks() {
        Task task1 = new Task("Task 1", "Description 1", 0, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 1, TaskStatus.NEW);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(2, tasks.size());
        assertTrue(tasks.contains(task1));
        assertTrue(tasks.contains(task2));
    }

    @Test //Проверяет создание и сохранение эпиков и подзадач, а затем загрузку и проверку их наличия.
    void testCreateAndSaveEpicsAndSubtasks() {
        Epic epic1 = new Epic("Epic 1", "Description Epic 1", 0, TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Description Epic 2", 1, TaskStatus.NEW);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", 0, TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", 1, TaskStatus.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description Subtask 3", 2, TaskStatus.NEW, epic2.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        List<Epic> epics = loadedManager.getAllEpics();
        List<Subtask> subtasks = loadedManager.getAllSubtasks();

        assertEquals(2, epics.size());
        assertTrue(epics.contains(epic1));
        assertTrue(epics.contains(epic2));

        assertEquals(3, subtasks.size());
        assertTrue(subtasks.contains(subtask1));
        assertTrue(subtasks.contains(subtask2));
        assertTrue(subtasks.contains(subtask3));
    }

    @Test //Проверяет обновление задачи и сохранение обновленного состояния.
    void testUpdateTask() {
        Task task1 = new Task("Task 1", "Description 1", 0, TaskStatus.NEW);
        manager.createTask(task1);

        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        Task loadedTask = loadedManager.getTaskById(task1.getId());

        assertEquals(TaskStatus.DONE, loadedTask.getStatus());
    }

    @Test //Проверяет удаление задачи и сохранение состояния после удаления.
    void testDeleteTask() {
        Task task1 = new Task("Task 1", "Description 1", 0, TaskStatus.NEW);
        manager.createTask(task1);

        manager.deleteTaskById(task1.getId());
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        assertNull(loadedManager.getTaskById(task1.getId()));
    }
}
