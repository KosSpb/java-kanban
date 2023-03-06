package managers;

import enums.CurrentStatus;
import network.KVServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    private KVServer kvServer;

    @BeforeEach
    void beforeEach() {
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

        taskManager = new HttpTaskManager("http://localhost:8078/");

        List<Epic> epicsBeforeAnyCreated = taskManager.getEpicList();
        assertNotNull(epicsBeforeAnyCreated, "Задачи не возвращаются.");
        assertEquals(0, epicsBeforeAnyCreated.size(), "Список эпиков не пуст.");

        epic = new Epic("existingEpicHeader", "existingEpicDescription");
        savedEpic = taskManager.createEpic(epic);
        assertNotNull(savedEpic, "Задача не найдена.");
        assertEquals(savedEpic, epic, "Задачи не совпадают.");

        List<Task> tasksBeforeAnyCreated = taskManager.getTaskList();
        assertNotNull(tasksBeforeAnyCreated, "Задачи не возвращаются.");
        assertEquals(0, tasksBeforeAnyCreated.size(), "Список задач не пуст.");

        task = new Task("existingTaskHeader", "existingTaskDescription", CurrentStatus.NEW,
                LocalDateTime.of(2023, 2, 18, 10, 0), 60);
        savedTask = taskManager.createTask(task);
        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(savedTask, task, "Задачи не совпадают.");

        List<Subtask> subtasksBeforeAnyCreated = taskManager.getSubtaskList();
        assertNotNull(subtasksBeforeAnyCreated, "Задачи не возвращаются.");
        assertEquals(0, subtasksBeforeAnyCreated.size(), "Список подзадач не пуст.");

        subtask = new Subtask(1, "existingSubHeader", "existingSubDescription",
                CurrentStatus.NEW, null, 0);
        savedSubtask = taskManager.createSubtask(subtask);
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(savedSubtask, subtask, "Задачи не совпадают.");
    }

    @AfterEach
    void afterEach() {
        kvServer.stop();
    }

    @Test
    void loadFromKVServerTestWithAllEmptyTaskMapsAndEmptyHistory() {
        kvServer.stop();
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

        taskManager = new HttpTaskManager("http://localhost:8078/");

        assertEquals(0, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в " +
                "истории.");

        taskManager.deleteTasks();

        HttpTaskManager restoredEmptyHttpTaskManager = new HttpTaskManager("http://localhost:8078/");
        restoredEmptyHttpTaskManager.loadFromKVServer();

                assertNotNull(restoredEmptyHttpTaskManager.getTaskList(), "Задачи не возвращаются.");
        assertEquals(0, restoredEmptyHttpTaskManager.getTaskList().size(),
                "Неверное количество задач.");
        assertNotNull(restoredEmptyHttpTaskManager.getEpicList(), "Эпики не возвращаются.");
        assertEquals(0, restoredEmptyHttpTaskManager.getEpicList().size(),
                "Неверное количество задач.");
        assertNotNull(restoredEmptyHttpTaskManager.getSubtaskList(), "Подзадачи не возвращаются.");
        assertEquals(0, restoredEmptyHttpTaskManager.getSubtaskList().size(),
                "Неверное количество подзадач.");
        assertNotNull(restoredEmptyHttpTaskManager.getHistory(), "История не возвращается.");
        assertEquals(0, restoredEmptyHttpTaskManager.getHistory().size(),
                "Неверное количество просмотров в истории.");
    }

    @Test
    void loadFromKVServerTestWith1NullTimedTaskAnd1TimedTaskAnd1EpicAnd2TimedSubtasksAndWithHistoryOfView() {
        kvServer.stop();
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

        taskManager = new HttpTaskManager("http://localhost:8078/");

        assertEquals(0, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в " +
                "истории.");

        Task task1 = new Task("newTaskHeader1", "newTaskDescription1", CurrentStatus.NEW,
                null, 0);
        Task task2 = new Task("newTaskHeader2", "newTaskDescription2", CurrentStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 2, 5, 1, 5), 25);
        Epic epic1 = new Epic("newEpicHeader", "newEpicDescription");
        Subtask subtask1 = new Subtask(3, "newSubHeader1", "newSubDescription1",
                CurrentStatus.NEW, LocalDateTime.of(2023, 2, 6, 12, 30),
                350);
        Subtask subtask2 = new Subtask(3, "newSubHeader2", "newSubDescription2",
                CurrentStatus.IN_PROGRESS, LocalDateTime.of(2023, 1, 3, 9, 10),
                150);

        Task savedTask1 = taskManager.createTask(task1);
        Task savedTask2 = taskManager.createTask(task2);
        Epic savedEpic = taskManager.createEpic(epic1);
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);
        Subtask savedSubtask2 = taskManager.createSubtask(subtask2);
        taskManager.getEpicById(3);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(5);
        taskManager.getTaskById(2);
        taskManager.getSubtaskById(4);
        LocalDateTime savedEpicStartTime = savedEpic.getStartTime();
        LocalDateTime savedEpicEndTime = savedEpic.getEndTime();
        Duration savedEpicDurationInMinutes = savedEpic.getDurationInMinutes();

        assertEquals(2, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(1, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(2, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(5, taskManager.getHistory().size(), "Неверное количество просмотров в " +
                "истории.");

        HttpTaskManager restoredHttpTaskManagerWithData = new HttpTaskManager("http://localhost:8078/");
        restoredHttpTaskManagerWithData.loadFromKVServer();

        assertNotNull(restoredHttpTaskManagerWithData.getTaskList(), "Задачи не возвращаются.");
        assertEquals(2, restoredHttpTaskManagerWithData.getTaskList().size(),
                "Неверное количество задач.");
        assertEquals(savedTask1, restoredHttpTaskManagerWithData.getTaskList().get(0),
                "Задачи не совпадают.");
        assertEquals(savedTask2, restoredHttpTaskManagerWithData.getTaskList().get(1),
                "Задачи не совпадают.");
        assertNotNull(restoredHttpTaskManagerWithData.getEpicList(), "Эпики не возвращаются.");
        assertEquals(1, restoredHttpTaskManagerWithData.getEpicList().size(),
                "Неверное количество задач.");
        assertEquals(savedEpic, restoredHttpTaskManagerWithData.getEpicList().get(0),
                "Эпики не совпадают.");
        assertEquals(savedEpicStartTime, restoredHttpTaskManagerWithData.getEpicList().get(0).getStartTime(),
                "Начальное время не совпадает.");
        assertEquals(savedEpicEndTime, restoredHttpTaskManagerWithData.getEpicList().get(0).getEndTime(),
                "Конечное время не совпадает");
        assertEquals(savedEpicDurationInMinutes,
                restoredHttpTaskManagerWithData.getEpicList().get(0).getDurationInMinutes(),
                "Продолжительность не совпадает.");
        assertNotNull(restoredHttpTaskManagerWithData.getSubtaskList(), "Подзадачи не " +
                "возвращаются.");
        assertEquals(2, restoredHttpTaskManagerWithData.getSubtaskList().size(),
                "Неверное количество подзадач.");
        assertEquals(savedSubtask1, restoredHttpTaskManagerWithData.getSubtaskList().get(0),
                "Подзадачи не совпадают.");
        assertEquals(savedSubtask2, restoredHttpTaskManagerWithData.getSubtaskList().get(1),
                "Подзадачи не совпадают.");
        assertNotNull(restoredHttpTaskManagerWithData.getHistory(), "История не возвращается.");
        assertEquals(5, restoredHttpTaskManagerWithData.getHistory().size(),
                "Неверное количество просмотров в истории.");
        assertEquals(List.of(savedEpic, savedTask1, savedSubtask2, savedTask2, savedSubtask1),
                restoredHttpTaskManagerWithData.getHistory(), "Неверный порядок просмотров.");
    }

    @Test
    void loadFromKVServerTestWith1EpicWithoutSubtasksAnd1TimedTaskAndWithHistoryOfView() {
        kvServer.stop();
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

        taskManager = new HttpTaskManager("http://localhost:8078/");

        assertEquals(0, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в " +
                "истории.");

        Task task1 = new Task("newTaskHeader2", "newTaskDescription2", CurrentStatus.IN_PROGRESS,
                LocalDateTime.of(2023, 2, 5, 1, 5), 25);
        Epic epic1 = new Epic("newEpicHeader", "newEpicDescription");

        Task savedTask1 = taskManager.createTask(task1);
        Epic savedEpic = taskManager.createEpic(epic1);

        taskManager.getTaskById(1);
        taskManager.getEpicById(2);

        assertEquals(1, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(1, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(2, taskManager.getHistory().size(), "Неверное количество просмотров в " +
                "истории.");

        HttpTaskManager restoredHttpTaskManagerWithoutSubtasks =
                new HttpTaskManager("http://localhost:8078/");
        restoredHttpTaskManagerWithoutSubtasks.loadFromKVServer();

        assertNotNull(restoredHttpTaskManagerWithoutSubtasks.getTaskList(), "Задачи не " +
                "возвращаются.");
        assertEquals(1, restoredHttpTaskManagerWithoutSubtasks.getTaskList().size(),
                "Неверное количество задач.");
        assertEquals(savedTask1, restoredHttpTaskManagerWithoutSubtasks.getTaskList().get(0),
                "Задачи не совпадают.");
        assertNotNull(restoredHttpTaskManagerWithoutSubtasks.getEpicList(), "Эпики не " +
                "возвращаются.");
        assertEquals(1, restoredHttpTaskManagerWithoutSubtasks.getEpicList().size(),
                "Неверное количество задач.");
        assertEquals(savedEpic, restoredHttpTaskManagerWithoutSubtasks.getEpicList().get(0),
                "Эпики не совпадают.");
        assertNull(restoredHttpTaskManagerWithoutSubtasks.getEpicList().get(0).getStartTime(),
                "Начальное время не совпадает.");
        assertNull(restoredHttpTaskManagerWithoutSubtasks.getEpicList().get(0).getEndTime(),
                "Конечное время не совпадает");
        assertEquals(Duration.ofMinutes(0),
                restoredHttpTaskManagerWithoutSubtasks.getEpicList().get(0).getDurationInMinutes(),
                "Продолжительность не совпадает.");
        assertNotNull(restoredHttpTaskManagerWithoutSubtasks.getSubtaskList(),
                "Подзадачи не возвращаются.");
        assertEquals(0, restoredHttpTaskManagerWithoutSubtasks.getSubtaskList().size(),
                "Неверное количество подзадач.");
        assertNotNull(restoredHttpTaskManagerWithoutSubtasks.getHistory(), "История не " +
                "возвращается.");
        assertEquals(2, restoredHttpTaskManagerWithoutSubtasks.getHistory().size(),
                "Неверное количество просмотров в истории.");
        assertEquals(List.of(savedTask1, savedEpic),
                restoredHttpTaskManagerWithoutSubtasks.getHistory(), "Неверный порядок просмотров.");
    }

    @Test
    void loadFromKVServerTestWithEmptyHistoryOfViewWith1TimedTaskAnd1EpicWith2NullTimedSubtasks() {
        kvServer.stop();
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }

        taskManager = new HttpTaskManager("http://localhost:8078/");

        assertEquals(0, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в " +
                "истории.");

        Task task1 = new Task("newTaskHeader2", "newTaskDescription2", CurrentStatus.NEW,
                LocalDateTime.of(2023, 2, 5, 1, 5), 25);
        Epic epic1 = new Epic("newEpicHeader", "newEpicDescription");
        Subtask subtask1 = new Subtask(2, "newSubHeader1", "newSubDescription1",
                CurrentStatus.NEW, null, 0);
        Subtask subtask2 = new Subtask(2, "newSubHeader2", "newSubDescription2",
                CurrentStatus.IN_PROGRESS, null, 0);

        Task savedTask1 = taskManager.createTask(task1);
        Epic savedEpic = taskManager.createEpic(epic1);
        Subtask savedSubtask1 = taskManager.createSubtask(subtask1);
        Subtask savedSubtask2 = taskManager.createSubtask(subtask2);

        assertEquals(1, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(1, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(2, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в " +
                "истории.");

        HttpTaskManager restoredHttpTaskManagerWithEmptyHistory =
                new HttpTaskManager("http://localhost:8078/");
        restoredHttpTaskManagerWithEmptyHistory.loadFromKVServer();

        assertNotNull(restoredHttpTaskManagerWithEmptyHistory.getTaskList(), "Задачи не " +
                "возвращаются.");
        assertEquals(1, restoredHttpTaskManagerWithEmptyHistory.getTaskList().size(),
                "Неверное количество задач.");
        assertEquals(savedTask1, restoredHttpTaskManagerWithEmptyHistory.getTaskList().get(0),
                "Задачи не совпадают.");
        assertNotNull(restoredHttpTaskManagerWithEmptyHistory.getEpicList(), "Эпики не " +
                "возвращаются.");
        assertEquals(1, restoredHttpTaskManagerWithEmptyHistory.getEpicList().size(),
                "Неверное количество задач.");
        assertEquals(savedEpic, restoredHttpTaskManagerWithEmptyHistory.getEpicList().get(0),
                "Эпики не совпадают.");
        assertNotNull(restoredHttpTaskManagerWithEmptyHistory.getSubtaskList(),
                "Подзадачи не возвращаются.");
        assertEquals(2, restoredHttpTaskManagerWithEmptyHistory.getSubtaskList().size(),
                "Неверное количество подзадач.");
        assertEquals(savedSubtask1, restoredHttpTaskManagerWithEmptyHistory.getSubtaskList().get(0),
                "Подзадачи не совпадают.");
        assertEquals(savedSubtask2, restoredHttpTaskManagerWithEmptyHistory.getSubtaskList().get(1),
                "Подзадачи не совпадают.");
        assertNotNull(restoredHttpTaskManagerWithEmptyHistory.getHistory(), "История не " +
                "возвращается.");
        assertEquals(0, restoredHttpTaskManagerWithEmptyHistory.getHistory().size(),
                "Неверное количество просмотров в истории.");
    }
}