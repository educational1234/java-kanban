package main.managers;

import main.enums.TaskStatus;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class FileBackedTaskManagerExceptionTest {
    Duration duration = Duration.ofMinutes(60); // Например, 60 минут
    LocalDateTime startTime = LocalDateTime.of(2023, 8, 1, 10, 0); // Например, 1 августа 2023 года, 10:00

    @TempDir
    File tempDir;

    private FileBackedTaskManager manager;

    @BeforeEach
    void setUp() {
        File tempFile = new File(tempDir, "tasks.csv");
        manager = new FileBackedTaskManager(tempFile);
    }


    @Test
    void testLoadException() {
        // Создаем некорректный файл, который не может быть правильно обработан
        File invalidFile = new File(tempDir, "invalid_tasks.csv");
        try {
            Files.writeString(invalidFile.toPath(), "1,TASK,Task 1,NEW"); // Некорректная строка, так как не хватает полей
        } catch (IOException e) {
            throw new RuntimeException("Не удалось создать некорректный файл для теста.", e);
        }
    }


    @Test
    void testNoExceptionOnValidSaveAndLoad() {
        File tempFile = new File(tempDir, "valid_tasks.csv");
        FileBackedTaskManager validManager = new FileBackedTaskManager(tempFile);

        // Добавляем валидные данные
        Task task = new Task("Task 1", "Description 1", 1, TaskStatus.NEW, Duration.ofMinutes(60), LocalDateTime.now());
        validManager.createTask(task);

        // Проверяем, что метод save() не вызывает исключений
        assertDoesNotThrow(validManager::save, "Сохранение валидных данных не должно приводить к исключению.");

        // Проверяем, что метод loadFromFile() не вызывает исключений
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(tempFile), "Загрузка валидных данных не должна приводить к исключению.");
    }
}
