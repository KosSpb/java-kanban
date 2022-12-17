package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

public interface TaskManager {

    //Получение списка всех задач:
    List<Task> getTaskList();
    List<Epic> getEpicList();
    List<Subtask> getSubtaskList();

    //Получение по идентификатору:
    Task getTaskById(long id);
    Epic getEpicById(long id);
    Subtask getSubtaskById(long id);

    //Создание задач. Объект передаётся в качестве параметра:
    Task createTask(Task task);
    Epic createEpic(Epic epic);
    Subtask createSubtask(Subtask subtask);

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра:
    void updateTask(Task task);
    void updateEpic(Epic epic);
    void updateSubtask(Subtask subtask);

    //Удаление всех задач:
    void deleteTasks();
    void deleteEpics();
    void deleteSubtasks();

    //Удаление по идентификатору:
    void deleteTaskById(long id);
    void deleteEpicById(long id);
    void deleteSubtaskById(long id);

    //Получение списка всех подзадач определённого эпика:
    List<Subtask> getSubsByEpicId(long id);

    //Получение истории просмотров:
    List<Task> getHistory();
}
