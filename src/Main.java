import managers.TaskManager;
import models.Epic;
import models.Subtask;
import models.Task;
import enums.TaskStatus;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new TaskManager();

        // Создание задач
        Task task1 = new Task("Task 1", "Description 1", 0, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 0, TaskStatus.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        // Создание эпиков
        Epic epic1 = new Epic("Epic 1", "Description Epic 1", 0, TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Description Epic 2", 0, TaskStatus.NEW);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", 0, TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", 0, TaskStatus.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description Subtask 3", 0, TaskStatus.NEW, epic2.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createSubtask(subtask3);

        // Вывод всех задач
        System.out.println("Tasks:");
        System.out.println(manager.getAllTasks());

        // Вывод всех эпиков
        System.out.println("Epics:");
        System.out.println(manager.getAllEpics());

        // Вывод всех подзадач
        System.out.println("Subtasks:");
        System.out.println(manager.getAllSubtasks());

        // Обновление статуса задачи и подзадачи
        task1.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateTask(task1);
        subtask1.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subtask1);

        // Вывод обновленных задач
        System.out.println("Updated Tasks:");
        System.out.println(manager.getAllTasks());

        // Вывод обновленных эпиков
        System.out.println("Updated Epics:");
        System.out.println(manager.getAllEpics());

        // Удаление задачи и эпика
        manager.deleteTaskById(task1.getId());
        manager.deleteEpicById(epic1.getId());

        // Вывод оставшихся задач и эпиков
        System.out.println("Remaining Tasks:");
        System.out.println(manager.getAllTasks());

        System.out.println("Remaining Epics:");
        System.out.println(manager.getAllEpics());
    }
}
