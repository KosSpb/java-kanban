package tasks;

import enums.TaskType;
import enums.CurrentStatus;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Long> subtaskIds;//Каждый эпик знает, какие подзадачи в него входят

    public Epic(String header, String description) {
        super(header, description, CurrentStatus.NEW);
        this.taskType = TaskType.EPIC;
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(long id, String header, CurrentStatus status, String description) {
        super(id, header, status, description);
        this.taskType = TaskType.EPIC;
        this.subtaskIds = new ArrayList<>();
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

    @Override
    public String toString() {
        return "AllTasks.Epic{" +
                "subtaskIdList=" + subtaskIds +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
