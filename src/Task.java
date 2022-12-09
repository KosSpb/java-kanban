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

    @Override
    public String toString() {
        return "Task{" +
                "header='" + header + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", id=" + id +
                '}';
    }
}
