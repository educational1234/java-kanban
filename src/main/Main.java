package main;

import main.enums.TaskStatus;
import main.managers.*;
import main.models.*;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager(); // Используем InMemoryTaskManager как пример

        // Создание задач
        Task task1 = new Task("Task 1", "Description 1", 0, TaskStatus.NEW);
        Task task2 = new Task("Task 2", "Description 2", 1, TaskStatus.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        // Создание эпиков
        Epic epic1 = new Epic("Epic 1", "Description Epic 1", 0, TaskStatus.NEW);
        Epic epic2 = new Epic("Epic 2", "Description Epic 2", 1, TaskStatus.NEW);
        manager.createEpic(epic1);
        manager.createEpic(epic2);

        // Создание подзадач
        Subtask subtask1 = new Subtask("Subtask 1", "Description Subtask 1", 0, TaskStatus.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Description Subtask 2", 1, TaskStatus.NEW, epic1.getId());
        Subtask subtask3 = new Subtask("Subtask 3", "Description Subtask 3", 2, TaskStatus.NEW, epic2.getId());
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

        // Дополнительное задание
        // Создаём две задачи
        Task task3 = new Task("Task 3", "Description 3", 2, TaskStatus.NEW);
        Task task4 = new Task("Task 4", "Description 4", 3, TaskStatus.IN_PROGRESS);
        manager.createTask(task3);
        manager.createTask(task4);

        // Создаём эпик с тремя подзадачами
        Epic epicWithSubtasks = new Epic("Epic 3", "Epic with subtasks", 2, TaskStatus.NEW);
        manager.createEpic(epicWithSubtasks);
        Subtask subtask4 = new Subtask("Subtask 4", "Description Subtask 4", 3, TaskStatus.NEW, epicWithSubtasks.getId());
        Subtask subtask5 = new Subtask("Subtask 5", "Description Subtask 5", 4, TaskStatus.NEW, epicWithSubtasks.getId());
        Subtask subtask6 = new Subtask("Subtask 6", "Description Subtask 6", 5, TaskStatus.NEW, epicWithSubtasks.getId());
        manager.createSubtask(subtask4);
        manager.createSubtask(subtask5);
        manager.createSubtask(subtask6);

        // Создаём эпик без подзадач
        Epic epicWithoutSubtasks = new Epic("Epic 4", "Epic without subtasks", 3, TaskStatus.NEW);
        manager.createEpic(epicWithoutSubtasks);

        // Запрашиваем созданные задачи несколько раз в разном порядке
        manager.getTaskById(task3.getId());
        manager.getTaskById(task4.getId());
        manager.getTaskById(epicWithSubtasks.getId());
        manager.getTaskById(subtask4.getId());
        manager.getTaskById(subtask5.getId());
        manager.getTaskById(subtask6.getId());
        manager.getTaskById(epicWithoutSubtasks.getId());
        manager.getTaskById(task3.getId());
        manager.getTaskById(epicWithSubtasks.getId());

        // Выводим историю и убеждаемся, что в ней нет повторов
        printHistory(manager);

        // Удаляем задачу, которая есть в истории, и проверяем, что при печати она не будет выводиться
        manager.deleteTaskById(task3.getId());
        printHistory(manager);

        // Удаляем эпик с тремя подзадачами и проверяем, что из истории удалился как сам эпик, так и все его подзадачи
        manager.deleteEpicById(epicWithSubtasks.getId());
        printHistory(manager);
    }

    private static void printHistory(TaskManager manager) {
        List<Task> history = manager.getHistory();
        System.out.println("История просмотров:");
        for (Task task : history) {
            System.out.println(task);
        }
        System.out.println();
    }
}
