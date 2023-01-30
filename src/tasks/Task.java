package tasks;

import enums.TaskType;
import enums.CurrentStatus;

public class Task {
    protected TaskType taskType;
    protected String header;
    protected String description;
    protected long id;
    protected CurrentStatus status;

    public Task(String header, String description, CurrentStatus status) {
        this.taskType = TaskType.TASK;
        this.header = header;
        this.description = description;
        this.status = status;
    }

    public Task(long id, String header, CurrentStatus status, String description) {
        this.id = id;
        this.taskType = TaskType.TASK;
        this.header = header;
        this.status = status;
        this.description = description;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public String toString() {
        return "AllTasks.Task{" +
                "header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
