package main.managers;

import main.enums.TaskStatus;
import main.enums.TaskType;
import main.exceptions.ManagerLoadException;
import main.exceptions.ManagerSaveException;
import main.models.Epic;
import main.models.Subtask;
import main.models.Task;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {

        this.file = file;
    }

    //save сохраняет текущее состояние менеджера в файл
    protected void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(toString(subtask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения задач в файл", e);
        }
    }

    //Реализация методов toString/fromString для преобразования задач в строку и обратно
    private static String toString(Task task) {
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%d",
                    subtask.getId(), TaskType.SUBTASK, subtask.getTitle(), subtask.getStatus(), subtask.getDescription(), subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(), TaskType.EPIC, task.getTitle(), task.getStatus(), task.getDescription());
        } else {
            return String.format("%d,%s,%s,%s,%s,",
                    task.getId(), TaskType.TASK, task.getTitle(), task.getStatus(), task.getDescription());
        }
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String title = parts[2];
        TaskStatus status = TaskStatus.valueOf(parts[3]);
        String description = parts[4];

        return switch (type) {
            case TASK -> new Task(title, description, id, status);
            case EPIC -> new Epic(title, description, id, status);
            case SUBTASK -> new Subtask(title, description, id, status, Integer.parseInt(parts[5]));
        };
    }

    //Реализация метода загрузки из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            int maxId = 0;
            for (int i = 1; i < lines.size(); i++) {
                Task task = fromString(lines.get(i));
                maxId = Math.max(maxId, task.getId());
                if (task instanceof Epic) {
                    manager.epics.put(task.getId(), (Epic) task);
                } else if (task instanceof Subtask) {
                    manager.subtasks.put(task.getId(), (Subtask) task);
                } else {
                    manager.tasks.put(task.getId(), task);
                }
            }
            // Восстановление подзадач в эпиках
            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtask(subtask);
                }
            }
            manager.currentId = maxId + 1;
        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка загрузки задачи из файла", e);
        }
        return manager;
    }


    public File getFile() {
        return file;
    }

    // Override
    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public int createEpic(Epic epic) {
        int id = super.createEpic(epic);
        save();
        return id;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

}
