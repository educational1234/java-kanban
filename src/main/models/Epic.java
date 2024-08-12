package main.models;

import main.enums.TaskStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {
    private final List<Subtask> subtasks;
    private LocalDateTime endTime; // Новое поле для хранения времени окончания эпика

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
                updateStatus();
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

    // Метод для расчета времени окончания эпика
    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" + "subtasks=" + subtasks + ", title='" + title + '\'' + ", description='" + description + '\'' + ", id=" + id + ", status=" + status + ", startTime=" + getStartTime() + ", endTime=" + endTime + ", duration=" + getDuration() + '}';
    }
}
