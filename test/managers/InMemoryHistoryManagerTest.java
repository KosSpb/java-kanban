package managers;

import enums.CurrentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private TaskManager taskManager;
    private HistoryManager historyManager;
    private Epic epic;
    private Epic savedEpic;
    private Subtask subtask;
    private Subtask savedSubtask;
    private Task task;
    private Task savedTask;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        historyManager = new InMemoryHistoryManager();

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

    @Test
    void getHistoryTestWithEmptyHistory() {
        List<Task> emptyHistoryOfView = historyManager.getHistory();
        assertNotNull(emptyHistoryOfView, "Задачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Список истории просмотров не пуст.");
    }

    @Test
    void getHistoryTestWith1TaskAddedToHistory() {
        List<Task> historyOfViewBeforeAddingTask = historyManager.getHistory();
        assertNotNull(historyOfViewBeforeAddingTask, "Задачи не возвращаются.");
        assertEquals(0, historyOfViewBeforeAddingTask.size(), "Список истории просмотров не пуст.");

        historyManager.add(savedTask);

        List<Task> historyOfViewAfterAddingTask = historyManager.getHistory();
        assertNotNull(historyOfViewAfterAddingTask, "Задачи не возвращаются.");
        assertEquals(1, historyOfViewAfterAddingTask.size(), "Неверное количество задач.");
    }

    @Test
    void addTestWith1Task1EpicAnd1SubtaskAsArgument() {
        List<Task> historyOfViewBeforeAddingTasks = historyManager.getHistory();
        assertNotNull(historyOfViewBeforeAddingTasks, "Задачи не возвращаются.");
        assertEquals(0, historyOfViewBeforeAddingTasks.size(), "Список истории просмотров не пуст.");

        historyManager.add(savedSubtask);
        historyManager.add(savedTask);
        historyManager.add(savedEpic);

        List<Task> historyOfViewAfterAddingTasks = historyManager.getHistory();
        assertNotNull(historyOfViewAfterAddingTasks, "Задачи не возвращаются.");
        assertEquals(3, historyOfViewAfterAddingTasks.size(), "Неверное количество задач.");
        assertEquals(List.of(savedSubtask, savedTask, savedEpic), historyOfViewAfterAddingTasks,
                "Неверный порядок задач в истории.");
    }

    @Test
    void addTestWithNullAsArgument() {
        List<Task> historyOfViewBeforeAddingNull = historyManager.getHistory();
        assertNotNull(historyOfViewBeforeAddingNull, "Задачи не возвращаются.");
        assertEquals(0, historyOfViewBeforeAddingNull.size(), "Список истории просмотров не пуст.");

        historyManager.add(null);

        List<Task> historyOfViewAfterAddingNull = historyManager.getHistory();
        assertNotNull(historyOfViewAfterAddingNull, "Задачи не возвращаются.");
        assertEquals(0, historyOfViewAfterAddingNull.size(), "Список истории просмотров не пуст.");
    }

    @Test
    void removeTestWith1Task1EpicAnd1SubtaskInHistoryAndCorrectId() {
        historyManager.add(savedSubtask);
        historyManager.add(savedTask);
        historyManager.add(savedEpic);

        List<Task> historyOfViewAfterAddingTasks = historyManager.getHistory();
        assertNotNull(historyOfViewAfterAddingTasks, "Задачи не возвращаются.");
        assertEquals(3, historyOfViewAfterAddingTasks.size(), "Неверное количество задач.");
        assertEquals(List.of(savedSubtask, savedTask, savedEpic), historyOfViewAfterAddingTasks,
                "Неверный порядок задач в истории.");

        historyManager.remove(3);

        List<Task> historyOfViewAfterRemovingCorrectId = historyManager.getHistory();
        assertNotNull(historyOfViewAfterRemovingCorrectId, "Задачи не возвращаются.");
        assertEquals(2, historyOfViewAfterRemovingCorrectId.size(), "Неверное количество задач.");
        assertEquals(List.of(savedTask, savedEpic), historyOfViewAfterRemovingCorrectId,
                "Неверный порядок задач в истории.");
    }

    @Test
    void removeTestWith1Task1EpicAnd1SubtaskInHistoryAndIncorrectId() {
        historyManager.add(savedSubtask);
        historyManager.add(savedTask);
        historyManager.add(savedEpic);

        List<Task> historyOfViewAfterAddingTasks = historyManager.getHistory();
        assertNotNull(historyOfViewAfterAddingTasks, "Задачи не возвращаются.");
        assertEquals(3, historyOfViewAfterAddingTasks.size(), "Неверное количество задач.");
        assertEquals(List.of(savedSubtask, savedTask, savedEpic), historyOfViewAfterAddingTasks,
                "Неверный порядок задач в истории.");

        historyManager.remove(0);

        List<Task> historyOfViewAfterRemovingIncorrectId = historyManager.getHistory();
        assertNotNull(historyOfViewAfterRemovingIncorrectId, "Задачи не возвращаются.");
        assertEquals(3, historyOfViewAfterRemovingIncorrectId.size(), "Неверное количество задач.");
        assertEquals(List.of(savedSubtask, savedTask, savedEpic), historyOfViewAfterRemovingIncorrectId,
                "Неверный порядок задач в истории.");
    }

    @Test
    void removeTestWithEmptyHistoryAndCorrectId() {
        List<Task> emptyHistoryOfView = historyManager.getHistory();
        assertNotNull(emptyHistoryOfView, "Задачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Список истории просмотров не пуст.");

        historyManager.remove(3);

        List<Task> historyOfViewAfterRemovingCorrectId = historyManager.getHistory();
        assertNotNull(historyOfViewAfterRemovingCorrectId, "Задачи не возвращаются.");
        assertEquals(0, historyOfViewAfterRemovingCorrectId.size(), "Список истории просмотров не " +
                "пуст.");
    }

    @Test
    void getHistoryTestAfter1TaskGotten() {
        List<Task> emptyHistoryOfView = taskManager.getHistory();

        assertNotNull(emptyHistoryOfView, "Подзадачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Неверное количество просмотров.");

        taskManager.getTaskById(2);

        List<Task> historyOfViewAfter1TaskGotten = taskManager.getHistory();

        assertNotNull(historyOfViewAfter1TaskGotten, "Подзадачи не возвращаются.");
        assertEquals(1, historyOfViewAfter1TaskGotten.size(), "Неверное количество просмотров.");
        assertEquals(2, historyOfViewAfter1TaskGotten.get(0).getId(),
                "ID вызванной задачи и ID задачи попавшей в историю просмотров не совпадают.");
    }

    @Test
    void getHistoryTestAfter2SubtaskWithSameIdAnd2EpicsWithSameIdGotten() {
        List<Task> emptyHistoryOfView = taskManager.getHistory();

        assertNotNull(emptyHistoryOfView, "Подзадачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Неверное количество просмотров.");

        taskManager.getSubtaskById(3);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(1);

        List<Task> historyOfViewAfter2SubtaskWithSameIdAnd2EpicsWithSameIdGotten = taskManager.getHistory();

        assertNotNull(historyOfViewAfter2SubtaskWithSameIdAnd2EpicsWithSameIdGotten,
                "Подзадачи не возвращаются.");
        assertEquals(2, historyOfViewAfter2SubtaskWithSameIdAnd2EpicsWithSameIdGotten.size(),
                "Неверное количество просмотров.");
        assertEquals(3, historyOfViewAfter2SubtaskWithSameIdAnd2EpicsWithSameIdGotten.get(0).getId(),
                "ID вызванной задачи и ID задачи попавшей в историю просмотров не совпадают.");
        assertEquals(1, historyOfViewAfter2SubtaskWithSameIdAnd2EpicsWithSameIdGotten.get(1).getId(),
                "ID вызванной задачи и ID задачи попавшей в историю просмотров не совпадают.");
    }

    @Test
    void shouldRemoveFirstTaskFromHistoryOfViewAndSaveOrder() {
        List<Task> emptyHistoryOfView = taskManager.getHistory();

        assertNotNull(emptyHistoryOfView, "Подзадачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Неверное количество просмотров.");

        taskManager.getTaskById(2);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(3);

        List<Task> historyOfViewAfter3TasksGotten = taskManager.getHistory();

        assertNotNull(historyOfViewAfter3TasksGotten, "Подзадачи не возвращаются.");
        assertEquals(3, historyOfViewAfter3TasksGotten.size(), "Неверное количество просмотров.");
        assertEquals(List.of(task, epic, subtask), historyOfViewAfter3TasksGotten,
                "Неверный порядок просмотров.");

        taskManager.deleteTaskById(2);

        List<Task> historyOfViewAfterFirstTaskWasDeleted = taskManager.getHistory();

        assertNotNull(historyOfViewAfterFirstTaskWasDeleted, "Подзадачи не возвращаются.");
        assertEquals(2, historyOfViewAfterFirstTaskWasDeleted.size(),
                "Неверное количество просмотров.");
        assertEquals(List.of(epic, subtask), historyOfViewAfterFirstTaskWasDeleted,
                "Неверный порядок просмотров.");
    }

    @Test
    void shouldRemoveSubtaskFromMiddleOfHistoryOfViewAndSaveOrder() {
        List<Task> emptyHistoryOfView = taskManager.getHistory();

        assertNotNull(emptyHistoryOfView, "Подзадачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Неверное количество просмотров.");

        taskManager.getTaskById(2);
        taskManager.getSubtaskById(3);
        taskManager.getEpicById(1);

        List<Task> historyOfViewAfter3TasksGotten = taskManager.getHistory();

        assertNotNull(historyOfViewAfter3TasksGotten, "Подзадачи не возвращаются.");
        assertEquals(3, historyOfViewAfter3TasksGotten.size(), "Неверное количество просмотров.");
        assertEquals(List.of(task, subtask, epic), historyOfViewAfter3TasksGotten,
                "Неверный порядок просмотров.");

        taskManager.deleteSubtaskById(3);

        List<Task> historyOfViewAfterSubtaskFromMiddleWasDeleted = taskManager.getHistory();

        assertNotNull(historyOfViewAfterSubtaskFromMiddleWasDeleted, "Подзадачи не возвращаются.");
        assertEquals(2, historyOfViewAfterSubtaskFromMiddleWasDeleted.size(),
                "Неверное количество просмотров.");
        assertEquals(List.of(task, epic), historyOfViewAfterSubtaskFromMiddleWasDeleted,
                "Неверный порядок просмотров.");
    }

    @Test
    void shouldRemoveLastTaskFromHistoryOfViewAndSaveOrder() {
        List<Task> emptyHistoryOfView = taskManager.getHistory();

        assertNotNull(emptyHistoryOfView, "Подзадачи не возвращаются.");
        assertEquals(0, emptyHistoryOfView.size(), "Неверное количество просмотров.");

        taskManager.getSubtaskById(3);
        taskManager.getEpicById(1);
        taskManager.getTaskById(2);

        List<Task> historyOfViewAfter3TasksGotten = taskManager.getHistory();

        assertNotNull(historyOfViewAfter3TasksGotten, "Подзадачи не возвращаются.");
        assertEquals(3, historyOfViewAfter3TasksGotten.size(), "Неверное количество просмотров.");
        assertEquals(List.of(subtask, epic, task), historyOfViewAfter3TasksGotten,
                "Неверный порядок просмотров.");

        taskManager.deleteTaskById(2);

        List<Task> historyOfViewAfterLastTaskWasDeleted = taskManager.getHistory();

        assertNotNull(historyOfViewAfterLastTaskWasDeleted, "Подзадачи не возвращаются.");
        assertEquals(2, historyOfViewAfterLastTaskWasDeleted.size(), "Неверное количество просмотров.");
        assertEquals(List.of(subtask, epic), historyOfViewAfterLastTaskWasDeleted,
                "Неверный порядок просмотров.");
    }
}