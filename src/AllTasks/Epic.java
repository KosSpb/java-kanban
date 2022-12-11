package AllTasks;

import Status.CurrentStatus;

import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    private List<Long> subtaskIdList;//Каждый эпик знает, какие подзадачи в него входят

    public Epic(String header, String description) {
        super(header, description, CurrentStatus.New);
        this.subtaskIdList = new ArrayList<>();
    }

    public List<Long> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(List<Long> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    public void addIdSubtaskIdList(long id) {//добавление ID сабтасков в список эпика
        this.subtaskIdList.add(id);
    }

    @Override
    public String toString() {
        return "AllTasks.Epic{" +
                "subtaskIdList=" + subtaskIdList +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
