package tasks;

import enums.TaskType;
import enums.CurrentStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private long epicId;//Для каждой подзадачи известно, в рамках какого эпика она выполняется

    public Subtask(long epicId, String header, String description, CurrentStatus status,
                   LocalDateTime startTime, int durationInMinutes) {
        super(header, description, status, startTime, durationInMinutes);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }

    public Subtask(long id, String header, CurrentStatus status, String description,
                   LocalDateTime startTime, Duration durationInMinutes, long epicId) {
        super(id, header, status, description, startTime, durationInMinutes);
        this.taskType = TaskType.SUBTASK;
        this.epicId = epicId;
    }


    public long getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicId=" + epicId +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", startTime=" + startTime +
                ", duration=" + durationInMinutes +
                ", id=" + id +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return epicId == subtask.epicId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicId);
    }
}
