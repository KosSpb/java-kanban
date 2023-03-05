package managers;

import enums.CurrentStatus;
import enums.TaskType;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import exceptions.ManagerSaveException;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager {
    private File file;

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    //восстанавливает данные менеджера из файла при запуске программы:
    public static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager managerFromFile = new FileBackedTasksManager(file);
        try (BufferedReader backupFileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            boolean isTask = true;
            long idCorrector = 0;
            Map<Long, Task> tasksForHistoryRestore = new HashMap<>();

            while (backupFileReader.ready()) {
                String lineFromFile = backupFileReader.readLine();
                if (lineFromFile.startsWith("id")) {
                    continue;
                }
                if (lineFromFile.isBlank()) {
                    isTask = false;
                    continue;
                }
                if (isTask) {
                    Task task = managerFromFile.fromString(lineFromFile);
                    if (task.getId() > idCorrector) {
                        idCorrector = task.getId();
                    }
                    tasksForHistoryRestore.put(task.getId(), task);
                    if (task.getTaskType() == TaskType.TASK) {
                        managerFromFile.taskStorage.put(task.getId(), task);
                    } else if (task.getTaskType() == TaskType.EPIC) {
                        Epic epic = (Epic) task;
                        managerFromFile.epicStorage.put(epic.getId(), epic);
                    } else {
                        Subtask subtask = (Subtask) task;
                        managerFromFile.subStorage.put(subtask.getId(), subtask);
                        Epic epic = managerFromFile.epicStorage.get(subtask.getEpicId());
                        epic.addIdSubtaskIdList(subtask.getId());
                    }
                } else {
                    for (Long taskId : historyFromString(lineFromFile)) {
                        managerFromFile.historyManager.add(tasksForHistoryRestore.get(taskId));
                    }
                }
            }
            managerFromFile.id = idCorrector;
        } catch (IOException exception) {
            throw new ManagerSaveException("Произошла ошибка во время восстановления!", exception);
        }
        return managerFromFile;
    }

    //Получение по идентификатору:
    public Task getTaskById(long id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }
    public Epic getEpicById(long id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }
    public Subtask getSubtaskById(long id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    //Создание задач. Объект передаётся в качестве параметра:
    public Task createTask(Task task) {
        super.createTask(task);
        save();
        return task;
    }
    public Epic createEpic(Epic epic) {
        super.createEpic(epic);
        save();
        return epic;
    }
    public Subtask createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
        return subtask;
    }

    //Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра:
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    //Удаление всех задач:
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    //Удаление по идентификатору:
    public void deleteTaskById(long id) {
        super.deleteTaskById(id);
        save();
    }
    public void deleteEpicById(long id) {
        super.deleteEpicById(id);
        save();
    }
    public void deleteSubtaskById(long id) {
        super.deleteSubtaskById(id);
        save();
    }

    public void save() { //сохраняет текущее состояние менеджера в указанный файл
        Map<Long, Task> sortedTasks = new TreeMap<>();
        for (Long taskId : taskStorage.keySet()) {
            sortedTasks.put(taskId, taskStorage.get(taskId));
        }
        for (Long epicId : epicStorage.keySet()) {
            sortedTasks.put(epicId, epicStorage.get(epicId));
        }
        for (Long subtaskId : subStorage.keySet()) {
            sortedTasks.put(subtaskId, subStorage.get(subtaskId));
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {
            bufferedWriter.write("id,type,name,status,description,start time,end time,duration,epic\n");
            for (Map.Entry<Long, Task> sortedTasksEntry : sortedTasks.entrySet()) {
                if (sortedTasksEntry.getValue() != null) {
                    bufferedWriter.write(toString(sortedTasksEntry.getValue()) + "\n");
                }
            }
            bufferedWriter.write("\n");
            if (!historyManager.getHistory().isEmpty()) {
                bufferedWriter.write(historyToString(historyManager));
            }
        } catch (IOException exception) {
            throw new ManagerSaveException("Произошла ошибка во время сохранения!", exception);
        }
    }

    private String toString(Task task) { //создаёт строку из задачи
        String taskStartTime;
        String taskEndTime;
        if (task.getStartTime() == null) {
            taskStartTime = "null";
            taskEndTime = "null";
        } else {
            taskStartTime = task.getStartTime().toString();
            taskEndTime = task.getEndTime().toString();
        }
        String taskToString = String.format("%d,%s,%s,%s,%s,%s,%s,%s,", task.getId(), task.getTaskType().toString(),
                task.getHeader(), task.getStatus().toString(), task.getDescription(), taskStartTime, taskEndTime,
                task.getDurationInMinutes().toString());
        if (task.getTaskType() == TaskType.SUBTASK) {
            Subtask subtask = (Subtask)task;
            taskToString += String.format("%d", subtask.getEpicId());
        }
        return taskToString;
    }

    private static String historyToString(HistoryManager manager) { //создаёт строку из истории просмотров
        StringBuilder historyString = new StringBuilder();
        for (Task task : manager.getHistory()) {
            historyString.append(task.getId());
            historyString.append(",");
        }
        historyString.deleteCharAt(historyString.length() - 1);
        return historyString.toString();
    }

    private Task fromString(String value) { //создаёт задачу из строки
        String[] taskFields = value.split(",");
        long id = Long.parseLong(taskFields[0]);
        TaskType taskType = TaskType.valueOf(taskFields[1]);
        String header = taskFields[2];
        CurrentStatus status = CurrentStatus.valueOf(taskFields[3]);
        String description = taskFields[4];
        LocalDateTime startTime;
        LocalDateTime endTime;
        if (taskFields[5].equals("null")) {
            startTime = null;
            endTime = null;
        } else {
            startTime = LocalDateTime.parse(taskFields[5]);
            endTime = LocalDateTime.parse(taskFields[6]);
        }
        Duration durationInMinutes = Duration.parse(taskFields[7]);
        if (taskType == TaskType.SUBTASK) {
            long epicId = Long.parseLong(taskFields[8]);
            return new Subtask(id, header, status, description, startTime, durationInMinutes, epicId);
        }
        if (taskType == TaskType.EPIC) {
            return new Epic(id, header, status, description, startTime, endTime, durationInMinutes);
        }
        return new Task(id, header, status, description, startTime, durationInMinutes);
    }

    private static List<Long> historyFromString(String value) { //восстановление истории просмотров из строки
        String[] historyTaskIds = value.split(",");
        List<Long> restoredHistory = new ArrayList<>();
        for (String taskId : historyTaskIds) {
            restoredHistory.add(Long.parseLong(taskId));
        }
        return restoredHistory;
    }

    public static void main(String[] args) {
        TaskManager fileBackedTasksManager = Managers.getDefault();

        Task task1 = new Task("Заменить процессор", "на купленный", CurrentStatus.NEW,
                null, 0);
        Task task2 = new Task("Починить печку", "в машине", CurrentStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 2, 5, 1, 5), 25);
        Epic epic1 = new Epic("Закончить 6-й спринт", "ещё вчера");
        Subtask subtask1 = new Subtask(3, "Изучить теорию", "7-го спринта",
                CurrentStatus.NEW, LocalDateTime.of(2023, 2, 6, 12, 30),
                350);
        Subtask subtask2 = new Subtask(3, "Сдать ТЗ", "6-го спринта", CurrentStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 1, 3, 9, 10), 150);
        Epic epic2 = new Epic("Закончить второй учебный модуль", "успеть до 20.02");
        Subtask subtask3 = new Subtask(6, "Сдать ТЗ", "7-го спринта", CurrentStatus.NEW,
                null, 0);

        fileBackedTasksManager.createTask(task1);
        fileBackedTasksManager.createTask(task2);
        fileBackedTasksManager.createEpic(epic1);
        fileBackedTasksManager.createSubtask(subtask1);
        fileBackedTasksManager.createSubtask(subtask2);
        fileBackedTasksManager.createEpic(epic2);
        fileBackedTasksManager.createSubtask(subtask3);

        fileBackedTasksManager.getEpicById(3);
        fileBackedTasksManager.getSubtaskById(4);
        fileBackedTasksManager.getSubtaskById(5);
        fileBackedTasksManager.getEpicById(6);
        fileBackedTasksManager.getSubtaskById(7);
        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.getTaskById(2);
        fileBackedTasksManager.getTaskById(1);
        fileBackedTasksManager.getEpicById(3);
        fileBackedTasksManager.getEpicById(6);
        fileBackedTasksManager.getSubtaskById(7);
        fileBackedTasksManager.getTaskById(1);

        System.out.println("\nИстория просмотров после вызова 12 задач:\n");
        System.out.println(fileBackedTasksManager.getHistory());
        System.out.println("________________________________");

        TaskManager restoredFileBackedTasksManager =
                loadFromFile((Paths.get("resources/FileBackedTasks.csv")).toFile());

        System.out.println("\nСписок задач после восстановления из файла:\n");
        System.out.println(restoredFileBackedTasksManager.getTaskList());
        System.out.println(restoredFileBackedTasksManager.getEpicList());
        System.out.println(restoredFileBackedTasksManager.getSubtaskList());
        System.out.println("________________________________");

        System.out.println("\nИстория просмотров в восстановленной истории:\n");
        System.out.println(restoredFileBackedTasksManager.getHistory());
        System.out.println("________________________________");
    }
}
