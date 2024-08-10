package main.managers;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    @Override
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }
}
