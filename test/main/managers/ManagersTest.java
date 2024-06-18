package main.managers;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagersTest {
    @Test
    void testGetDefault() {
        // Тест проверки, что утилитарный класс возвращает проинициализированный менеджер
        assertNotNull(Managers.getDefault(), "default manager should not be null");
    }
    @Test
    void testGetDefaultHistory() {
        // Тест проверки, что утилитарный класс возвращает проинициализированный менеджер истории
        assertNotNull(Managers.getDefaultHistory(), "default history manager should not be null");
    }
}
