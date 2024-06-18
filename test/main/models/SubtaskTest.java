package main.models;

import main.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubtaskTest {

    @Test
    void testSubtaskEquals() {
        // Тест эквивалентности подзадач по ID
        Subtask subtask1 = new Subtask("Подзадача 1", "Описание подзадачи 1", 1, TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Подзадача 2", "Описание подзадачи 2", 1, TaskStatus.NEW, 1);

        assertEquals(subtask1, subtask2, "Подзадачи с одинаковым id должны быть равны.");
    }


}
