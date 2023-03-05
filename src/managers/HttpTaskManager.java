package managers;

import com.google.gson.Gson;
import network.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.nio.file.Paths;
import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String urlOfKVServer) {
        super((Paths.get("resources/FileBackedTasks.csv")).toFile());
        this.kvTaskClient = new KVTaskClient(urlOfKVServer);
        this.gson = Managers.getGson();
    }

    @Override
    public void save() {
        String regex = "@d#_%Q=!#&8!";
        StringBuilder tasksJsonString = new StringBuilder();
        if (taskStorage.isEmpty()) {
            tasksJsonString.append(gson.toJson(new ArrayList<>()));
        }
        for (Long taskId : taskStorage.keySet()) {
            tasksJsonString.append(gson.toJson(taskStorage.get(taskId)));
            tasksJsonString.append(regex);
        }
        kvTaskClient.put("tasks", tasksJsonString.toString());

        StringBuilder epicsJsonString = new StringBuilder();
        if (epicStorage.isEmpty()) {
            epicsJsonString.append(gson.toJson(new ArrayList<>()));
        }
        for (Long epicId : epicStorage.keySet()) {
            epicsJsonString.append(gson.toJson(epicStorage.get(epicId)));
            epicsJsonString.append(regex);
        }
        kvTaskClient.put("epics", epicsJsonString.toString());

        StringBuilder subtasksJsonString = new StringBuilder();
        if (subStorage.isEmpty()) {
            subtasksJsonString.append(gson.toJson(new ArrayList<>()));
        }
        for (Long subtaskId : subStorage.keySet()) {
            subtasksJsonString.append(gson.toJson(subStorage.get(subtaskId)));
            subtasksJsonString.append(regex);
        }
        kvTaskClient.put("subtasks", subtasksJsonString.toString());

        StringBuilder historyString = new StringBuilder();
        if (historyManager.getHistory().isEmpty()) {
            historyString.append(gson.toJson(new ArrayList<>()));
        }
        for (Task task : historyManager.getHistory()) {
            historyString.append(task.getId());
            historyString.append(",");
        }
        kvTaskClient.put("history", historyString.toString());
    }

    public HttpTaskManager loadFromKVServer(String urlOfKVServer) {
        HttpTaskManager managerFromKVServer = new HttpTaskManager(urlOfKVServer);
        String regex = "@d#_%Q=!#&8!";
        long idCorrector = 0;
        Map<Long, Task> tasksForHistoryRestore = new HashMap<>();

        String jsonResponse = kvTaskClient.load("tasks");
        String[] partsOfJsonResponse = jsonResponse.split(regex);
        if (!jsonResponse.equals("[]")) {
            for (String taskFromJsonResponse : partsOfJsonResponse) {
                Task task = gson.fromJson(taskFromJsonResponse, Task.class);
                managerFromKVServer.taskStorage.put(task.getId(), task);
                managerFromKVServer.prioritizedTasks.add(task);
                if (task.getId() > idCorrector) {
                    idCorrector = task.getId();
                }
                tasksForHistoryRestore.put(task.getId(), task);
            }
        }

        jsonResponse = kvTaskClient.load("epics");
        partsOfJsonResponse = jsonResponse.split(regex);
        if (!jsonResponse.equals("[]")) {
            for (String epicFromJsonResponse : partsOfJsonResponse) {
                Epic epic = gson.fromJson(epicFromJsonResponse, Epic.class);
                managerFromKVServer.epicStorage.put(epic.getId(), epic);
                if (epic.getId() > idCorrector) {
                    idCorrector = epic.getId();
                }
                tasksForHistoryRestore.put(epic.getId(), epic);
            }
        }

        jsonResponse = kvTaskClient.load("subtasks");
        partsOfJsonResponse = jsonResponse.split(regex);
        if (!jsonResponse.equals("[]")) {
            for (String subtaskFromJsonResponse : partsOfJsonResponse) {
                Subtask subtask = gson.fromJson(subtaskFromJsonResponse, Subtask.class);
                managerFromKVServer.subStorage.put(subtask.getId(), subtask);
                managerFromKVServer.prioritizedTasks.add(subtask);
                if (subtask.getId() > idCorrector) {
                    idCorrector = subtask.getId();
                }
                tasksForHistoryRestore.put(subtask.getId(), subtask);
            }
        }

        String historyString = kvTaskClient.load("history");
        if (!historyString.equals("[]")) {
            String[] historyTaskIds = historyString.split(",");
            for (String taskId : historyTaskIds) {
                long parsedId = Long.parseLong(taskId);
                managerFromKVServer.historyManager.add(tasksForHistoryRestore.get(parsedId));
            }
        }

        managerFromKVServer.id = idCorrector;
        return managerFromKVServer;
    }
}
