package tasks;

import status.CurrentStatus;

public class Subtask extends Task {
    private long epicId;//Для каждой подзадачи известно, в рамках какого эпика она выполняется

    public Subtask(long epicId, String header, String description, CurrentStatus status) {
        super(header, description, status);
        this.epicId = epicId;
    }

    public long getEpicId() {
        return epicId;
    }

    public void setEpicId(long epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "AllTasks.Subtask{" +
                "belongsToEpic='" + epicId + '\'' +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
