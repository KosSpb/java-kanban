import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Manager {
    private long id;//тип long для большего диапазона возможных id
    private Map<Long, Task> taskStorage;
    private Map<Long, Epic> epicStorage;
    private Map<Long, Subtask> subStorage;

    public Manager() {
        this.id = 0;
        this.taskStorage = new HashMap<>();
        this.epicStorage = new HashMap<>();
        this.subStorage = new HashMap<>();
    }

    private long generateId() {
        return ++id;
    }

    //Получение списка всех задач:
    public List<Task> getTaskList() {
        List<Task> taskList = new ArrayList<>();
        for (Task tasks : taskStorage.values()) {
            taskList.add(tasks);
        }
        return taskList;
    }

    public List<Epic> getEpicList() {
        List<Epic> epicList = new ArrayList<>();
        for (Epic epics : epicStorage.values()) {
            epicList.add(epics);
        }
        return epicList;
    }

    public List<Subtask> getSubtaskList() {
        List<Subtask> subtaskList = new ArrayList<>();
        for (Subtask subs : subStorage.values()) {
            subtaskList.add(subs);
        }
        return subtaskList;
    }

    //Получение по идентификатору:
    public Task getTaskById(long id) {
        return taskStorage.get(id);
    }

    public Epic getEpicById(long id) {
        return epicStorage.get(id);
    }

    public Subtask getSubtaskById(long id) {
        return subStorage.get(id);
    }

    //Создание задач. Объект передаётся в качестве параметра:
    public Task createTask(Task task) {
        task.id = generateId();
        taskStorage.put(task.id, task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.id = generateId();
        epicStorage.put(epic.id, epic);
        return epic;
    }

    public Subtask createSubtask(Subtask subtask) {
        subtask.id = generateId();
        subStorage.put(subtask.id, subtask);
        //при создании сабтаска, добавляю его id в список эпика
        Epic epic = epicStorage.get(subtask.epicId);
        epic.subtaskIdList.add(subtask.id);
        setEpicStatus(epic);
        return subtask;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра:
    public void updateTask(Task task) {
        taskStorage.put(task.id, task);
    }

    public void updateEpic(Epic epic) {
        setEpicStatus(epic);
        epicStorage.put(epic.id, epic);
    }

    public void updateSubtask(Subtask subtask) {
        subStorage.put(subtask.id, subtask);
    }

    //Удаление всех задач:
    public void deleteTasks() {
        taskStorage.clear();
    }

    public void deleteEpics() {
        subStorage.clear();
        epicStorage.clear();
    }

    public void deleteSubtasks() {
        for (Epic epic : epicStorage.values()) {
            epic.subtaskIdList.clear();
            setEpicStatus(epic);
        }
        subStorage.clear();
    }

    //Удаление по идентификатору:
    public void deleteTaskById(long id) {
        taskStorage.remove(id);
    }

    public void deleteEpicById(long id) {
        for (Long idFor : epicStorage.get(id).subtaskIdList) {//удаление сабтасков привязанных к эпику
            subStorage.remove(idFor);
        }
        epicStorage.remove(id);
    }

    public void deleteSubtaskById(long id) {
        Epic epic = epicStorage.get(subStorage.get(id).epicId);//удаление id сабтаска из списка его эпика
        epic.subtaskIdList.remove(id);
        setEpicStatus(epic);
        subStorage.remove(id);
    }

    //Получение списка всех подзадач определённого эпика:
    public List<Subtask> getSubsByEpicId(long id) {
        List<Subtask> subsByEpicId = new ArrayList<>();
        for (Long idFor : epicStorage.get(id).subtaskIdList) {
            subsByEpicId.add(subStorage.get(idFor));
        }
        return subsByEpicId;
    }

    //Управление статусами эпиков:
    private void setEpicStatus(Epic epic) {
        long newTasks = 0;
        long doneTasks = 0;
        for (Long subtaskId : epic.subtaskIdList) {
            Subtask sub = subStorage.get(subtaskId);
            switch (sub.status) {
                case New:
                    newTasks++;
                    break;
                case Done:
                    doneTasks++;
            }
            //если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW
            if (epic.subtaskIdList.isEmpty() || newTasks == epic.subtaskIdList.size()) {
                epic.status = CurrentStatus.New;
            //если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE
            } else if (doneTasks == epic.subtaskIdList.size()) {
                epic.status = CurrentStatus.Done;
            //во всех остальных случаях статус должен быть IN_PROGRESS
            } else {
                epic.status = CurrentStatus.InProgress;
            }
        }
    }
}
