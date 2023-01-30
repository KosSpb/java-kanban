package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import enums.CurrentStatus;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private long id;//тип long для большего диапазона возможных id
    private Map<Long, Task> taskStorage;
    private Map<Long, Epic> epicStorage;
    private Map<Long, Subtask> subStorage;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.id = 0;
        this.taskStorage = new HashMap<>();
        this.epicStorage = new HashMap<>();
        this.subStorage = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    protected Map<Long, Task> getTaskStorage() {
        return taskStorage;
    }

    protected Map<Long, Epic> getEpicStorage() {
        return epicStorage;
    }

    protected Map<Long, Subtask> getSubStorage() {
        return subStorage;
    }

    protected HistoryManager getHistoryManager() {
        return historyManager;
    }

    protected void setId(long id) {
        this.id = id;
    }

    private long generateId() {
        return ++id;
    }

    //Получение списка всех задач:
    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(taskStorage.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicStorage.values());
    }

    @Override
    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subStorage.values());
    }

    //Получение по идентификатору:
    @Override
    public Task getTaskById(long id) {
        Task task = taskStorage.get(id);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = epicStorage.get(id);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = subStorage.get(id);
        historyManager.add(subtask);
        return subtask;
    }

    //Создание задач. Объект передаётся в качестве параметра:
    @Override
    public Task createTask(Task task) {
        task.setId(generateId());
        taskStorage.put(task.getId(), task);
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epicStorage.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        subtask.setId(generateId());
        subStorage.put(subtask.getId(), subtask);
        //при создании сабтаска, добавляю его id в список эпика
        Epic epic = epicStorage.get(subtask.getEpicId());
        epic.addIdSubtaskIdList(subtask.getId());
        setEpicStatus(epic);
        return subtask;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра:
    @Override
    public void updateTask(Task task) {
        if (taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
            System.out.println("Задача с ID '" + task.getId() + "' обновлена.");
        } else {
            System.out.println("ID '" + task.getId() + "' нет в списке задач. Обновление невозможно.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epicStorage.containsKey(epic.getId())) {
            epicStorage.put(epic.getId(), epic);
            System.out.println("Эпик с ID '" + epic.getId() + "' обновлен.");
        } else {
            System.out.println("ID '" + epic.getId() + "' нет в списке эпиков. Обновление невозможно.");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subStorage.containsKey(subtask.getId())) {
            subStorage.put(subtask.getId(), subtask);
            setEpicStatus(epicStorage.get(subtask.getEpicId()));
            System.out.println("Подзадача с ID '" + subtask.getId() + "' обновлена.");
        } else {
            System.out.println("ID '" + subtask.getId() + "' нет в списке подзадач. Обновление невозможно.");
        }
    }

    //Удаление всех задач:
    @Override
    public void deleteTasks() {
        if (!taskStorage.isEmpty()) {
            for (Long taskId : taskStorage.keySet()) {
                historyManager.remove(taskId);
            }
        }
        taskStorage.clear();
        System.out.println("Все задачи удалены.");
    }

    @Override
    public void deleteEpics() {
        if (!subStorage.isEmpty()) {
            for (Long subId : subStorage.keySet()) {
                historyManager.remove(subId);
            }
        }
        if (!epicStorage.isEmpty()) {
            for (Long epicId : epicStorage.keySet()) {
                historyManager.remove(epicId);
            }
        }
        subStorage.clear();
        epicStorage.clear();
        System.out.println("Все эпики с подзадачами удалены.");
    }

    @Override
    public void deleteSubtasks() {
        if (!subStorage.isEmpty()) {
            for (Long subId : subStorage.keySet()) {
                historyManager.remove(subId);
            }
        }
        for (Epic epic : epicStorage.values()) {
            List<Long> subList = epic.getSubtaskIds();
            subList.clear();
            epic.setSubtaskIds(subList);
            setEpicStatus(epic);
        }
        subStorage.clear();
        System.out.println("Все подзадачи удалены.");
    }

    //Удаление по идентификатору:
    @Override
    public void deleteTaskById(long id) {
        if (taskStorage.containsKey(id)) {
            taskStorage.remove(id);
            System.out.println("Задача с ID '" + id + "' удалена.");
            historyManager.remove(id);
        } else {
            System.out.println("Задача с ID '" + id + "' отсутствует, либо уже была удалена.");
        }
    }

    @Override
    public void deleteEpicById(long id) {
        if (epicStorage.containsKey(id)) {
            for (Long idFor : epicStorage.get(id).getSubtaskIds()) {//удаление сабтасков привязанных к эпику
                subStorage.remove(idFor);
                historyManager.remove(idFor);
            }
            epicStorage.remove(id);
            System.out.println("Эпик с ID '" + id + "' удален вместе с его подзадачами.");
            historyManager.remove(id);
        } else {
            System.out.println("Эпик с ID '" + id + "' отсутствует, либо уже был удален.");
        }
    }

    @Override
    public void deleteSubtaskById(long id) {
        if (subStorage.containsKey(id)) {
            Epic epic = epicStorage.get(subStorage.get(id).getEpicId());//удаление id сабтаска из списка его эпика
            epic.getSubtaskIds().remove(id);
            setEpicStatus(epic);
            subStorage.remove(id);
            System.out.println("Подзадача с ID '" + id + "' удалена.");
            historyManager.remove(id);
        } else {
            System.out.println("Подзадача с ID '" + id + "' отсутствует, либо уже была удалена.");
        }
    }

    //Получение списка всех подзадач определённого эпика:
    @Override
    public List<Subtask> getSubsByEpicId(long id) {
        List<Subtask> subsByEpicId = new ArrayList<>();
        for (Long idFor : epicStorage.get(id).getSubtaskIds()) {
            subsByEpicId.add(subStorage.get(idFor));
        }
        return subsByEpicId;
    }

    //Получение истории просмотров:
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    //Управление статусами эпиков:
    private void setEpicStatus(Epic epic) {
        long newTasks = 0;
        long doneTasks = 0;
        for (Long subtaskId : epic.getSubtaskIds()) {
            Subtask sub = subStorage.get(subtaskId);
            switch (sub.getStatus()) {
                case NEW:
                    newTasks++;
                    break;
                case DONE:
                    doneTasks++;
            }
            //если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW
            if (epic.getSubtaskIds().isEmpty() || newTasks == epic.getSubtaskIds().size()) {
                epic.setStatus(CurrentStatus.NEW);
            //если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE
            } else if (doneTasks == epic.getSubtaskIds().size()) {
                epic.setStatus(CurrentStatus.DONE);
            //во всех остальных случаях статус должен быть IN_PROGRESS
            } else {
                epic.setStatus(CurrentStatus.IN_PROGRESS);
            }
        }
    }
}
