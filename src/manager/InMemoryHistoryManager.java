package manager;

import allTasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    //HISTORY объявлена static final для использования всеми экземплярами класса:
    private static final List<Task> HISTORY = new ArrayList<>();

    @Override
    public void add(Task task) {
        HISTORY.add(task);
        if (HISTORY.size() > 10) {
            HISTORY.remove(0);
        }
    }

    @Override
    public List<Task> getHistory() {
        for (Task task : HISTORY) {
            System.out.println(task);
        }
        return HISTORY;
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "history=" + HISTORY +
                '}';
    }
}
