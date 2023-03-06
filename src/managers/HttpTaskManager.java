package managers;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import network.KVTaskClient;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.*;

public class HttpTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson;

    public HttpTaskManager(String urlOfKVServer) {
        super(null);
        this.kvTaskClient = new KVTaskClient(urlOfKVServer);
        this.gson = Managers.getGson();
    }

    @Override
    public void save() {
        if (taskStorage.isEmpty()) {
            kvTaskClient.put("tasks", gson.toJson(new ArrayList<>()));
        }
        kvTaskClient.put("tasks", gson.toJson(taskStorage));

        if (epicStorage.isEmpty()) {
            kvTaskClient.put("epics", gson.toJson(new ArrayList<>()));
        }
        kvTaskClient.put("epics", gson.toJson(epicStorage));

        if (subStorage.isEmpty()) {
            kvTaskClient.put("subtasks", gson.toJson(new ArrayList<>()));
        }
        kvTaskClient.put("subtasks", gson.toJson(subStorage));

        if (historyManager.getHistory().isEmpty()) {
            kvTaskClient.put("history", gson.toJson(new ArrayList<>()));
        }
        kvTaskClient.put("history", gson.toJson(historyManager.getHistory()));
    }

    public void loadFromKVServer() {
        long idCorrector = 0;
        String jsonResponse = kvTaskClient.load("tasks");
        if (!jsonResponse.equals("[]")) {
            taskStorage = gson.fromJson(jsonResponse, new TypeToken<HashMap<Long, Task>>(){}.getType());
            for (Map.Entry<Long, Task> taskEntry : taskStorage.entrySet()) {
                prioritizedTasks.add(taskEntry.getValue());
                if (taskEntry.getValue().getId() > idCorrector) {
                    idCorrector = taskEntry.getValue().getId();
                }
            }
        }
        jsonResponse = kvTaskClient.load("epics");
        if (!jsonResponse.equals("[]")) {
            epicStorage = gson.fromJson(jsonResponse, new TypeToken<HashMap<Long, Epic>>(){}.getType());
            for (Map.Entry<Long, Epic> epicEntry : epicStorage.entrySet()) {
                if (epicEntry.getValue().getId() > idCorrector) {
                    idCorrector = epicEntry.getValue().getId();
                }
            }
        }
        jsonResponse = kvTaskClient.load("subtasks");
        if (!jsonResponse.equals("[]")) {
            subStorage = gson.fromJson(jsonResponse, new TypeToken<HashMap<Long, Subtask>>(){}.getType());
            for (Map.Entry<Long, Subtask> subtaskEntry : subStorage.entrySet()) {
                prioritizedTasks.add(subtaskEntry.getValue());
                if (subtaskEntry.getValue().getId() > idCorrector) {
                    idCorrector = subtaskEntry.getValue().getId();
                }
            }
        }
        jsonResponse = kvTaskClient.load("history");
        if (!jsonResponse.equals("[]")) {
            List<Task> tasks = gson.fromJson(jsonResponse, new TypeToken<ArrayList<Task>>(){}.getType());
            for (Task task : tasks) {
                switch (task.getTaskType()) {
                    case EPIC:
                        historyManager.add(epicStorage.get(task.getId()));
                        break;
                    case SUBTASK:
                        historyManager.add(subStorage.get(task.getId()));
                        break;
                    default:
                        historyManager.add(taskStorage.get(task.getId()));
                }
            }
        }
        id = idCorrector;
    }
}
