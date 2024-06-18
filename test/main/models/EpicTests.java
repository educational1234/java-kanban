package main.models;

import main.enums.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTests {

    @Test
    void testEpicEquals() {
        // Тест эквивалентности эпиков по ID
        Epic epic1 = new Epic("Эпик 1", "Описание эпика 1", 1, TaskStatus.NEW);
        Epic epic2 = new Epic("Эпик 2", "Описание эпика 2", 1, TaskStatus.NEW);

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны.");
    }


}
