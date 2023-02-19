package tasks;

import enums.TaskType;
import enums.CurrentStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private List<Long> subtaskIds;//Каждый эпик знает, какие подзадачи в него входят
    private LocalDateTime endTime;

    public Epic(String header, String description) {
        super(header, description, CurrentStatus.NEW, null, 0);
        this.taskType = TaskType.EPIC;
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(long id, String header, CurrentStatus status, String description, LocalDateTime startTime,
                LocalDateTime endTime, Duration durationInMinutes) {
        super(id, header, status, description, startTime, durationInMinutes);
        this.taskType = TaskType.EPIC;
        this.subtaskIds = new ArrayList<>();
        this.endTime = endTime;
    }

    public List<Long> getSubtaskIds() {
        return subtaskIds;
    }

    public void setSubtaskIds(List<Long> subtaskIds) {
        this.subtaskIds = subtaskIds;
    }

    public void addIdSubtaskIdList(long id) {//добавление ID сабтасков в список эпика
        this.subtaskIds.add(id);
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIds=" + subtaskIds +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", duration=" + durationInMinutes +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtaskIds, epic.subtaskIds) && Objects.equals(endTime, epic.endTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtaskIds, endTime);
    }
}
