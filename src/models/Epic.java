package models;


import enums.TaskStatus;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;

    public Epic(String title, String description, int id, TaskStatus status) {
        super(title, description, id, status);
        this.subtasks = new ArrayList<>();
    }

    public List<Subtask> getSubtasks() {

        return subtasks;
    }

    // Метод для добавления подзадачи
    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateStatus();
    }

    // Метод для удаления подзадачи
    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateStatus();
    }

    public void updateSubtask(Subtask subtask) {
        for (int i = 0; i < subtasks.size(); i++) {
            if (subtasks.get(i).getId() == subtask.getId()) {
                subtasks.set(i, subtask);
                return;
            }
        }
    }

    // Метод для обновления статуса эпика на основе статусов подзадач
    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
        } else {
            boolean allDone = true;
            boolean allNew = true;

            for (Subtask subtask : subtasks) {
                if (subtask.getStatus() != TaskStatus.DONE) {
                    allDone = false;
                }
                if (subtask.getStatus() != TaskStatus.NEW) {
                    allNew = false;
                }
            }

            if (allDone) {
                setStatus(TaskStatus.DONE);
            } else if (allNew) {
                setStatus(TaskStatus.NEW);
            } else {
                setStatus(TaskStatus.IN_PROGRESS);
            }
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}