package main.managers;

import main.enums.TaskStatus;
import main.exceptions.ManagerLoadException;
import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @TempDir
    File tempDir;

    @BeforeEach
    @Override
    void setUp() {
        File tempFile = new File(tempDir, "tasks.csv");
        taskManager = new FileBackedTaskManager(tempFile);
    }

    @Test
    void testCreateNonOverlappingSubtasks() {
        Epic epic = new Epic("Epic 1", "Epic Description", 1, TaskStatus.NEW);
        taskManager.createEpic(epic);

        // Создаем две подзадачи с непересекающимся временем
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", 1, TaskStatus.NEW, epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusHours(1));
        Subtask subtask2 = new Subtask("Subtask 2", "Description 2", 2, TaskStatus.NEW, epic.getId(), Duration.ofMinutes(60), LocalDateTime.now().plusHours(2));

        // Проверяем, что подзадачи создаются без исключений
        assertDoesNotThrow(() -> taskManager.createSubtask(subtask1));
        assertDoesNotThrow(() -> taskManager.createSubtask(subtask2));
    }

    @Test
    void testLoadException() {
        // Создаем некорректный файл, который не может быть правильно обработан
        File invalidFile = new File(tempDir, "invalid_tasks.csv");
        try {
            Files.writeString(invalidFile.toPath(), "Некорректные данные\n1,TASK,Task 1,NEW,Description 1");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать некорректный файл для теста.", e);
        }
        // Ожидаем, что метод loadFromFile выбросит исключение
        assertThrows(ManagerLoadException.class, () -> FileBackedTaskManager.loadFromFile(invalidFile));

    }

    @Test
    void testNoExceptionOnValidSaveAndLoad() {
        File tempFile = new File(tempDir, "valid_tasks.csv");
        FileBackedTaskManager validManager = new FileBackedTaskManager(tempFile);

        // Добавляем валидные данные

        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Epic epic = new Epic("Epic 1", "Epic Description", 2, TaskStatus.NEW);
        Subtask subtask = new Subtask("Subtask 1", "Subtask Description", 3, TaskStatus.NEW, epic.getId());
        subtask.setDuration(Duration.ofMinutes(45));
        subtask.setStartTime(LocalDateTime.now().plusHours(1));

        validManager.createTask(task);
        validManager.createEpic(epic);
        validManager.createSubtask(subtask);

        // Проверяем, что метод save() не вызывает исключений
        assertDoesNotThrow(validManager::save, "Сохранение валидных данных не должно приводить к исключению.");

        // Проверяем, что метод loadFromFile() не вызывает исключений
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что загруженные данные совпадают с исходными
        Task loadedTask = loadedManager.getTaskById(task.getId());
        assertNotNull(loadedTask);
        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());

        Epic loadedEpic = loadedManager.getEpicById(epic.getId());
        assertNotNull(loadedEpic);
        assertEquals(epic.getTitle(), loadedEpic.getTitle());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
        assertEquals(epic.getStatus(), loadedEpic.getStatus());
        assertEquals(epic.getDuration(), loadedEpic.getDuration());
        assertEquals(epic.getStartTime(), loadedEpic.getStartTime());
        assertEquals(epic.getEndTime(), loadedEpic.getEndTime());

        Subtask loadedSubtask = loadedManager.getSubtaskById(subtask.getId());
        assertNotNull(loadedSubtask);
        assertEquals(subtask.getTitle(), loadedSubtask.getTitle());
        assertEquals(subtask.getDescription(), loadedSubtask.getDescription());
        assertEquals(subtask.getStatus(), loadedSubtask.getStatus());
        assertEquals(subtask.getEpicId(), loadedSubtask.getEpicId());
        assertEquals(subtask.getDuration(), loadedSubtask.getDuration());
        assertEquals(subtask.getStartTime(), loadedSubtask.getStartTime());
    }

    @Test
    void testPrioritizedTasksAreRestoredCorrectly() {
        File tempFile = new File(tempDir, "prioritized_tasks.csv");
        FileBackedTaskManager validManager = new FileBackedTaskManager(tempFile);

        // Создаем задачи с разными временами начала
        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(2));
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.NEW, Duration.ofMinutes(30), LocalDateTime.now().plusHours(1));
        validManager.createTask(task1);
        validManager.createTask(task2);

        // Сохраняем в файл
        validManager.save();

        // Загружаем из файла
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        // Проверяем, что задачи восстанавливаются в правильном порядке
        List<Task> prioritizedTasks = loadedManager.getPrioritizedTasks();
        assertEquals(2, prioritizedTasks.size());
        assertEquals(task2.getId(), prioritizedTasks.get(0).getId());
        assertEquals(task1.getId(), prioritizedTasks.get(1).getId());
    }

}
