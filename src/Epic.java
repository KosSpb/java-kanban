import java.util.List;
import java.util.ArrayList;

public class Epic extends Task {
    protected List<Long> subtaskIdList;//Каждый эпик знает, какие подзадачи в него входят

    public Epic(String header, String description) {
        super(header, description, CurrentStatus.New);
        this.subtaskIdList = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtaskIdList=" + subtaskIdList +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
