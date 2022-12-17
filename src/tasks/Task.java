package tasks;

import status.CurrentStatus;

public class Task {
    protected String header;
    protected String description;
    protected long id;
    protected CurrentStatus status;

    public Task(String header, String description, CurrentStatus status) {
        this.header = header;
        this.description = description;
        this.status = status;
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
