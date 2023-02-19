package tasks;

import enums.TaskType;
import enums.CurrentStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected TaskType taskType;
    protected String header;
    protected String description;
    protected long id;
    protected CurrentStatus status;
    protected Duration durationInMinutes;
    protected LocalDateTime startTime;

    public Task(String header, String description, CurrentStatus status, LocalDateTime startTime,
                int durationInMinutes) {
        this.taskType = TaskType.TASK;
        this.header = header;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.durationInMinutes = Duration.ofMinutes(durationInMinutes);
    }

    public Task(long id, String header, CurrentStatus status, String description, LocalDateTime startTime,
                Duration durationInMinutes) {
        this.id = id;
        this.taskType = TaskType.TASK;
        this.header = header;
        this.status = status;
        this.description = description;
        this.startTime = startTime;
        this.durationInMinutes = durationInMinutes;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getHeader() {
        return header;
    }

    public String getDescription() {
        return description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public CurrentStatus getStatus() {
        return status;
    }

    public void setStatus(CurrentStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() throws IllegalArgumentException{
        LocalDateTime endTime;
        try {
            endTime = startTime.plus(durationInMinutes);
        } catch (NullPointerException exception) {
            throw new IllegalArgumentException("Начальное время не указано. Расчет конечного времени невозможен.");
        }
        return endTime;
    }

    public Duration getDurationInMinutes() {
        return durationInMinutes;
    }

    public void setDurationInMinutes(Duration durationInMinutes) {
        this.durationInMinutes = durationInMinutes;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public String toString() {
        return "Task{" +
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
        Task task = (Task) o;
        return id == task.id && taskType == task.taskType && Objects.equals(header, task.header)
                && Objects.equals(description, task.description) && status == task.status
                && Objects.equals(durationInMinutes, task.durationInMinutes)
                && Objects.equals(startTime, task.startTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskType, header, description, id, status, durationInMinutes, startTime);
    }
}
