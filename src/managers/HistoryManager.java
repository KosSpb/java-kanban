package managers;

import tasks.Task;

import java.util.List;

public interface HistoryManager {

    void add(Task task); //помечает задачи как просмотренные
    void remove(long id); //удаление задачи из истории просмотра
    List<Task> getHistory(); //возвращает список просмотренных задач
}
