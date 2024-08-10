package main.models;

import main.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskTest {

    @Test
    void testTaskEquals() {
        // Тест эквивалентности задач по ID
        Task task1 = new Task("Задача 1", "Описание задачи 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Задача 2", "Описание задачи 2", 1, TaskStatus.NEW);

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны.");
    }


}
