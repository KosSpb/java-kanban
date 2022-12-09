public class Subtask extends Task {
    protected long epicId;//Для каждой подзадачи известно, в рамках какого эпика она выполняется

    public Subtask(long epicId, String header, String description, CurrentStatus status) {
        super(header, description, status);
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "belongsToEpic='" + epicId + '\'' +
                ", header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
