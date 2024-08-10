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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    // Сохранение текущего состояния менеджера в файл
    protected void save() {
        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath())) {
            writer.write("id,type,name,status,description,duration,startTime,epic\n");
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

    // Реализация методов toString/fromString для преобразования задач в строку и обратно
    private static String toString(Task task) {
        String duration = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        String startTime = task.getStartTime() != null ? task.getStartTime().toString() : "";
        if (task instanceof Subtask) {
            Subtask subtask = (Subtask) task;
            return String.format("%d,%s,%s,%s,%s,%s,%s,%d",
                    subtask.getId(), TaskType.SUBTASK, subtask.getTitle(), subtask.getStatus(), subtask.getDescription(),
                    duration, startTime, subtask.getEpicId());
        } else if (task instanceof Epic) {
            return String.format("%d,%s,%s,%s,%s,%s,%s,",
                    task.getId(), TaskType.EPIC, task.getTitle(), task.getStatus(), task.getDescription(),
                    duration, startTime);
        } else {
            return String.format("%d,%s,%s,%s,%s,%s,%s,",
                    task.getId(), TaskType.TASK, task.getTitle(), task.getStatus(), task.getDescription(),
                    duration, startTime);
        }
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");

        if (fields.length < 5) {
            throw new ManagerLoadException("Некорректная строка задачи: " + value);
        }

        int id = Integer.parseInt(fields[0]);
        String type = fields[1];
        String title = fields[2];
        TaskStatus status = TaskStatus.valueOf(fields[3]);
        String description = fields[4];
        Duration duration = fields.length > 5 && !fields[5].isEmpty() ? Duration.ofMinutes(Long.parseLong(fields[5])) : null;
        LocalDateTime startTime = fields.length > 6 && !fields[6].isEmpty() ? LocalDateTime.parse(fields[6]) : null;

        switch (type) {
            case "TASK":
                Task task = new Task(title, description, id, status);
                task.setDuration(duration);
                task.setStartTime(startTime);
                return task;
            case "EPIC":
                Epic epic = new Epic(title, description, id, status);
                epic.setDuration(duration);
                epic.setStartTime(startTime);
                return epic;
            case "SUBTASK":
                int epicId;
                try {
                    epicId = Integer.parseInt(fields[7]);
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    throw new ManagerLoadException("Некорректный ID эпика в подзадаче: " + value, e);
                }
                Subtask subtask = new Subtask(title, description, id, status, epicId);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                return subtask;
            default:
                throw new ManagerLoadException("Неизвестный тип задачи: " + type);
        }
    }

    // Реализация метода загрузки из файла
    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            int maxId = 0;

            for (int i = 1; i < lines.size(); i++) { // Пропускаем заголовок
                String line = lines.get(i);

                // Проверка на пустую строку
                if (line.trim().isEmpty()) {
                    continue; // Пропускаем пустые строки
                }

                try {
                    Task task = fromString(line);
                    maxId = Math.max(maxId, task.getId());

                    if (task instanceof Epic) {
                        manager.epics.put(task.getId(), (Epic) task);
                    } else if (task instanceof Subtask) {
                        manager.subtasks.put(task.getId(), (Subtask) task);
                    } else {
                        manager.tasks.put(task.getId(), task);
                    }
                } catch (Exception e) {
                    // Если строка не может быть обработана, выбрасываем исключение
                    throw new ManagerLoadException("Ошибка загрузки задачи из строки: " + line, e);
                }
            }

            // Восстановление подзадач в эпиках
            for (Subtask subtask : manager.subtasks.values()) {
                Epic epic = manager.epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.addSubtask(subtask);
                } else {
                    throw new ManagerLoadException("Эпик с ID " + subtask.getEpicId() + " для подзадачи с ID " + subtask.getId() + " не найден.");
                }
            }

            manager.currentId = maxId + 1;

        } catch (IOException e) {
            throw new ManagerLoadException("Ошибка чтения файла задач", e);
        }

        return manager;
    }

    // Проверка пересечения задач по времени
    private boolean isTimeConflict(Task newTask) {
        List<Task> tasks = getAllTasks();
        List<Subtask> subtasks = getAllSubtasks();

        return tasks.stream().anyMatch(existingTask -> hasTimeConflict(existingTask, newTask))
                || subtasks.stream().anyMatch(existingSubtask -> hasTimeConflict(existingSubtask, newTask));
    }

    private boolean hasTimeConflict(Task existingTask, Task newTask) {
        if (existingTask.getStartTime() == null || newTask.getStartTime() == null) {
            return false; // Нет времени, нет конфликта
        }
        LocalDateTime existingStart = existingTask.getStartTime();
        LocalDateTime existingEnd = existingStart.plus(existingTask.getDuration());
        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newStart.plus(newTask.getDuration());

        return newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);
    }

    // Override
    @Override
    public int createTask(Task task) {
        if (isTimeConflict(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }
        int id = super.createTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (isTimeConflict(task)) {
            throw new IllegalArgumentException("Задача пересекается по времени с другой задачей.");
        }
        super.updateTask(task);
        save();
    }

    @Override
    public int createSubtask(Subtask subtask) {
        if (isTimeConflict(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей или подзадачей.");
        }
        int id = super.createSubtask(subtask);
        save();
        return id;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (isTimeConflict(subtask)) {
            throw new IllegalArgumentException("Подзадача пересекается по времени с другой задачей или подзадачей.");
        }
        super.updateSubtask(subtask);
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
