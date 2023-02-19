package managers;

import enums.CurrentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {
    private final Path path = Paths.get("resources/FileBackedTasks.csv");

    @BeforeEach
    public void beforeEach(TestInfo info) {
        taskManager = new FileBackedTasksManager(path.toFile());
        if (!info.getDisplayName().contains("Empty") && !info.getDisplayName().contains("NullAsArgument")
                && !info.getDisplayName().contains("loadFromFile")) {
            epic = new Epic("existingEpicHeader", "existingEpicDescription");
            savedEpic = taskManager.createEpic(epic);
            assertNotNull(savedEpic, "Задача не найдена.");
            assertEquals(savedEpic, epic, "Задачи не совпадают.");

            task = new Task("existingTaskHeader", "existingTaskDescription", CurrentStatus.NEW,
                    LocalDateTime.of(2023, 2, 18, 10, 0), 60);
            savedTask = taskManager.createTask(task);
            assertNotNull(savedTask, "Задача не найдена.");
            assertEquals(savedTask, task, "Задачи не совпадают.");

            if (!info.getDisplayName().contains("epicStatusCalculation")) {
                subtask = new Subtask(1, "existingSubHeader", "existingSubDescription",
                        CurrentStatus.NEW, null, 0);
                savedSubtask = taskManager.createSubtask(subtask);
                assertNotNull(savedSubtask, "Задача не найдена.");
                assertEquals(savedSubtask, subtask, "Задачи не совпадают.");
            }
        }
        if (info.getDisplayName().contains("loadFromFile")) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException exception) {
                System.out.println("Директория resources/ пуста.");
            }
        }
    }

    @Test
    void loadFromFileTestWithAllEmptyTaskMapsAndEmptyHistory() {
        assertEquals(0, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в истории.");
        assertFalse(Files.exists(path));

        taskManager.deleteTasks();

        assertTrue(Files.exists(path));

        TaskManager restoredEmptyFileBackedTasksManager = FileBackedTasksManager.loadFromFile(path.toFile());

        assertNotNull(restoredEmptyFileBackedTasksManager.getTaskList(), "Задачи не возвращаются.");
        assertEquals(0, restoredEmptyFileBackedTasksManager.getTaskList().size(),
                "Неверное количество задач.");
        assertNotNull(restoredEmptyFileBackedTasksManager.getEpicList(), "Эпики не возвращаются.");
        assertEquals(0, restoredEmptyFileBackedTasksManager.getEpicList().size(),
                "Неверное количество задач.");
        assertNotNull(restoredEmptyFileBackedTasksManager.getSubtaskList(), "Подзадачи не возвращаются.");
        assertEquals(0, restoredEmptyFileBackedTasksManager.getSubtaskList().size(),
                "Неверное количество подзадач.");
        assertNotNull(restoredEmptyFileBackedTasksManager.getHistory(), "История не возвращается.");
        assertEquals(0, restoredEmptyFileBackedTasksManager.getHistory().size(),
                "Неверное количество просмотров в истории.");
    }

    @Test
    void loadFromFileTestWith1NullTimedTaskAnd1TimedTaskAnd1EpicAnd2TimedSubtasksAndWithHistoryOfView() {
        assertEquals(0, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в истории.");
        assertFalse(Files.exists(path));

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
        assertEquals(5, taskManager.getHistory().size(), "Неверное количество просмотров в истории.");
        assertTrue(Files.exists(path));

        TaskManager restoredFileBackedTasksManagerWithData = FileBackedTasksManager.loadFromFile(path.toFile());

        assertNotNull(restoredFileBackedTasksManagerWithData.getTaskList(), "Задачи не возвращаются.");
        assertEquals(2, restoredFileBackedTasksManagerWithData.getTaskList().size(),
                "Неверное количество задач.");
        assertEquals(savedTask1, restoredFileBackedTasksManagerWithData.getTaskList().get(0),
                "Задачи не совпадают.");
        assertEquals(savedTask2, restoredFileBackedTasksManagerWithData.getTaskList().get(1),
                "Задачи не совпадают.");
        assertNotNull(restoredFileBackedTasksManagerWithData.getEpicList(), "Эпики не возвращаются.");
        assertEquals(1, restoredFileBackedTasksManagerWithData.getEpicList().size(),
                "Неверное количество задач.");
        assertEquals(savedEpic, restoredFileBackedTasksManagerWithData.getEpicList().get(0),
                "Эпики не совпадают.");
        assertEquals(savedEpicStartTime, restoredFileBackedTasksManagerWithData.getEpicList().get(0).getStartTime(),
                "Начальное время не совпадает.");
        assertEquals(savedEpicEndTime, restoredFileBackedTasksManagerWithData.getEpicList().get(0).getEndTime(),
                "Конечное время не совпадает");
        assertEquals(savedEpicDurationInMinutes,
                restoredFileBackedTasksManagerWithData.getEpicList().get(0).getDurationInMinutes(),
                "Продолжительность не совпадает.");
        assertNotNull(restoredFileBackedTasksManagerWithData.getSubtaskList(), "Подзадачи не возвращаются.");
        assertEquals(2, restoredFileBackedTasksManagerWithData.getSubtaskList().size(),
                "Неверное количество подзадач.");
        assertEquals(savedSubtask1, restoredFileBackedTasksManagerWithData.getSubtaskList().get(0),
                "Подзадачи не совпадают.");
        assertEquals(savedSubtask2, restoredFileBackedTasksManagerWithData.getSubtaskList().get(1),
                "Подзадачи не совпадают.");
        assertNotNull(restoredFileBackedTasksManagerWithData.getHistory(), "История не возвращается.");
        assertEquals(5, restoredFileBackedTasksManagerWithData.getHistory().size(),
                "Неверное количество просмотров в истории.");
        assertEquals(List.of(savedEpic, savedTask1, savedSubtask2, savedTask2, savedSubtask1),
                restoredFileBackedTasksManagerWithData.getHistory(), "Неверный порядок просмотров.");
    }

    @Test
    void loadFromFileTestWith1EpicWithoutSubtasksAnd1TimedTaskAndWithHistoryOfView() {
        assertEquals(0, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в истории.");
        assertFalse(Files.exists(path));

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
        assertEquals(2, taskManager.getHistory().size(), "Неверное количество просмотров в истории.");
        assertTrue(Files.exists(path));

        TaskManager restoredFileBackedTasksManagerWithoutSubtasks = FileBackedTasksManager.loadFromFile(path.toFile());

        assertNotNull(restoredFileBackedTasksManagerWithoutSubtasks.getTaskList(), "Задачи не возвращаются.");
        assertEquals(1, restoredFileBackedTasksManagerWithoutSubtasks.getTaskList().size(),
                "Неверное количество задач.");
        assertEquals(savedTask1, restoredFileBackedTasksManagerWithoutSubtasks.getTaskList().get(0),
                "Задачи не совпадают.");
        assertNotNull(restoredFileBackedTasksManagerWithoutSubtasks.getEpicList(), "Эпики не возвращаются.");
        assertEquals(1, restoredFileBackedTasksManagerWithoutSubtasks.getEpicList().size(),
                "Неверное количество задач.");
        assertEquals(savedEpic, restoredFileBackedTasksManagerWithoutSubtasks.getEpicList().get(0),
                "Эпики не совпадают.");
        assertNull(restoredFileBackedTasksManagerWithoutSubtasks.getEpicList().get(0).getStartTime(),
                "Начальное время не совпадает.");
        assertNull(restoredFileBackedTasksManagerWithoutSubtasks.getEpicList().get(0).getEndTime(),
                "Конечное время не совпадает");
        assertEquals(Duration.ofMinutes(0),
                restoredFileBackedTasksManagerWithoutSubtasks.getEpicList().get(0).getDurationInMinutes(),
                "Продолжительность не совпадает.");
        assertNotNull(restoredFileBackedTasksManagerWithoutSubtasks.getSubtaskList(),
                "Подзадачи не возвращаются.");
        assertEquals(0, restoredFileBackedTasksManagerWithoutSubtasks.getSubtaskList().size(),
                "Неверное количество подзадач.");
        assertNotNull(restoredFileBackedTasksManagerWithoutSubtasks.getHistory(), "История не возвращается.");
        assertEquals(2, restoredFileBackedTasksManagerWithoutSubtasks.getHistory().size(),
                "Неверное количество просмотров в истории.");
        assertEquals(List.of(savedTask1, savedEpic),
                restoredFileBackedTasksManagerWithoutSubtasks.getHistory(), "Неверный порядок просмотров.");
    }

    @Test
    void loadFromFileTestWithEmptyHistoryOfViewWith1TimedTaskAnd1EpicWith2NullTimedSubtasks() {
        assertEquals(0, taskManager.getTaskList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getEpicList().size(), "Неверное количество задач.");
        assertEquals(0, taskManager.getSubtaskList().size(), "Неверное количество подзадач.");
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в истории.");
        assertFalse(Files.exists(path));

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
        assertEquals(0, taskManager.getHistory().size(), "Неверное количество просмотров в истории.");
        assertTrue(Files.exists(path));

        TaskManager restoredFileBackedTasksManagerWithEmptyHistory = FileBackedTasksManager.loadFromFile(path.toFile());

        assertNotNull(restoredFileBackedTasksManagerWithEmptyHistory.getTaskList(), "Задачи не возвращаются.");
        assertEquals(1, restoredFileBackedTasksManagerWithEmptyHistory.getTaskList().size(),
                "Неверное количество задач.");
        assertEquals(savedTask1, restoredFileBackedTasksManagerWithEmptyHistory.getTaskList().get(0),
                "Задачи не совпадают.");
        assertNotNull(restoredFileBackedTasksManagerWithEmptyHistory.getEpicList(), "Эпики не возвращаются.");
        assertEquals(1, restoredFileBackedTasksManagerWithEmptyHistory.getEpicList().size(),
                "Неверное количество задач.");
        assertEquals(savedEpic, restoredFileBackedTasksManagerWithEmptyHistory.getEpicList().get(0),
                "Эпики не совпадают.");
        assertNotNull(restoredFileBackedTasksManagerWithEmptyHistory.getSubtaskList(),
                "Подзадачи не возвращаются.");
        assertEquals(2, restoredFileBackedTasksManagerWithEmptyHistory.getSubtaskList().size(),
                "Неверное количество подзадач.");
        assertEquals(savedSubtask1, restoredFileBackedTasksManagerWithEmptyHistory.getSubtaskList().get(0),
                "Подзадачи не совпадают.");
        assertEquals(savedSubtask2, restoredFileBackedTasksManagerWithEmptyHistory.getSubtaskList().get(1),
                "Подзадачи не совпадают.");
        assertNotNull(restoredFileBackedTasksManagerWithEmptyHistory.getHistory(), "История не возвращается.");
        assertEquals(0, restoredFileBackedTasksManagerWithEmptyHistory.getHistory().size(),
                "Неверное количество просмотров в истории.");
    }
}