package main.managers;

import main.enums.TaskStatus;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

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
    void testLoadException() {
        // Создаем некорректный файл, который не может быть правильно обработан
        File invalidFile = new File(tempDir, "invalid_tasks.csv");
        try {
            Files.writeString(invalidFile.toPath(), "Некорректные данные\n1,TASK,Task 1,NEW,Description 1");
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать некорректный файл для теста.", e);
        }

    }

    @Test
    void testNoExceptionOnValidSaveAndLoad() {
        File tempFile = new File(tempDir, "valid_tasks.csv");
        FileBackedTaskManager validManager = new FileBackedTaskManager(tempFile);

        // Добавляем валидные данные

        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        validManager.createTask(task);

        // Проверяем, что метод save() не вызывает исключений
        assertDoesNotThrow(validManager::save, "Сохранение валидных данных не должно приводить к исключению.");

        // Проверяем, что метод loadFromFile() не вызывает исключений
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tempFile), "Загрузка валидных данных не должна приводить к исключению.");
    }


}
