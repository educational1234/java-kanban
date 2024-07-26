package main.managers;

import main.enums.TaskStatus;
import main.models.Epic;
import main.models.Subtask;
import main.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest {

    private FileBackedTaskManager manager;
    @TempDir //создает временный каталог для тестов
    File tempDir;

    @BeforeEach
        //создает экземпляр FileBackedTaskManager, используя временный файл
    void setUp() {
        File tempFile = new File(tempDir, "tasks.csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @Test
        //Проверяет сохранение и загрузку пустого менеджера.
    void testSaveAndLoadEmptyManager() {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        assertTrue(loadedManager.getAllTasks().isEmpty());
        assertTrue(loadedManager.getAllEpics().isEmpty());
        assertTrue(loadedManager.getAllSubtasks().isEmpty());
    }

    @Test
        //Проверяет создание и сохранение задач, а затем загрузку и проверку их наличия.
    void testCreateAndSaveTasks() {
        Task task1 = new Task("Task 1", "Description 1", 0, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 1, TaskStatus.NEW);
        manager.createTask(task1);
        manager.createTask(task2);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(2, tasks.size());
        Task loadedTask1 = null;
        for (Task task : tasks) {
            if (task.getId() == task1.getId()) {
                loadedTask1 = task;
                break;
            }
        }
        assertNotNull(loadedTask1);
        assertEquals(task1.getTitle(), loadedTask1.getTitle());
        assertEquals(task1.getDescription(), loadedTask1.getDescription());
        assertEquals(task1.getStatus(), loadedTask1.getStatus());

        Task loadedTask2 = null;
        for (Task task : tasks) {
            if (task.getId() == task2.getId()) {
                loadedTask2 = task;
                break;
            }
        }
        assertNotNull(loadedTask2);
        assertEquals(task2.getTitle(), loadedTask2.getTitle());
        assertEquals(task2.getDescription(), loadedTask2.getDescription());
        assertEquals(task2.getStatus(), loadedTask2.getStatus());
    }

    @Test
        //Проверяет создание и сохранение эпиков и подзадач, а затем загрузку и проверку их наличия.
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

        Epic loadedEpic1 = null;
        for (Epic epic : epics) {
            if (epic.getId() == epic1.getId()) {
                loadedEpic1 = epic;
                break;
            }
        }
        assertNotNull(loadedEpic1);
        assertEquals(epic1.getTitle(), loadedEpic1.getTitle());
        assertEquals(epic1.getDescription(), loadedEpic1.getDescription());
        assertEquals(epic1.getStatus(), loadedEpic1.getStatus());

        Epic loadedEpic2 = null;
        for (Epic epic : epics) {
            if (epic.getId() == epic2.getId()) {
                loadedEpic2 = epic;
                break;
            }
        }
        assertNotNull(loadedEpic2);
        assertEquals(epic2.getTitle(), loadedEpic2.getTitle());
        assertEquals(epic2.getDescription(), loadedEpic2.getDescription());
        assertEquals(epic2.getStatus(), loadedEpic2.getStatus());


        assertEquals(3, subtasks.size());

        Subtask loadedSubtask1 = null;
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtask1.getId()) {
                loadedSubtask1 = subtask;
                break;
            }
        }
        assertNotNull(loadedSubtask1);
        assertEquals(subtask1.getTitle(), loadedSubtask1.getTitle());
        assertEquals(subtask1.getDescription(), loadedSubtask1.getDescription());
        assertEquals(subtask1.getStatus(), loadedSubtask1.getStatus());

        Subtask loadedSubtask2 = null;
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtask2.getId()) {
                loadedSubtask2 = subtask;
                break;
            }
        }
        assertNotNull(loadedSubtask2);
        assertEquals(subtask2.getTitle(), loadedSubtask2.getTitle());
        assertEquals(subtask2.getDescription(), loadedSubtask2.getDescription());
        assertEquals(subtask2.getStatus(), loadedSubtask2.getStatus());

        Subtask loadedSubtask3 = null;
        for (Subtask subtask : subtasks) {
            if (subtask.getId() == subtask3.getId()) {
                loadedSubtask3 = subtask;
                break;
            }
        }
        assertNotNull(loadedSubtask3);
        assertEquals(subtask3.getTitle(), loadedSubtask3.getTitle());
        assertEquals(subtask3.getDescription(), loadedSubtask3.getDescription());
        assertEquals(subtask3.getStatus(), loadedSubtask3.getStatus());
    }

    @Test
        //Проверяет обновление задачи и сохранение обновленного состояния.
    void testUpdateTask() {
        Task task1 = new Task("Task 1", "Description 1", 0, TaskStatus.NEW);
        manager.createTask(task1);

        task1.setStatus(TaskStatus.DONE);
        manager.updateTask(task1);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        Task loadedTask = loadedManager.getTaskById(task1.getId());


        assertNotNull(loadedTask);
        assertEquals(task1.getId(), loadedTask.getId());
        assertEquals(task1.getTitle(), loadedTask.getTitle());
        assertEquals(task1.getDescription(), loadedTask.getDescription());
        assertEquals(task1.getStatus(), loadedTask.getStatus());
    }

    @Test
        //Проверяет удаление задачи и сохранение состояния после удаления.
    void testDeleteTask() {
        Task task1 = new Task("Task 1", "Description 1", 0, TaskStatus.NEW);
        manager.createTask(task1);

        manager.deleteTaskById(task1.getId());
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(manager.getFile());
        assertNull(loadedManager.getTaskById(task1.getId()));
    }

    @Test
    public void testLoadFromFile() {
        File tempFile = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.IN_PROGRESS);

        manager.createTask(task1);
        manager.createTask(task2);

        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loadedManager.getAllTasks();
        assertEquals(2, tasks.size());

        // Проверка task1
        Task loadedTask1 = null;
        for (Task task : tasks) {
            if (task.getId() == task1.getId()) {
                loadedTask1 = task;
                break;
            }
        }
        assertNotNull(loadedTask1);
        assertEquals(task1.getTitle(), loadedTask1.getTitle());
        assertEquals(task1.getDescription(), loadedTask1.getDescription());
        assertEquals(task1.getStatus(), loadedTask1.getStatus());

        // Проверка task2
        Task loadedTask2 = null;
        for (Task task : tasks) {
            if (task.getId() == task2.getId()) {
                loadedTask2 = task;
                break;
            }
        }
        assertNotNull(loadedTask2);
        assertEquals(task2.getTitle(), loadedTask2.getTitle());
        assertEquals(task2.getDescription(), loadedTask2.getDescription());
        assertEquals(task2.getStatus(), loadedTask2.getStatus());
    }

    @Test
    public void testDeleteTaskById() {
        File tempFile = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Task", "Description", 1, TaskStatus.NEW);
        manager.createTask(task);
        manager.save();

        manager.deleteTaskById(1);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        Task deletedTask = null;
        for (Task t : loadedManager.getAllTasks()) {
            if (t.getId() == 1) {
                deletedTask = t;
                break;
            }
        }
        assertNull(deletedTask);
    }

    @Test
    public void testDeleteAllTasks() {
        File tempFile = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task1 = new Task("Task 1", "Description 1", 1, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 2, TaskStatus.IN_PROGRESS);

        manager.createTask(task1);
        manager.createTask(task2);

        manager.save();

        manager.deleteAllTasks();
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Task> tasks = loadedManager.getAllTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testDeleteEpicById() {
        File tempFile = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Epic", "Epic Description", 1, TaskStatus.NEW);
        manager.createEpic(epic);
        manager.save();

        manager.deleteEpicById(1);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);

        Epic deletedEpic = null;
        for (Epic e : loadedManager.getAllEpics()) {
            if (e.getId() == 1) {
                deletedEpic = e;
                break;
            }
        }
        assertNull(deletedEpic);
    }

    @Test
    public void testDeleteAllEpics() {
        File tempFile = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic1 = new Epic("Epic 1", "Epic Description 1", 1, TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Epic Description 2", 2, TaskStatus.NEW);

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        manager.save();

        manager.deleteAllEpics();
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Epic> epics = loadedManager.getAllEpics();
        assertTrue(epics.isEmpty());
    }

    @Test
    public void testDeleteSubtaskById() {
        File tempFile = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Epic", "Epic Description", 1, TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask", "Subtask Description", 2, TaskStatus.NEW, 1);
        manager.createSubtask(subtask);
        manager.save();

        manager.deleteSubtaskById(2);
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Subtask> subtasks = loadedManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void testDeleteAllSubtasks() {
        File tempFile = new File(tempDir, "tasks.csv");
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Epic", "Epic Description", 1, TaskStatus.NEW);
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask Description 1", 2, TaskStatus.NEW, 1);
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask Description 2", 3, TaskStatus.NEW, 1);

        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        manager.save();

        manager.deleteAllSubtasks();
        manager.save();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        List<Subtask> subtasks = loadedManager.getAllSubtasks();
        assertTrue(subtasks.isEmpty());
    }
}
