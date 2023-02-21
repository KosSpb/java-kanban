package managers;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import enums.CurrentStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected long id;//тип long для большего диапазона возможных id
    protected Map<Long, Task> taskStorage;
    protected Map<Long, Epic> epicStorage;
    protected Map<Long, Subtask> subStorage;
    protected HistoryManager historyManager;
    protected Set<Task> prioritizedTasks;

    public InMemoryTaskManager() {
        this.id = 0;
        this.taskStorage = new HashMap<>();
        this.epicStorage = new HashMap<>();
        this.subStorage = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.prioritizedTasks = new TreeSet<>((task1, task2) -> {
            if (task1.getStartTime() == null) {
                if (task2.getStartTime() == null) {
                    return (int) (task1.getId() - task2.getId());
                }
                return 1;
            }
            if (task2.getStartTime() == null) {
                return -1;
            }
            return task1.getStartTime().compareTo(task2.getStartTime());
        });
    }

    //Получение списка задач, отсортированных по времени начала:
    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
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
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(long id) {
        Epic epic = epicStorage.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(long id) {
        Subtask subtask = subStorage.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    //Создание задач. Объект передаётся в качестве параметра:
    @Override
    public Task createTask(Task task) {
        if (task != null) {
            checkTaskTimeCrossing(task);
            task.setId(generateId());
            taskStorage.put(task.getId(), task);
            prioritizedTasks.add(task);
        } else {
            System.out.println("В метод создания задачи был передан null. Задача не создана.");
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic != null) {
            epic.setId(generateId());
            epicStorage.put(epic.getId(), epic);
        } else {
            System.out.println("В метод создания эпика был передан null. Эпик не создан.");
        }
        return epic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask != null) {
            checkTaskTimeCrossing(subtask);
            if (!epicStorage.containsKey(subtask.getEpicId())) {
                throw new IllegalArgumentException("Эпика с ID '" + subtask.getEpicId() + "' не существует.");
            }
            Epic epic = epicStorage.get(subtask.getEpicId());
            subtask.setId(generateId());
            subStorage.put(subtask.getId(), subtask);
            //при создании сабтаска, добавляю его id в список эпика
            epic.addIdSubtaskIdList(subtask.getId());
            setEpicStatus(epic);
            if (subtask.getStartTime() != null) {
                setEpicTime(epic);
            }
            prioritizedTasks.add(subtask);
        } else {
            System.out.println("В метод создания подзадачи был передан null. Подзадача не создана.");
        }
        return subtask;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра:
    @Override
    public void updateTask(Task task) {
        if (task != null) {
            if (!taskStorage.containsKey(task.getId())) {
                throw new IllegalArgumentException("ID '" + task.getId() + "' нет в списке задач. " +
                        "Обновление невозможно.");
            }
            checkTaskTimeCrossing(task);
            prioritizedTasks.remove(taskStorage.get(task.getId()));
            taskStorage.put(task.getId(), task);
            prioritizedTasks.add(task);
            System.out.println("Задача с ID '" + task.getId() + "' обновлена.");
        } else {
            System.out.println("В метод обновления задачи был передан null. Задача не обновлена.");
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic != null) {
            if (!epicStorage.containsKey(epic.getId())) {
                throw new IllegalArgumentException("ID '" + epic.getId() + "' нет в списке эпиков. " +
                        "Обновление невозможно.");
            }
            List<Long> existingSubtasks = epicStorage.get(epic.getId()).getSubtaskIds();
            if (!existingSubtasks.isEmpty()) {
                epic.setSubtaskIds(existingSubtasks);
                setEpicTime(epic);
            }
            epicStorage.put(epic.getId(), epic);
            System.out.println("Эпик с ID '" + epic.getId() + "' обновлен.");
        } else {
            System.out.println("В метод обновления эпика был передан null. Эпик не обновлен.");
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask != null) {
            if (!subStorage.containsKey(subtask.getId())) {
                throw new IllegalArgumentException("ID '" + subtask.getId() + "' нет в списке подзадач. " +
                        "Обновление невозможно.");
            }
            checkTaskTimeCrossing(subtask);
            prioritizedTasks.remove(subStorage.get(subtask.getId()));
            subStorage.put(subtask.getId(), subtask);
            prioritizedTasks.add(subtask);
            setEpicStatus(epicStorage.get(subtask.getEpicId()));
            setEpicTime(epicStorage.get(subtask.getEpicId()));
            System.out.println("Подзадача с ID '" + subtask.getId() + "' обновлена.");
        } else {
            System.out.println("В метод обновления подзадачи был передан null. Подзадача не обновлена.");
        }
    }

    //Удаление всех задач:
    @Override
    public void deleteTasks() {
        if (!taskStorage.isEmpty()) {
            for (Map.Entry<Long, Task> taskEntry : taskStorage.entrySet()) {
                historyManager.remove(taskEntry.getKey());
                prioritizedTasks.remove(taskEntry.getValue());
            }
        }
        taskStorage.clear();
        System.out.println("Все задачи удалены.");
    }

    @Override
    public void deleteEpics() {
        if (!subStorage.isEmpty()) {
            for (Map.Entry<Long, Subtask> subEntry : subStorage.entrySet()) {
                historyManager.remove(subEntry.getKey());
                prioritizedTasks.remove(subEntry.getValue());
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
            for (Map.Entry<Long, Subtask> subEntry : subStorage.entrySet()) {
                historyManager.remove(subEntry.getKey());
                prioritizedTasks.remove(subEntry.getValue());
            }
        }
        for (Epic epic : epicStorage.values()) {
            List<Long> subList = epic.getSubtaskIds();
            subList.clear();
            epic.setSubtaskIds(subList);
            setEpicStatus(epic);
            setEpicTime(epic);
        }
        subStorage.clear();
        System.out.println("Все подзадачи удалены.");
    }

    //Удаление по идентификатору:
    @Override
    public void deleteTaskById(long id) {
        if (taskStorage.containsKey(id)) {
            prioritizedTasks.remove(taskStorage.remove(id));
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
                prioritizedTasks.remove(subStorage.remove(idFor));
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
            setEpicTime(epic);
            prioritizedTasks.remove(subStorage.remove(id));
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
        Epic epic = epicStorage.get(id);
        if (epic != null) {
            for (Long idFor : epic.getSubtaskIds()) {
                subsByEpicId.add(subStorage.get(idFor));
            }
        } else {
            System.out.println("ID '" + id + "' нет в списке эпиков. Невозможно получить список его подзадач.");
        }
        return subsByEpicId;
    }

    //Получение истории просмотров:
    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    private long generateId() {
        return ++id;
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

    //Управление временем эпиков:
    private void setEpicTime(Epic epic) {
        LocalDateTime earliestTime = null;
        LocalDateTime latestTime = null;
        Duration epicDuration = Duration.ofMinutes(0);
        for (Long subtaskId : epic.getSubtaskIds()) {
            Subtask sub = subStorage.get(subtaskId);
            LocalDateTime subStartTime = sub.getStartTime();
            if (subStartTime == null) {
                continue;
            }
            if (earliestTime == null || subStartTime.isBefore(earliestTime)) {
                earliestTime = subStartTime;
            }
            LocalDateTime subEndTime = sub.getEndTime();
            if (latestTime == null || subEndTime.isAfter(latestTime)) {
                latestTime = subEndTime;
            }
            epicDuration = epicDuration.plus(sub.getDurationInMinutes());
        }
        epic.setDurationInMinutes(epicDuration);
        epic.setStartTime(earliestTime);
        epic.setEndTime(latestTime);
    }

    //Проверка пересечения задач по времени:
    private void checkTaskTimeCrossing(Task newTask) {
        if (newTask.getStartTime() == null) {
            return;
        }
        boolean isTimeCrossing = false;
        for (Task existingTask : prioritizedTasks) {
            if (existingTask.getStartTime() == null || existingTask.getId() == newTask.getId()) {
                continue;
            }
            if ((newTask.getStartTime().isAfter(existingTask.getStartTime())
                    || newTask.getStartTime().equals(existingTask.getStartTime()))
                    && (newTask.getStartTime().isBefore(existingTask.getEndTime())
                    || newTask.getStartTime().equals(existingTask.getEndTime()))) {
                isTimeCrossing = true;
            } else if ((newTask.getEndTime().isAfter(existingTask.getStartTime())
                    || newTask.getEndTime().equals(existingTask.getStartTime()))
                    && (newTask.getEndTime().isBefore(existingTask.getEndTime())
                    || newTask.getEndTime().equals(existingTask.getEndTime()))) {
                isTimeCrossing = true;
            } else if ((existingTask.getStartTime().isAfter(newTask.getStartTime())
                    || existingTask.getStartTime().equals(newTask.getStartTime()))
                    && (existingTask.getEndTime().isBefore(newTask.getEndTime())
                    || existingTask.getEndTime().equals(newTask.getEndTime()))) {
                isTimeCrossing = true;
            }
            if (isTimeCrossing) {
                throw new IllegalStateException(String.format("Время создаваемой задачи пересекается с задачей '%s' " +
                        "типа %s. Время её начала - %s. Время её окончания - %s. Выберите другое время для новой " +
                        "задачи.", existingTask.getHeader(), existingTask.getTaskType().toString(),
                        existingTask.getStartTime().toString(), existingTask.getEndTime().toString()));
            }
        }
    }
}
