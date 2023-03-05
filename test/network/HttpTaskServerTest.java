package network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import enums.CurrentStatus;
import managers.Managers;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {
    private HttpTaskServer httpTaskServer;
    private KVServer kvServer;
    private TaskManager httpTaskManager;
    private HttpClient client;
    private final Gson gson = Managers.getGson();
    private Epic epic;
    private Epic savedEpic;
    private Subtask subtask;
    private Subtask savedSubtask;
    private Task task;
    private Task savedTask;

    @BeforeEach
    void beforeEach() {
        try {
            kvServer = new KVServer();
            kvServer.start();
            httpTaskManager = Managers.getDefault();
            httpTaskServer = new HttpTaskServer(httpTaskManager);
            client = HttpClient.newHttpClient();

            List<Epic> epicsBeforeAnyCreated = httpTaskManager.getEpicList();
            assertNotNull(epicsBeforeAnyCreated, "Задачи не возвращаются.");
            assertEquals(0, epicsBeforeAnyCreated.size(), "Список эпиков не пуст.");

            epic = new Epic("existingEpicHeader", "existingEpicDescription");
            savedEpic = httpTaskManager.createEpic(epic);
            assertNotNull(savedEpic, "Задача не найдена.");
            assertEquals(savedEpic, epic, "Задачи не совпадают.");

            List<Epic> epicsAfter1EpicCreated = httpTaskManager.getEpicList();
            assertNotNull(epicsAfter1EpicCreated, "Задачи не возвращаются.");
            assertEquals(1, epicsAfter1EpicCreated.size(), "Список эпиков пуст.");

            List<Task> tasksBeforeAnyCreated = httpTaskManager.getTaskList();
            assertNotNull(tasksBeforeAnyCreated, "Задачи не возвращаются.");
            assertEquals(0, tasksBeforeAnyCreated.size(), "Список задач не пуст.");

            task = new Task("existingTaskHeader", "existingTaskDescription", CurrentStatus.NEW,
                    LocalDateTime.of(2023, 2, 18, 10, 0), 60);
            savedTask = httpTaskManager.createTask(task);
            assertNotNull(savedTask, "Задача не найдена.");
            assertEquals(savedTask, task, "Задачи не совпадают.");

            List<Task> tasksAfter1TaskCreated = httpTaskManager.getTaskList();
            assertNotNull(tasksAfter1TaskCreated, "Задачи не возвращаются.");
            assertEquals(1, tasksAfter1TaskCreated.size(), "Список задач пуст.");

            List<Subtask> subtasksBeforeAnyCreated = httpTaskManager.getSubtaskList();
            assertNotNull(subtasksBeforeAnyCreated, "Задачи не возвращаются.");
            assertEquals(0, subtasksBeforeAnyCreated.size(), "Список подзадач не пуст.");

            subtask = new Subtask(1, "existingSubHeader", "existingSubDescription",
                    CurrentStatus.NEW, null, 0);
            savedSubtask = httpTaskManager.createSubtask(subtask);
            assertNotNull(savedSubtask, "Задача не найдена.");
            assertEquals(savedSubtask, subtask, "Задачи не совпадают.");

            List<Subtask> subtasksAfter1SubtaskCreated = httpTaskManager.getSubtaskList();
            assertNotNull(subtasksAfter1SubtaskCreated, "Задачи не возвращаются.");
            assertEquals(1, subtasksAfter1SubtaskCreated.size(), "Список подзадач пуст.");

            httpTaskServer.start();

        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }

    @AfterEach
    void afterEach() {
        httpTaskServer.stop();
        kvServer.stop();
    }

    @Test
    void shouldResponseWithStatus405WhenGetRequestWasSendWithWrongPath() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/ta");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("Путь /tasks/ta не соответствует ожидаемому.", response.body());
    }

    @Test
    void shouldResponseWithStatus405WhenRequestWasSendWithoutGetPostOrDeleteMethodsAndWithCorrectPath()
            throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task");
        HttpRequest request = HttpRequest.newBuilder().uri(url).PUT(HttpRequest.BodyPublishers.ofString(""))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("Ожидается GET, POST или DELETE запрос, а поступил - PUT", response.body());
    }

    @Test
    void getTasksTaskEndpointTestWithEmptyMap() throws IOException, InterruptedException {
        httpTaskManager.deleteTasks();
        List<Task> tasks = httpTaskManager.getTaskList();
        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), taskType);

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(0, actualTasks.size());
    }

    @Test
    void getTasksTaskEndpointTestWith1TaskCreated() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), taskType);

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(1, actualTasks.size(), "Неверное количество задач.");
        assertEquals(task, actualTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getTasksEpicEndpointTestWithEmptyMap() throws IOException, InterruptedException {
        httpTaskManager.deleteEpics();
        List<Epic> epics = httpTaskManager.getEpicList();
        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>(){}.getType();
        List<Epic> actualEpics = gson.fromJson(response.body(), taskType);

        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(0, actualEpics.size());
    }

    @Test
    void getTasksEpicEndpointTestWith1EpicCreated() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Epic>>(){}.getType();
        List<Epic> actualEpics = gson.fromJson(response.body(), taskType);

        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(1, actualEpics.size(), "Неверное количество эпиков.");
        assertEquals(epic, actualEpics.get(0), "Эпики не совпадают.");
    }

    @Test
    void getTasksSubtaskEndpointTestWithEmptyMap() throws IOException, InterruptedException {
        httpTaskManager.deleteSubtasks();
        List<Subtask> subtasks = httpTaskManager.getSubtaskList();
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        List<Subtask> actualSubtasks = gson.fromJson(response.body(), taskType);

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(0, actualSubtasks.size());
    }

    @Test
    void getTasksSubtaskEndpointTestWith1SubtaskCreated() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        List<Subtask> actualSubtasks = gson.fromJson(response.body(), taskType);

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(1, actualSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, actualSubtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getTasksHistoryEndpointTestWithEmptyHistory() throws IOException, InterruptedException {
        List<Task> emptyHistoryOfView = httpTaskManager.getHistory();
        assertNotNull(emptyHistoryOfView, "Задачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Список истории просмотров не пуст.");

        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), taskType);

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(0, actualTasks.size());
    }

    @Test
    void getTasksHistoryEndpointTestAfter1TaskGotten() throws IOException, InterruptedException {
        List<Task> emptyHistoryOfView = httpTaskManager.getHistory();
        assertNotNull(emptyHistoryOfView, "Задачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Неверное количество просмотров.");

        httpTaskManager.getTaskById(2);

        List<Task> historyOfViewAfter1TaskGotten = httpTaskManager.getHistory();
        assertNotNull(historyOfViewAfter1TaskGotten, "Задачи не возвращаются.");
        assertEquals(1, historyOfViewAfter1TaskGotten.size(), "Неверное количество просмотров.");

        URI url = URI.create("http://localhost:8080/tasks/history/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), taskType);

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(1, actualTasks.size(), "Неверное количество задач.");
        assertEquals(task, actualTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getTasksEndpointTestWith1TaskAnd1SubtaskCreatedShouldResponseByPrioritizedTasksList()
            throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), taskType);
        Type subtaskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        List<Subtask> actualSubtasks = gson.fromJson(response.body(), subtaskType);

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(2, actualTasks.size(), "Неверное количество задач.");
        assertEquals(task, actualTasks.get(0), "Задачи не совпадают.");
        assertEquals(subtask, actualSubtasks.get(1), "Подзадачи не совпадают.");
    }

    @Test
    void getTasksEndpointTestWithEmptySetShouldResponseByEmptyPrioritizedTasksList()
            throws IOException, InterruptedException {
        httpTaskManager.deleteTasks();
        httpTaskManager.deleteEpics();
        List<Task> prioritizedTasks = httpTaskManager.getPrioritizedTasks();
        assertNotNull(prioritizedTasks, "Задачи не возвращаются.");
        assertEquals(0, prioritizedTasks.size(), "Неверное количество задач.");

        URI url = URI.create("http://localhost:8080/tasks/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Task>>(){}.getType();
        List<Task> actualTasks = gson.fromJson(response.body(), taskType);

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(0, actualTasks.size());
    }

    @Test
    void getTasksSubtaskEpicIdEndpointTestWithCorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Type taskType = new TypeToken<ArrayList<Subtask>>(){}.getType();
        List<Subtask> actualSubtasks = gson.fromJson(response.body(), taskType);

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(1, actualSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, actualSubtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getTasksSubtaskEpicIdEndpointTestWithIncorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/epic/?id=0");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Эпика с ID '0' не существует, либо список его подзадач пуст.", response.body());
    }

    @Test
    void getTasksTaskIdEndpointTestWithCorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task actualTask = gson.fromJson(response.body(), Task.class);

        assertEquals(task, actualTask, "Задачи не совпадают.");
    }

    @Test
    void getTasksTaskIdEndpointTestWithIncorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=55");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задачи с ID '55' не существует.", response.body());
    }

    @Test
    void getTasksEpicIdEndpointTestWithCorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic actualEpic = gson.fromJson(response.body(), Epic.class);

        assertEquals(epic, actualEpic, "Эпики не совпадают.");
    }

    @Test
    void getTasksEpicIdEndpointTestWithIncorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=33");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задачи с ID '33' не существует.", response.body());
    }

    @Test
    void getTasksSubtaskIdEndpointTestWithCorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask actualSubtask = gson.fromJson(response.body(), Subtask.class);

        assertEquals(subtask, actualSubtask, "Задачи не совпадают.");
    }

    @Test
    void getTasksSubtaskIdEndpointTestWithIncorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=77");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задачи с ID '77' не существует.", response.body());
    }

    @Test
    void postTasksTaskEndpointTestShouldCreateNewTask() throws IOException, InterruptedException {
        Task postTask = new Task("postTaskHeader", "postTaskDescription", CurrentStatus.NEW,
                LocalDateTime.of(2023,1,31,9,30), 70);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postTask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача создана", response.body());

        List<Task> actualTasks = httpTaskManager.getTaskList();
        postTask.setId(actualTasks.get(1).getId());

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(2, actualTasks.size(), "Неверное количество задач.");
        assertEquals(postTask, actualTasks.get(1), "Задачи не совпадают.");
    }

    @Test
    void postTasksTaskEndpointTestShouldUpdateExistingTask() throws IOException, InterruptedException {
        Task postTask = new Task("postTaskHeader", "postTaskDescription", CurrentStatus.NEW,
                LocalDateTime.of(2023,1,31,9,30), 70);
        postTask.setId(2);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postTask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Задача обновлена", response.body());

        List<Task> actualTasks = httpTaskManager.getTaskList();

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(1, actualTasks.size(), "Неверное количество задач.");
        assertEquals(postTask, actualTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void postTasksTaskEndpointTestShouldResponseWith400IfOneOfFieldIsNullExceptStartTimeOrEndTime()
            throws IOException, InterruptedException {
        Task postTask = new Task(null, "postTaskDescription", CurrentStatus.NEW,
                LocalDateTime.of(2023,1,31,9,30), 70);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postTask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Значение null может быть только в поле startTime и endTime", response.body());

        List<Task> actualTasks = httpTaskManager.getTaskList();

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(1, actualTasks.size(), "Неверное количество задач.");
        assertEquals(task, actualTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void postTasksTaskEndpointTestShouldResponseWith400IfHeaderIsBlank() throws IOException, InterruptedException {
        Task postTask = new Task("", "postTaskDescription", CurrentStatus.NEW,
                LocalDateTime.of(2023,1,31,9,30), 70);

        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postTask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Заголовок не может быть пустыми", response.body());

        List<Task> actualTasks = httpTaskManager.getTaskList();

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(1, actualTasks.size(), "Неверное количество задач.");
        assertEquals(task, actualTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void postTasksEpicEndpointTestShouldCreateNewEpic() throws IOException, InterruptedException {
        Epic postEpic = new Epic("postEpicHeader", "postEpicDescription");

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postEpic));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Эпик создан", response.body());

        List<Epic> actualEpics = httpTaskManager.getEpicList();
        postEpic.setId(actualEpics.get(1).getId());

        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(2, actualEpics.size(), "Неверное количество эпиков.");
        assertEquals(postEpic, actualEpics.get(1), "Эпики не совпадают.");
    }

    @Test
    void postTasksEpicEndpointTestShouldUpdateExistingEpic() throws IOException, InterruptedException {
        Epic postEpic = new Epic("postEpicHeader", "postEpicDescription");
        postEpic.setId(1);

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postEpic));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Эпик обновлен", response.body());

        List<Epic> actualEpics = httpTaskManager.getEpicList();
        postEpic.setSubtaskIds(List.of(httpTaskManager.getSubtaskById(3).getId()));

        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(1, actualEpics.size(), "Неверное количество эпиков.");
        assertEquals(postEpic, actualEpics.get(0), "Эпики не совпадают.");
    }

    @Test
    void postTasksEpicEndpointTestShouldResponseWith400IfOneOfFieldIsNullExceptStartTimeOrEndTime()
            throws IOException, InterruptedException {
        Epic postEpic = new Epic("postEpicHeader", null);

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postEpic));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Значение null может быть только в поле startTime и endTime", response.body());

        List<Epic> actualEpics = httpTaskManager.getEpicList();

        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(1, actualEpics.size(), "Неверное количество эпиков.");
        assertEquals(epic, actualEpics.get(0), "Эпики не совпадают.");
    }

    @Test
    void postTasksEpicEndpointTestShouldResponseWith400IfHeaderIsBlank() throws IOException, InterruptedException {
        Epic postEpic = new Epic("", "postEpicDescription");

        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postEpic));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Заголовок не может быть пустыми", response.body());

        List<Epic> actualEpics = httpTaskManager.getEpicList();

        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(1, actualEpics.size(), "Неверное количество эпиков.");
        assertEquals(epic, actualEpics.get(0), "Эпики не совпадают.");
    }

    @Test
    void postTasksSubtaskEndpointTestShouldCreateNewSubtask() throws IOException, InterruptedException {
        Subtask postSubtask = new Subtask(1, "postSubtaskHeader", "postSubtaskDescription",
                CurrentStatus.DONE, LocalDateTime.of(2023,1,31,9,30),
                70);

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postSubtask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Подзадача создана", response.body());

        List<Subtask> actualSubtasks = httpTaskManager.getSubtaskList();
        postSubtask.setId(actualSubtasks.get(1).getId());

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(2, actualSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(postSubtask, actualSubtasks.get(1), "Подзадачи не совпадают.");
    }

    @Test
    void postTasksSubtaskEndpointTestShouldUpdateExistingSubtask() throws IOException, InterruptedException {
        Subtask postSubtask = new Subtask(1, "postSubtaskHeader", "postSubtaskDescription",
                CurrentStatus.DONE, LocalDateTime.of(2023,1,31,9,30),
                70);
        postSubtask.setId(3);

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postSubtask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("Подзадача обновлена", response.body());

        List<Subtask> actualSubtasks = httpTaskManager.getSubtaskList();

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(1, actualSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(postSubtask, actualSubtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void postTasksSubtaskEndpointTestShouldResponseWith400IfOneOfFieldIsNullExceptStartTimeOrEndTime()
            throws IOException, InterruptedException {
        Subtask postSubtask = new Subtask(1, "postSubtaskHeader", "postSubtaskDescription",
                null, LocalDateTime.of(2023,1,31,9,30),
                70);

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postSubtask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Значение null может быть только в поле startTime и endTime", response.body());

        List<Subtask> actualSubtasks = httpTaskManager.getSubtaskList();

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(1, actualSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, actualSubtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void postTasksSubtaskEndpointTestShouldResponseWith400IfHeaderIsBlank() throws IOException, InterruptedException {
        Subtask postSubtask = new Subtask(1, "", "postSubtaskDescription",
                CurrentStatus.IN_PROGRESS, LocalDateTime.of(2023,1,31,9,30),
                70);

        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postSubtask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode());
        assertEquals("Заголовок не может быть пустыми", response.body());

        List<Subtask> actualSubtasks = httpTaskManager.getSubtaskList();

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(1, actualSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, actualSubtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void shouldResponseWithStatus405WhenPostRequestWasSendWithWrongPath() throws IOException, InterruptedException {
        Subtask postSubtask = new Subtask(1, "postSubtaskHeader", "postSubtaskDescription",
                CurrentStatus.DONE, LocalDateTime.of(2023,1,31,9,30),
                70);

        URI url = URI.create("http://localhost:8080/tasks/subt/");
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(gson.toJson(postSubtask));
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(405, response.statusCode());
        assertEquals("Путь /tasks/subt/ не соответствует ожидаемому.", response.body());

        List<Subtask> actualSubtasks = httpTaskManager.getSubtaskList();

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(1, actualSubtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, actualSubtasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void shouldResponseWithStatus405WhenDeleteRequestWasSendWithWrongPath() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epi");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(405, response.statusCode());
        assertEquals("Путь /tasks/epi не соответствует ожидаемому.", response.body());
    }

    @Test
    void deleteTasksTaskEndpointTestWith1TaskCreated() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Task> actualTasks = httpTaskManager.getTaskList();

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(0, actualTasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteTasksEpicEndpointTestWith1EpicCreated() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Epic> actualEpics = httpTaskManager.getEpicList();

        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(0, actualEpics.size(), "Неверное количество эпиков.");
    }

    @Test
    void deleteTasksSubtaskEndpointTestWith1SubtaskCreated() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        List<Subtask> actualSubtasks = httpTaskManager.getSubtaskList();

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(0, actualSubtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void deleteTasksTaskIdEndpointTestWithCorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task actualTask = httpTaskManager.getTaskById(2);
        List<Task> actualTasks = httpTaskManager.getTaskList();

        assertNull(actualTask, "Задача не null");
        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(0, actualTasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteTasksTaskIdEndpointTestWithIncorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/task/?id=55");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Задача с ID '55' отсутствует, либо уже была удалена.", response.body());

        List<Task> actualTasks = httpTaskManager.getTaskList();

        assertNotNull(actualTasks, "Задачи не возвращаются.");
        assertEquals(1, actualTasks.size(), "Неверное количество задач.");
    }

    @Test
    void deleteTasksEpicIdEndpointTestWithCorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Epic actualEpic = httpTaskManager.getEpicById(1);
        List<Epic> actualEpics = httpTaskManager.getEpicList();

        assertNull(actualEpic, "Эпик не null");
        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(0, actualEpics.size(), "Неверное количество эпиков.");
    }

    @Test
    void deleteTasksEpicIdEndpointTestWithIncorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/epic/?id=33");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Эпик с ID '33' отсутствует, либо уже был удален.", response.body());

        List<Epic> actualEpics = httpTaskManager.getEpicList();

        assertNotNull(actualEpics, "Эпики не возвращаются.");
        assertEquals(1, actualEpics.size(), "Неверное количество эпиков.");
    }

    @Test
    void deleteTasksSubtaskIdEndpointTestWithCorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Subtask actualSubtask = httpTaskManager.getSubtaskById(3);
        List<Subtask> actualSubtasks = httpTaskManager.getSubtaskList();

        assertNull(actualSubtask, "Подзадача не null");
        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(0, actualSubtasks.size(), "Неверное количество подзадач.");
    }

    @Test
    void deleteTasksSubtaskIdEndpointTestWithIncorrectId() throws IOException, InterruptedException {
        URI url = URI.create("http://localhost:8080/tasks/subtask/?id=77");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Подзадача с ID '77' отсутствует, либо уже была удалена.", response.body());

        List<Subtask> actualSubtasks = httpTaskManager.getSubtaskList();

        assertNotNull(actualSubtasks, "Подзадачи не возвращаются.");
        assertEquals(1, actualSubtasks.size(), "Неверное количество подзадач.");
    }
}