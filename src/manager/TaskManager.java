package manager;

import allTasks.Epic;
import allTasks.Subtask;
import allTasks.Task;
import status.CurrentStatus;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class TaskManager {
    private long id;//тип long для большего диапазона возможных id
    private Map<Long, Task> taskStorage;
    private Map<Long, Epic> epicStorage;
    private Map<Long, Subtask> subStorage;

    public TaskManager() {
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
        return new ArrayList<>(taskStorage.values());
    }

    public List<Epic> getEpicList() {
        return new ArrayList<>(epicStorage.values());
    }

    public List<Subtask> getSubtaskList() {
        return new ArrayList<>(subStorage.values());
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
        task.setId(generateId());
        taskStorage.put(task.getId(), task);
        return task;
    }

    public Epic createEpic(Epic epic) {
        epic.setId(generateId());
        epicStorage.put(epic.getId(), epic);
        return epic;
    }

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
    public void updateTask(Task task) {
        if (taskStorage.containsKey(task.getId())) {
            taskStorage.put(task.getId(), task);
            System.out.println("Задача с ID '" + task.getId() + "' обновлена.");
        } else {
            System.out.println("ID '" + task.getId() + "' нет в списке задач. Обновление невозможно.");
        }
    }

    public void updateEpic(Epic epic) {
        if (epicStorage.containsKey(epic.getId())) {
            epicStorage.put(epic.getId(), epic);
            System.out.println("Эпик с ID '" + epic.getId() + "' обновлен.");
        } else {
            System.out.println("ID '" + epic.getId() + "' нет в списке эпиков. Обновление невозможно.");
        }
    }

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
    public void deleteTasks() {
        taskStorage.clear();
        System.out.println("Все задачи удалены.");
    }

    public void deleteEpics() {
        subStorage.clear();
        epicStorage.clear();
        System.out.println("Все эпики с подзадачами удалены.");
    }

    public void deleteSubtasks() {
        for (Epic epic : epicStorage.values()) {
            List<Long> subList = epic.getSubtaskIdList();
            subList.clear();
            epic.setSubtaskIdList(subList);
            setEpicStatus(epic);
        }
        subStorage.clear();
        System.out.println("Все подзадачи удалены.");
    }

    //Удаление по идентификатору:
    public void deleteTaskById(long id) {
        if (taskStorage.containsKey(id)) {
            taskStorage.remove(id);
            System.out.println("Задача с ID '" + id + "' удалена.");
        } else {
            System.out.println("Задача с ID '" + id + "' отсутствует, либо уже была удалена.");
        }
    }

    public void deleteEpicById(long id) {
        if (epicStorage.containsKey(id)) {
            for (Long idFor : epicStorage.get(id).getSubtaskIdList()) {//удаление сабтасков привязанных к эпику
                subStorage.remove(idFor);
            }
            epicStorage.remove(id);
            System.out.println("Эпик с ID '" + id + "' удален вместе с его подзадачами.");
        } else {
            System.out.println("Эпик с ID '" + id + "' отсутствует, либо уже был удален.");
        }
    }

    public void deleteSubtaskById(long id) {
        if (subStorage.containsKey(id)) {
            Epic epic = epicStorage.get(subStorage.get(id).getEpicId());//удаление id сабтаска из списка его эпика
            epic.getSubtaskIdList().remove(id);
            setEpicStatus(epic);
            subStorage.remove(id);
            System.out.println("Подзадача с ID '" + id + "' удалена.");
        } else {
            System.out.println("Подзадача с ID '" + id + "' отсутствует, либо уже была удалена.");
        }
    }

    //Получение списка всех подзадач определённого эпика:
    public List<Subtask> getSubsByEpicId(long id) {
        List<Subtask> subsByEpicId = new ArrayList<>();
        for (Long idFor : epicStorage.get(id).getSubtaskIdList()) {
            subsByEpicId.add(subStorage.get(idFor));
        }
        return subsByEpicId;
    }

    //Управление статусами эпиков:
    private void setEpicStatus(Epic epic) {
        long newTasks = 0;
        long doneTasks = 0;
        for (Long subtaskId : epic.getSubtaskIdList()) {
            Subtask sub = subStorage.get(subtaskId);
            switch (sub.getStatus()) {
                case New:
                    newTasks++;
                    break;
                case Done:
                    doneTasks++;
            }
            //если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW
            if (epic.getSubtaskIdList().isEmpty() || newTasks == epic.getSubtaskIdList().size()) {
                epic.setStatus(CurrentStatus.New);
            //если все подзадачи имеют статус DONE, то и эпик считается завершённым — со статусом DONE
            } else if (doneTasks == epic.getSubtaskIdList().size()) {
                epic.setStatus(CurrentStatus.Done);
            //во всех остальных случаях статус должен быть IN_PROGRESS
            } else {
                epic.setStatus(CurrentStatus.InProgress);
            }
        }
    }
}
