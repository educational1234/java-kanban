package main.managers;

import main.models.Epic;
import main.models.Subtask;
import main.models.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int currentId = 0;

    private int generateId() {
        return ++currentId;
    }

    protected final TreeSet<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if (task1.getStartTime() == null && task2.getStartTime() == null) return 0;
        if (task1.getStartTime() == null) return 1;
        if (task2.getStartTime() == null) return -1;
        return task1.getStartTime().compareTo(task2.getStartTime());
    });

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.values().forEach(task -> {
            historyManager.remove(task.getId());
            prioritizedTasks.remove(task);
        });
        tasks.clear();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public int createTask(Task task) {
        if (!isTaskOverlapping(task)) {
            task.setId(generateId());
            tasks.put(task.getId(), task);
            prioritizedTasks.add(task);
            return task.getId();
        } else {
            throw new IllegalArgumentException("Задача пересекается с другой задачей.");
        }
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            if (!isTaskOverlapping(task)) {
                prioritizedTasks.remove(tasks.get(task.getId()));
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            } else {
                throw new IllegalArgumentException("Задача пересекается с другой задачей.");
            }
        }
    }

    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        epics.values().forEach(epic -> {
            epic.getSubtasks().forEach(subtask -> {
                subtasks.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtask.getId());
            });
            historyManager.remove(epic.getId());
        });
        epics.clear();
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public int createEpic(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic.getId();
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            Epic existingEpic = epics.get(epic.getId());
            existingEpic.setTitle(epic.getTitle());
            existingEpic.setDescription(epic.getDescription());
        }
    }

    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            epic.getSubtasks().forEach(subtask -> {
                subtasks.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtask.getId());
            });
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.values().forEach(subtask -> {
            historyManager.remove(subtask.getId());
            prioritizedTasks.remove(subtask);
        });
        subtasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubtasks().clear();
            updateEpicDetails(epic);
        });
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public int createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            if (!isTaskOverlapping(subtask)) {
                int id = generateId();
                subtask.setId(id);
                subtasks.put(id, subtask);
                epic.addSubtask(subtask);
                updateEpicDetails(epic);
                prioritizedTasks.add(subtask);
                return id;
            } else {
                throw new IllegalArgumentException("Подзадача пересекается с другой задачей.");
            }
        }
        return -1;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            if (!isTaskOverlapping(subtask)) {
                prioritizedTasks.remove(subtasks.get(subtask.getId()));
                subtasks.put(subtask.getId(), subtask);
                Epic epic = epics.get(subtask.getEpicId());
                if (epic != null) {
                    epic.updateSubtask(subtask);
                    updateEpicDetails(epic);
                }
                prioritizedTasks.add(subtask);
            } else {
                throw new IllegalArgumentException("Подзадача пересекается с другой задачей.");
            }
        }
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.remove(id);
        if (subtask != null) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.removeSubtask(subtask);
                updateEpicDetails(epic);
            }
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
        }
    }

    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic != null) {
            return epic.getSubtasks().stream().collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    void updateEpicDetails(Epic epic) {
        Duration totalDuration = epic.getSubtasks().stream().map(Subtask::getDuration).filter(Objects::nonNull).reduce(Duration.ZERO, Duration::plus);
        epic.setDuration(totalDuration);

        LocalDateTime startTime = epic.getSubtasks().stream().map(Subtask::getStartTime).filter(Objects::nonNull).min(LocalDateTime::compareTo).orElse(null);
        epic.setStartTime(startTime);

        LocalDateTime endTime = epic.getSubtasks().stream().map(Subtask::getEndTime).filter(Objects::nonNull).max(LocalDateTime::compareTo).orElse(null);
        epic.setEndTime(endTime);
    }


    private boolean isTaskOverlapping(Task newTask) {
        return prioritizedTasks.stream()
                .filter(existingTask -> existingTask.getId() != newTask.getId()) // Исключаем проверку самой задачи
                .anyMatch(existingTask -> {
                    LocalDateTime newTaskStart = newTask.getStartTime();
                    LocalDateTime newTaskEnd = newTask.getEndTime();
                    LocalDateTime existingTaskStart = existingTask.getStartTime();
                    LocalDateTime existingTaskEnd = existingTask.getEndTime();

                    return newTaskStart != null && newTaskEnd != null && existingTaskStart != null
                            && existingTaskEnd != null
                            &&
                            ((newTaskStart.isBefore(existingTaskEnd) || newTaskStart.isEqual(existingTaskEnd)) &&
                                    (newTaskEnd.isAfter(existingTaskStart) || newTaskEnd.isEqual(existingTaskStart)));
                });
    }
}
