package network;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import enums.TaskType;
import managers.Managers;
import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private HttpServer server;
    private Gson gson;
    private TaskManager httpTaskManager;
    private final String pathOfTask;
    private final String pathOfEpic;
    private final String pathOfSubtask;
    private final String pathOfTaskWithId;
    private final String pathOfEpicWithId;
    private final String pathOfSubtaskWithId;
    private final String pathOfHistory;
    private final String pathOfPrioritizedTasks;
    private final String pathOfEpicSubtasksWithId;

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public HttpTaskServer(TaskManager httpTaskManager) throws IOException {
        this.httpTaskManager = httpTaskManager;
        this.gson = Managers.getGson();
        this.server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::handleTasks);
        this.pathOfTask = "^/tasks/task/$";
        this.pathOfEpic = "^/tasks/epic/$";
        this.pathOfSubtask = "^/tasks/subtask/$";
        this.pathOfTaskWithId = "^/tasks/task/\\?id=\\d+$";
        this.pathOfEpicWithId = "^/tasks/epic/\\?id=\\d+$";
        this.pathOfSubtaskWithId = "^/tasks/subtask/\\?id=\\d+$";
        this.pathOfHistory = "^/tasks/history/$";
        this.pathOfPrioritizedTasks = "^/tasks/$";
        this.pathOfEpicSubtasksWithId = "^/tasks/subtask/epic/\\?id=\\d+$";
    }

    public void start() {
        System.out.println("Запускаем HttpTaskServer на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        server.stop(0);
        System.out.println("Остановили HttpTaskServer на порту " + PORT);
    }

    private void handleTasks(HttpExchange httpExchange) {
        try {
            String path = httpExchange.getRequestURI().toString();
            String requestMethod = httpExchange.getRequestMethod();
            String response;
            switch (requestMethod) {
                case "GET":
                    if (Pattern.matches(pathOfTask, path)
                            || Pattern.matches(pathOfEpic, path)
                            || Pattern.matches(pathOfSubtask, path)
                            || Pattern.matches(pathOfHistory, path)
                            || Pattern.matches(pathOfPrioritizedTasks, path)) {

                        handleGetAnyTypeOfTaskListOrListOfHistory(httpExchange, path);

                    } else if (Pattern.matches(pathOfTaskWithId, path)
                            || Pattern.matches(pathOfEpicWithId, path)
                            || Pattern.matches(pathOfSubtaskWithId, path)) {

                        handleGetAnyTypeOfTaskById(httpExchange, path);

                    } else if (Pattern.matches(pathOfEpicSubtasksWithId, path)) {

                        handleGetSubsByEpicId(httpExchange, path);

                    } else {
                        response = "Путь " + path + " не соответствует ожидаемому.";
                        System.out.println(response);
                        sendResponse(httpExchange, response, 405);
                    }
                    break;

                case "POST":
                    if (Pattern.matches(pathOfTask, path)
                            || Pattern.matches(pathOfEpic, path)
                            || Pattern.matches(pathOfSubtask, path)) {

                        handlePostCreateOrUpdateAnyTypeOfTask(httpExchange, path);

                    } else {
                        response = "Путь " + path + " не соответствует ожидаемому.";
                        System.out.println(response);
                        sendResponse(httpExchange, response, 405);
                    }
                    break;

                case "DELETE":
                    if (Pattern.matches(pathOfTask, path)
                            || Pattern.matches(pathOfEpic, path)
                            || Pattern.matches(pathOfSubtask, path)) {

                        handleDeleteAnyTypeOfTasks(httpExchange, path);

                    } else if (Pattern.matches(pathOfTaskWithId, path)
                            || Pattern.matches(pathOfEpicWithId, path)
                            || Pattern.matches(pathOfSubtaskWithId, path)) {

                        handleDeleteAnyTypeOfTaskById(httpExchange, path);

                    } else {
                        response = "Путь " + path + " не соответствует ожидаемому.";
                        System.out.println(response);
                        sendResponse(httpExchange, response, 405);
                    }
                    break;

                default:
                    response = "Ожидается GET, POST или DELETE запрос, а поступил - " + requestMethod;
                    System.out.println(response);
                    sendResponse(httpExchange, response, 405);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            httpExchange.close();
        }
    }

    private long parsePathId(String path) {
        try {
            return Long.parseLong(path);
        } catch (NumberFormatException exception) {
            return -1L;
        }
    }

    private String readRequest(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), UTF_8);
    }

    private void sendResponse(HttpExchange httpExchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
        } else {
            byte[] response = responseString.getBytes(UTF_8);
            httpExchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            httpExchange.sendResponseHeaders(responseCode, response.length);
            try (OutputStream os = httpExchange.getResponseBody()){
                os.write(response);
            }
        }
    }

    //Обработка получения списка всех задач:
    private void handleGetAnyTypeOfTaskListOrListOfHistory(HttpExchange httpExchange, String path) throws IOException {
        String response = "";
        String[] pathParts = path.split("/");
        if (pathParts.length <= 2) {
            response = gson.toJson(httpTaskManager.getPrioritizedTasks());
        } else {
            String requestType = pathParts[2];
            switch (requestType) {
                case "task":
                    response = gson.toJson(httpTaskManager.getTaskList());
                    break;
                case "epic":
                    response = gson.toJson(httpTaskManager.getEpicList());
                    break;
                case "subtask":
                    response = gson.toJson(httpTaskManager.getSubtaskList());
                    break;
                case "history":
                    response = gson.toJson(httpTaskManager.getHistory());
            }
        }
        sendResponse(httpExchange, response, 200);
    }

    //Обработка получения по идентификатору:
    private void handleGetAnyTypeOfTaskById(HttpExchange httpExchange, String path) throws IOException {
        String response;
        Task anyTask = null;
        String taskType = path.split("/")[2];
        TaskType requestType = TaskType.valueOf(taskType.toUpperCase());
        String pathId = path.replaceFirst("/tasks/" + taskType + "/\\?id=", "");
        long id = parsePathId(pathId);
        if (id != -1) {
            switch (requestType) {
                case TASK:
                    anyTask = httpTaskManager.getTaskById(id);
                    break;
                case EPIC:
                    anyTask = httpTaskManager.getEpicById(id);
                    break;
                case SUBTASK:
                    anyTask = httpTaskManager.getSubtaskById(id);
            }
            if (anyTask == null) {
                response = "Задачи с ID '" + pathId + "' не существует.";
                System.out.println(response);
                sendResponse(httpExchange, response, 404);
            } else {
                response = gson.toJson(anyTask);
                sendResponse(httpExchange, response, 200);
            }
        } else {
            response = "Получен некорректный ID " + pathId;
            System.out.println(response);
            sendResponse(httpExchange, response, 405);
        }
    }

    //Обработка получения списка всех подзадач определённого эпика:
    private void handleGetSubsByEpicId(HttpExchange httpExchange, String path) throws IOException {
        String response;
        String pathId = path.replaceFirst("/tasks/subtask/epic/\\?id=", "");
        long id = parsePathId(pathId);
        if (id != -1) {
            List<Subtask> subsByEpicId = httpTaskManager.getSubsByEpicId(id);
            if (subsByEpicId.isEmpty()) {
                response = "Эпика с ID '" + pathId + "' не существует, либо список его подзадач пуст.";
                System.out.println(response);
                sendResponse(httpExchange, response, 404);
            } else {
                response = gson.toJson(subsByEpicId);
                sendResponse(httpExchange, response, 200);
            }
        } else {
            response = "Получен некорректный ID " + pathId;
            System.out.println(response);
            sendResponse(httpExchange, response, 405);
        }
    }

    //Обработка создания и обновления задач:
    private void handlePostCreateOrUpdateAnyTypeOfTask(HttpExchange httpExchange, String path) throws IOException {
        String jsonRequest = readRequest(httpExchange);
        TaskType requestType = TaskType.valueOf(path.split("/")[2].toUpperCase());
        try {
            switch (requestType) {
                case TASK:
                    Task task = gson.fromJson(jsonRequest, Task.class);
                    if (task.getTaskType() == null || task.getHeader() == null || task.getDescription() == null
                            || task.getStatus() == null || task.getDurationInMinutes() == null) {
                        sendResponse(httpExchange, "Значение null может быть только в поле startTime и " +
                                "endTime", 400);
                        return;
                    } else if (task.getHeader().isBlank()) {
                        sendResponse(httpExchange, "Заголовок не может быть пустыми", 400);
                        return;
                    } else if (task.getTaskType() != TaskType.TASK) {
                        sendResponse(httpExchange, "Передан JSON c некорректным типом задачи",
                                400);
                        return;
                    }

                    if (task.getId() > 0) {
                        httpTaskManager.updateTask(task);
                        sendResponse(httpExchange, "Задача обновлена", 200);
                    } else {
                        httpTaskManager.createTask(task);
                        sendResponse(httpExchange, "Задача создана", 200);
                    }
                    break;

                case EPIC:
                    Epic epic = gson.fromJson(jsonRequest, Epic.class);
                    if (epic.getSubtaskIds() == null || epic.getTaskType() == null || epic.getHeader() == null 
                            || epic.getDescription() == null || epic.getStatus() == null 
                            || epic.getDurationInMinutes() == null) {
                        sendResponse(httpExchange, "Значение null может быть только в поле startTime и " +
                                "endTime", 400);
                        return;
                    } else if (epic.getHeader().isBlank()) {
                        sendResponse(httpExchange, "Заголовок не может быть пустыми", 400);
                        return;
                    } else if (epic.getTaskType() != TaskType.EPIC) {
                        sendResponse(httpExchange, "Передан JSON c некорректным типом задачи",
                                400);
                        return;
                    }

                    if (epic.getId() > 0) {
                        httpTaskManager.updateEpic(epic);
                        sendResponse(httpExchange, "Эпик обновлен", 200);
                    } else {
                        httpTaskManager.createEpic(epic);
                        sendResponse(httpExchange, "Эпик создан", 200);
                    }
                    break;

                case SUBTASK:
                    Subtask subtask = gson.fromJson(jsonRequest, Subtask.class);
                    if (subtask.getTaskType() == null || subtask.getHeader() == null || subtask.getDescription() == null
                            || subtask.getStatus() == null || subtask.getDurationInMinutes() == null) {
                        sendResponse(httpExchange, "Значение null может быть только в поле startTime и " +
                                "endTime", 400);
                        return;
                    } else if (subtask.getHeader().isBlank()) {
                        sendResponse(httpExchange, "Заголовок не может быть пустыми", 400);
                        return;
                    } else if (subtask.getTaskType() != TaskType.SUBTASK) {
                        sendResponse(httpExchange, "Передан JSON c некорректным типом задачи",
                                400);
                        return;
                    }

                    if (subtask.getId() > 0) {
                        httpTaskManager.updateSubtask(subtask);
                        sendResponse(httpExchange, "Подзадача обновлена", 200);
                    } else {
                        httpTaskManager.createSubtask(subtask);
                        sendResponse(httpExchange, "Подзадача создана", 200);
                    }
            }
        } catch (JsonSyntaxException exception) {
            sendResponse(httpExchange, "Получен некорректный JSON", 400);
        } catch (IllegalArgumentException | IllegalStateException exception) {
            sendResponse(httpExchange, exception.getMessage(), 400);
        }
    }

    //Обработка удаления всех задач:
    private void handleDeleteAnyTypeOfTasks(HttpExchange httpExchange, String path) throws IOException {
        TaskType requestType = TaskType.valueOf(path.split("/")[2].toUpperCase());
        switch (requestType) {
            case TASK:
                httpTaskManager.deleteTasks();
                break;
            case EPIC:
                httpTaskManager.deleteEpics();
                break;
            case SUBTASK:
                httpTaskManager.deleteSubtasks();
        }
        httpExchange.sendResponseHeaders(200, 0);
    }

    //Обработка удаления по идентификатору:
    private void handleDeleteAnyTypeOfTaskById(HttpExchange httpExchange, String path) throws IOException {
        String taskType = path.split("/")[2];
        TaskType requestType = TaskType.valueOf(taskType.toUpperCase());
        String pathId = path.replaceFirst("/tasks/" + taskType + "/\\?id=", "");
        long id = parsePathId(pathId);
        if (id != -1) {
            try {
                switch (requestType) {
                    case TASK:
                        httpTaskManager.deleteTaskById(id);
                        break;
                    case EPIC:
                        httpTaskManager.deleteEpicById(id);
                        break;
                    case SUBTASK:
                        httpTaskManager.deleteSubtaskById(id);
                }
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
                sendResponse(httpExchange, exception.getMessage(), 404);
            }
            httpExchange.sendResponseHeaders(200, 0);
        } else {
            System.out.println("Получен некорректный ID " + pathId);
            httpExchange.sendResponseHeaders(405, 0);
        }
    }
}
