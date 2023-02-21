package managers;

import enums.CurrentStatus;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Epic epic;
    protected Epic savedEpic;
    protected Subtask subtask;
    protected Subtask savedSubtask;
    protected Task task;
    protected Task savedTask;

    @Test
    void epicStatusCalculationWithBlankSubtaskList() {
        taskManager.deleteSubtasks();
        CurrentStatus savedEpicStatus = savedEpic.getStatus();

        assertEquals(0, savedEpic.getSubtaskIds().size(), "Список подзадач не пуст.");
        assertEquals(CurrentStatus.NEW, savedEpicStatus, "Статусы не совпадают.");
    }

    @Test
    void epicStatusCalculationWithAllSubtaskMarkedNew() {
        Subtask subtask2 = new Subtask(1, "newSubHeader2", "newSubDescription2",
                CurrentStatus.NEW, null, 0);
        taskManager.createSubtask(subtask2);

        CurrentStatus savedEpicStatus = savedEpic.getStatus();

        assertEquals(2, savedEpic.getSubtaskIds().size(), "Неверное количество подзадач в списке.");
        assertEquals(CurrentStatus.NEW, savedEpicStatus, "Статусы не совпадают.");
    }

    @Test
    void epicStatusCalculationWithAllSubtaskMarkedDone() {
        taskManager.deleteSubtasks();
        assertEquals(0, savedEpic.getSubtaskIds().size(), "Список подзадач не пуст.");

        Subtask subtask1 = new Subtask(1, "newSubHeader1", "newSubDescription1",
                CurrentStatus.DONE, null, 0);
        Subtask subtask2 = new Subtask(1, "newSubHeader2", "newSubDescription2",
                CurrentStatus.DONE, null, 0);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        CurrentStatus savedEpicStatus = savedEpic.getStatus();

        assertEquals(2, savedEpic.getSubtaskIds().size(), "Неверное количество подзадач в списке.");
        assertEquals(CurrentStatus.DONE, savedEpicStatus, "Статусы не совпадают.");
    }

    @Test
    void epicStatusCalculationWithOneSubtaskMarkedNewAndAnotherMarkedDone() {
        Subtask subtask2 = new Subtask(1, "newSubHeader2", "newSubDescription2",
                CurrentStatus.DONE, null, 0);
        taskManager.createSubtask(subtask2);

        CurrentStatus savedEpicStatus = savedEpic.getStatus();

        assertEquals(2, savedEpic.getSubtaskIds().size(), "Неверное количество подзадач в списке.");
        assertEquals(CurrentStatus.IN_PROGRESS, savedEpicStatus, "Статусы не совпадают.");
    }

    @Test
    void epicStatusCalculationWithAllSubtaskMarkedInProgress() {
        taskManager.deleteSubtasks();
        assertEquals(0, savedEpic.getSubtaskIds().size(), "Список подзадач не пуст.");

        Subtask subtask1 = new Subtask(1, "newSubHeader1", "newSubDescription1",
                CurrentStatus.IN_PROGRESS, null, 0);
        Subtask subtask2 = new Subtask(1, "newSubHeader2", "newSubDescription2",
                CurrentStatus.IN_PROGRESS, null, 0);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        CurrentStatus savedEpicStatus = savedEpic.getStatus();

        assertEquals(2, savedEpic.getSubtaskIds().size(), "Неверное количество подзадач в списке.");
        assertEquals(CurrentStatus.IN_PROGRESS, savedEpicStatus, "Статусы не совпадают.");
    }

    @Test
    void getPrioritizedTasksTestWith3TimedTasksAnd3NullTimedTasks() {
        Task task1 = new Task("TestTaskHeader1", "TestTaskDescription1", CurrentStatus.IN_PROGRESS,
                LocalDateTime.of(2023,1,31,10,30), 120);
        Task task2 = new Task("TestTaskHeader2", "TestTaskDescription2", CurrentStatus.NEW,
                LocalDateTime.of(2023,1,25,11,30), 60);
        Subtask subtask1 = new Subtask(1, "newSubHeader1", "newSubDescription1",
                CurrentStatus.NEW, null, 0);
        Subtask subtask2 = new Subtask(1, "newSubHeader2", "newSubDescription2",
                CurrentStatus.IN_PROGRESS, LocalDateTime.of(2023,2,12,17,30),
                80);
        Task createdTask1 = taskManager.createTask(task1);
        Task createdTask2 = taskManager.createTask(task2);
        Subtask createdSubtask1 = taskManager.createSubtask(subtask1);
        Subtask createdSubtask2 = taskManager.createSubtask(subtask2);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(prioritizedTasks, "Задачи не возвращаются.");
        assertEquals(6, prioritizedTasks.size(), "Неверное количество задач.");

        Task earliestTask = taskManager.getPrioritizedTasks().get(0);
        Task thirdTask = taskManager.getPrioritizedTasks().get(2);
        Task latestTask = taskManager.getPrioritizedTasks().get(taskManager.getPrioritizedTasks().size() - 1);

        assertEquals(createdTask2, earliestTask, "Самые ранние задачи не совпадают.");
        assertEquals(createdSubtask2, thirdTask, "Задача не совпадает с третьей.");
        assertEquals(createdSubtask1, latestTask, "Самые поздние задачи не совпадают.");
    }

    @Test
    void getPrioritizedTasksTestWithEmptySet() {
        taskManager.deleteTasks();
        taskManager.deleteEpics();
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertNotNull(prioritizedTasks, "Задачи не возвращаются.");
        assertEquals(0, prioritizedTasks.size(), "Неверное количество задач.");
        assertEquals(new ArrayList<>(), prioritizedTasks, "Список не пуст.");
    }

    @Test
    void getTaskListTestWith1TaskCreated() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(savedTask, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getTaskListTestWithEmptyMap() {
        taskManager.deleteTasks();
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
        assertEquals(new ArrayList<>(), tasks, "Список не пуст.");
    }

    @Test
    void getEpicListTestWith1EpicCreated() {
        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(savedEpic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void getEpicListTestWithEmptyMap() {
        taskManager.deleteEpics();
        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество задач.");
        assertEquals(new ArrayList<>(), epics, "Список не пуст.");
    }

    @Test
    void getSubtaskListWith1SubtaskCreated() {
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(savedSubtask, subtasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getSubtaskListWithEmptyMap() {
        taskManager.deleteSubtasks();
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество задач.");
        assertEquals(new ArrayList<>(), subtasks, "Список не пуст.");
    }

    @Test
    void getTaskByIdTestWith1TaskCreatedAndCorrectId() {
        Task task = taskManager.getTaskById(2);
        int listOfTaskSize = taskManager.getTaskList().size();

        assertNotNull(task, "Вместо задачи возвращается null.");
        assertEquals(1, listOfTaskSize, "Неверное количество задач.");
        assertEquals(savedTask.getId(), task.getId(), "ID задач не совпадают.");
        assertEquals(savedTask, task, "Задачи не совпадают.");
    }

    @Test
    void getTaskByIdTestWith1TaskCreatedAndIncorrectId() {
        Task task = taskManager.getTaskById(0);
        int listOfTaskSize = taskManager.getTaskList().size();

        assertEquals(1, listOfTaskSize, "Неверное количество задач.");
        assertNull(task);
    }

    @Test
    void getTaskByIdTestWithEmptyMapAndCorrectId() {
        taskManager.deleteTasks();
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");

        Task task = taskManager.getTaskById(2);
        int listOfTaskSize = taskManager.getTaskList().size();

        assertEquals(0, listOfTaskSize, "Неверное количество задач.");
        assertNull(task);
    }

    @Test
    void getEpicByIdTestWith1EpicCreatedAndCorrectId() {
        Epic epic = taskManager.getEpicById(1);
        int listOfEpicSize = taskManager.getEpicList().size();

        assertNotNull(epic, "Вместо задачи возвращается null.");
        assertEquals(1, listOfEpicSize, "Неверное количество задач.");
        assertEquals(savedEpic.getId(), epic.getId(), "ID задач не совпадают.");
        assertEquals(savedEpic, epic, "Задачи не совпадают.");
    }

    @Test
    void getEpicByIdTestWith1EpicCreatedAndIncorrectId() {
        Epic epic = taskManager.getEpicById(0);
        int listOfEpicSize = taskManager.getEpicList().size();

        assertEquals(1, listOfEpicSize, "Неверное количество задач.");
        assertNull(epic);
    }

    @Test
    void getEpicByIdTestWithEmptyMapAndCorrectId() {
        taskManager.deleteEpics();
        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество задач.");

        Epic epic = taskManager.getEpicById(1);
        int listOfEpicSize = taskManager.getEpicList().size();

        assertEquals(0, listOfEpicSize, "Неверное количество задач.");
        assertNull(epic);
    }

    @Test
    void getSubtaskByIdTestWith1SubtaskCreatedAndCorrectId() {
        Subtask subtask = taskManager.getSubtaskById(3);
        int listOfSubtaskSize = taskManager.getTaskList().size();

        assertNotNull(subtask, "Вместо подзадачи возвращается null.");
        assertEquals(1, listOfSubtaskSize, "Неверное количество задач.");
        assertEquals(savedSubtask.getId(), subtask.getId(), "ID задач не совпадают.");
        assertEquals(savedSubtask, subtask, "Задачи не совпадают.");
    }

    @Test
    void getSubtaskByIdTestWith1SubtaskCreatedAndIncorrectId() {
        Subtask subtask = taskManager.getSubtaskById(0);
        int listOfSubtaskSize = taskManager.getTaskList().size();

        assertEquals(1, listOfSubtaskSize, "Неверное количество задач.");
        assertNull(subtask);
    }

    @Test
    void getSubtaskByIdTestWithEmptyMapAndCorrectId() {
        taskManager.deleteSubtasks();
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(0, subtasks.size(), "Неверное количество подзадач.");

        Subtask subtask = taskManager.getSubtaskById(3);
        int listOfSubtaskSize = taskManager.getSubtaskList().size();

        assertEquals(0, listOfSubtaskSize, "Неверное количество задач.");
        assertNull(subtask);
    }

    @Test
    void createTaskTestWithTaskAsArgument() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void createTaskTestWithNullAsArgument() {
        savedTask = taskManager.createTask(null);

        assertNull(savedTask);
    }

    @Test
    void createEpicTestWithEpicAsArgument() {
        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
    }

    @Test
    void createEpicTestWithNullAsArgument() {
        savedEpic = taskManager.createEpic(null);

        assertNull(savedEpic);
    }

    @Test
    void createSubtaskTestWithSubtaskAsArgument() {
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");

        Epic epicOfCurrentSubtask = taskManager.getEpicById(subtask.getEpicId());
        List<Long> subtaskIdsOfEpic = epicOfCurrentSubtask.getSubtaskIds();

        assertNotNull(subtaskIdsOfEpic, "Список подзадач не возвращается.");
        assertEquals(1, subtaskIdsOfEpic.size(), "Неверное количество подзадач.");
        assertEquals(subtask.getId(), subtaskIdsOfEpic.get(0), "ID подзадач не совпадают.");
        assertEquals(epic, epicOfCurrentSubtask, "Эпики не совпадают.");
    }

    @Test
    void createSubtaskTestWithNullAsArgument() {
        savedSubtask = taskManager.createSubtask(null);

        assertNull(savedSubtask);
    }

    @Test
    void startTimeAndEndTimeAndDurationCalculationForEpicAfterCreationOf1NullTimedSubtaskAnd2TimedSubtasks() {
        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        Subtask subtask1 = new Subtask(1, "timedSubHeader1", "timedSubDescription1",
                CurrentStatus.NEW, LocalDateTime.of(2023, 2, 1, 10, 10),
                180);
        Subtask subtask2 = new Subtask(1, "timedSubHeader2", "timedSubDescription2",
                CurrentStatus.IN_PROGRESS, LocalDateTime.of(2023, 1, 10, 15, 25),
                120);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(3, taskManager.getEpicById(epic.getId()).getSubtaskIds().size(),
                "Количество подзадач в списке эпика не совпадает.");
        assertEquals(Duration.ofMinutes(300), epics.get(0).getDurationInMinutes(),
                "Продолжительность не совпадает.");
        assertEquals(LocalDateTime.of(2023, 1, 10, 15, 25),
                epics.get(0).getStartTime(), "Начальное время не совпадает.");
        assertEquals(LocalDateTime.of(2023, 2, 1, 10, 10)
                .plus(Duration.ofMinutes(180)), epics.get(0).getEndTime(), "Конечное время не совпадает.");
    }

    @Test
    void updateTaskTestWithTaskAsArgumentWithCorrectId() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        Task updatedTask = new Task("updatedTaskHeader", "updatedTaskDescription",
                CurrentStatus.IN_PROGRESS, LocalDateTime.of(2023, 1, 1, 0, 0),
                5);
        updatedTask.setId(task.getId());
        taskManager.updateTask(updatedTask);
        List<Task> updatedTasks = taskManager.getTaskList();

        assertNotNull(updatedTasks, "Задачи не возвращаются.");
        assertEquals(1, updatedTasks.size(), "Неверное количество задач.");
        assertNotEquals(task, updatedTask, "Задачи совпадают.");
        assertEquals(updatedTask, updatedTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTaskTestWithTaskAsArgumentWithIncorrectId() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        Task updatedTask = new Task("updatedTaskHeader", "updatedTaskDescription",
                CurrentStatus.IN_PROGRESS, LocalDateTime.of(2023, 1, 1, 0, 0),
                5);
        updatedTask.setId(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateTask(updatedTask));
        assertEquals("ID '" + updatedTask.getId() + "' нет в списке задач. " +
                "Обновление невозможно.", exception.getMessage());

        List<Task> unUpdatedTasks = taskManager.getTaskList();

        assertNotNull(unUpdatedTasks, "Задачи не возвращаются.");
        assertEquals(1, unUpdatedTasks.size(), "Неверное количество задач.");
        assertEquals(task, unUpdatedTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateTaskTestWithNullArgument() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        taskManager.updateTask(null);

        List<Task> unUpdatedTasks = taskManager.getTaskList();

        assertNotNull(unUpdatedTasks, "Задачи не возвращаются.");
        assertEquals(1, unUpdatedTasks.size(), "Неверное количество задач.");
        assertEquals(task, unUpdatedTasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void updateEpicTestWithEpicAsArgumentWithCorrectId() {
        List<Epic> epics = taskManager.getEpicList();
        List<Long> subtaskIdsOfEpicBeforeUpdate = epics.get(0).getSubtaskIds();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        Epic updatedEpic = new Epic("updatedEpicHeader", "updatedEpicDescription");
        updatedEpic.setId(epic.getId());
        taskManager.updateEpic(updatedEpic);

        List<Epic> updatedEpics = taskManager.getEpicList();
        List<Long> subtaskIdsOfEpicAfterUpdate = updatedEpics.get(0).getSubtaskIds();

        assertNotNull(updatedEpics, "Задачи не возвращаются.");
        assertEquals(1, updatedEpics.size(), "Неверное количество задач.");
        assertNotEquals(epic, updatedEpic, "Задачи совпадают.");
        assertEquals(updatedEpic, updatedEpics.get(0), "Задачи не совпадают.");
        assertEquals(subtaskIdsOfEpicBeforeUpdate, subtaskIdsOfEpicAfterUpdate,
                "Списки подзадач не совпадают.");
    }

    @Test
    void updateEpicTestWithEpicAsArgumentWithIncorrectId() {
        List<Epic> epics = taskManager.getEpicList();
        List<Long> subtaskIdsOfEpicBeforeUpdate = epics.get(0).getSubtaskIds();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        Epic updatedEpic = new Epic("updatedEpicHeader", "updatedEpicDescription");
        updatedEpic.setId(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateEpic(updatedEpic));
        assertEquals("ID '" + updatedEpic.getId() + "' нет в списке эпиков. " +
                "Обновление невозможно.", exception.getMessage());

        List<Epic> unUpdatedEpics = taskManager.getEpicList();
        List<Long> subtaskIdsOfEpicAfterUpdate = unUpdatedEpics.get(0).getSubtaskIds();

        assertNotNull(unUpdatedEpics, "Задачи не возвращаются.");
        assertEquals(1, unUpdatedEpics.size(), "Неверное количество задач.");
        assertEquals(epic, unUpdatedEpics.get(0), "Задачи не совпадают.");
        assertEquals(subtaskIdsOfEpicBeforeUpdate, subtaskIdsOfEpicAfterUpdate,
                "Списки подзадач не совпадают.");
    }

    @Test
    void updateEpicTestWithNullArgument() {
        List<Epic> epics = taskManager.getEpicList();
        List<Long> subtaskIdsOfEpicBeforeUpdate = epics.get(0).getSubtaskIds();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");

        taskManager.updateEpic(null);

        List<Epic> unUpdatedEpics = taskManager.getEpicList();
        List<Long> subtaskIdsOfEpicAfterUpdate = unUpdatedEpics.get(0).getSubtaskIds();

        assertNotNull(unUpdatedEpics, "Задачи не возвращаются.");
        assertEquals(1, unUpdatedEpics.size(), "Неверное количество задач.");
        assertEquals(epic, unUpdatedEpics.get(0), "Задачи не совпадают.");
        assertEquals(subtaskIdsOfEpicBeforeUpdate, subtaskIdsOfEpicAfterUpdate,
                "Списки подзадач не совпадают.");
    }

    @Test
    void updateSubtaskTestWithSubtaskAsArgumentWithCorrectId() {
        List<Subtask> subtasks = taskManager.getSubtaskList();
        Epic epicBeforeSubtaskUpdate = taskManager.getEpicById(subtasks.get(0).getEpicId());

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
        assertEquals(CurrentStatus.NEW, epicBeforeSubtaskUpdate.getStatus(), "Статусы не совпадают.");
        assertNull(epicBeforeSubtaskUpdate.getStartTime());
        assertEquals(Duration.ofMinutes(0), epicBeforeSubtaskUpdate.getDurationInMinutes(),
                "Продолжительность не совпадает.");

        Subtask updatedSubtask = new Subtask(1, "updatedSubHeader", "updatedSubDescription",
                CurrentStatus.IN_PROGRESS, LocalDateTime.of(2023, 1, 10, 15, 25),
                120);
        updatedSubtask.setId(subtask.getId());
        taskManager.updateSubtask(updatedSubtask);

        List<Subtask> updatedSubtasks = taskManager.getSubtaskList();
        Epic epicAfterSubtaskUpdate = taskManager.getEpicById(updatedSubtasks.get(0).getEpicId());

        assertNotNull(updatedSubtasks, "Задачи не возвращаются.");
        assertEquals(1, updatedSubtasks.size(), "Неверное количество задач.");
        assertNotEquals(subtask, updatedSubtask, "Задачи совпадают.");
        assertEquals(updatedSubtask, updatedSubtasks.get(0), "Задачи не совпадают.");
        assertEquals(CurrentStatus.IN_PROGRESS, epicAfterSubtaskUpdate.getStatus(), "Статусы не совпадают.");
        assertEquals(LocalDateTime.of(2023, 1, 10, 15, 25),
                epicAfterSubtaskUpdate.getStartTime(), "Начальное время не совпадает.");
        assertEquals(Duration.ofMinutes(120), epicAfterSubtaskUpdate.getDurationInMinutes(),
                "Продолжительность не совпадает.");
    }

    @Test
    void updateSubtaskTestWithSubtaskAsArgumentWithIncorrectId() {
        List<Subtask> subtasks = taskManager.getSubtaskList();
        Epic epicBeforeSubtaskUpdate = taskManager.getEpicById(subtasks.get(0).getEpicId());

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
        assertEquals(CurrentStatus.NEW, epicBeforeSubtaskUpdate.getStatus(), "Статусы не совпадают.");
        assertNull(epicBeforeSubtaskUpdate.getStartTime());
        assertEquals(Duration.ofMinutes(0), epicBeforeSubtaskUpdate.getDurationInMinutes(),
                "Продолжительность не совпадает.");

        Subtask updatedSubtask = new Subtask(1, "updatedSubHeader", "updatedSubDescription",
                CurrentStatus.IN_PROGRESS, LocalDateTime.of(2023, 1, 10, 15, 25),
                120);
        updatedSubtask.setId(0);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskManager.updateSubtask(updatedSubtask));
        assertEquals("ID '" + updatedSubtask.getId() + "' нет в списке подзадач. " +
                "Обновление невозможно.", exception.getMessage());

        List<Subtask> unUpdatedSubtasks = taskManager.getSubtaskList();
        Epic epicAfterSubtaskUpdate = taskManager.getEpicById(unUpdatedSubtasks.get(0).getEpicId());

        assertNotNull(unUpdatedSubtasks, "Задачи не возвращаются.");
        assertEquals(1, unUpdatedSubtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, unUpdatedSubtasks.get(0), "Задачи не совпадают.");
        assertEquals(CurrentStatus.NEW, epicAfterSubtaskUpdate.getStatus(), "Статусы не совпадают.");
        assertNull(epicAfterSubtaskUpdate.getStartTime());
        assertEquals(Duration.ofMinutes(0), epicAfterSubtaskUpdate.getDurationInMinutes(),
                "Продолжительность не совпадает.");
    }

    @Test
    void updateSubtaskTestWithNullArgument() {
        List<Subtask> subtasks = taskManager.getSubtaskList();
        Epic epicBeforeSubtaskUpdate = taskManager.getEpicById(subtasks.get(0).getEpicId());

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");
        assertEquals(CurrentStatus.NEW, epicBeforeSubtaskUpdate.getStatus(), "Статусы не совпадают.");
        assertNull(epicBeforeSubtaskUpdate.getStartTime());
        assertEquals(Duration.ofMinutes(0), epicBeforeSubtaskUpdate.getDurationInMinutes(),
                "Продолжительность не совпадает.");

        taskManager.updateSubtask(null);

        List<Subtask> unUpdatedSubtasks = taskManager.getSubtaskList();
        Epic epicAfterSubtaskUpdate = taskManager.getEpicById(unUpdatedSubtasks.get(0).getEpicId());

        assertNotNull(unUpdatedSubtasks, "Задачи не возвращаются.");
        assertEquals(1, unUpdatedSubtasks.size(), "Неверное количество задач.");
        assertEquals(subtask, unUpdatedSubtasks.get(0), "Задачи не совпадают.");
        assertEquals(CurrentStatus.NEW, epicAfterSubtaskUpdate.getStatus(), "Статусы не совпадают.");
        assertNull(epicAfterSubtaskUpdate.getStartTime());
        assertEquals(Duration.ofMinutes(0), epicAfterSubtaskUpdate.getDurationInMinutes(),
                "Продолжительность не совпадает.");
    }

    @Test
    void checkTimeCrossingIfNewTaskEndsAfterExistingTaskStarts() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        Task newTask = new Task("newTaskHeader", "newTaskDescription", CurrentStatus.NEW,
                LocalDateTime.of(2023, 2, 18, 9, 30), 60);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> taskManager.createTask(newTask));
        assertEquals(String.format("Время создаваемой задачи пересекается с задачей '%s' типа %s. " +
                "Время её начала - %s. Время её окончания - %s. Выберите другое время для новой задачи.",
                task.getHeader(), task.getTaskType().toString(), task.getStartTime().toString(),
                task.getEndTime().toString()), exception.getMessage());

        List<Task> tasksAfterAttemptToCreateNewTask = taskManager.getTaskList();

        assertNotNull(tasksAfterAttemptToCreateNewTask, "Задачи не возвращаются.");
        assertEquals(1, tasksAfterAttemptToCreateNewTask.size());
        assertEquals(task, tasksAfterAttemptToCreateNewTask.get(0), "Задачи не совпадают.");
    }

    @Test
    void checkTimeCrossingIfNewTaskStartsBeforeExistingTaskEnds() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        Task newTask = new Task("newTaskHeader", "newTaskDescription", CurrentStatus.NEW,
                LocalDateTime.of(2023, 2, 18, 10, 30), 60);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> taskManager.createTask(newTask));
        assertEquals(String.format("Время создаваемой задачи пересекается с задачей '%s' типа %s. " +
                        "Время её начала - %s. Время её окончания - %s. Выберите другое время для новой задачи.",
                task.getHeader(), task.getTaskType().toString(), task.getStartTime().toString(),
                task.getEndTime().toString()), exception.getMessage());

        List<Task> tasksAfterAttemptToCreateNewTask = taskManager.getTaskList();

        assertNotNull(tasksAfterAttemptToCreateNewTask, "Задачи не возвращаются.");
        assertEquals(1, tasksAfterAttemptToCreateNewTask.size());
        assertEquals(task, tasksAfterAttemptToCreateNewTask.get(0), "Задачи не совпадают.");
    }

    @Test
    void checkTimeCrossingIfExistingTaskDurationBetweenNewTaskStartTimeAndEndTime() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        Task newTask = new Task("newTaskHeader", "newTaskDescription", CurrentStatus.NEW,
                LocalDateTime.of(2023, 2, 18, 9, 0), 200);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> taskManager.createTask(newTask));
        assertEquals(String.format("Время создаваемой задачи пересекается с задачей '%s' типа %s. " +
                        "Время её начала - %s. Время её окончания - %s. Выберите другое время для новой задачи.",
                task.getHeader(), task.getTaskType().toString(), task.getStartTime().toString(),
                task.getEndTime().toString()), exception.getMessage());

        List<Task> tasksAfterAttemptToCreateNewTask = taskManager.getTaskList();

        assertNotNull(tasksAfterAttemptToCreateNewTask, "Задачи не возвращаются.");
        assertEquals(1, tasksAfterAttemptToCreateNewTask.size());
        assertEquals(task, tasksAfterAttemptToCreateNewTask.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteTasksTest() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

        taskManager.deleteTasks();

        List<Task> tasksAfterAllWasDeleted = taskManager.getTaskList();

        assertEquals(0, tasksAfterAllWasDeleted.size(), "Неверное количество задач после удаления.");
    }

    @Test
    void deleteEpicsTest() {
        List<Epic> epics = taskManager.getEpicList();
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");

        taskManager.deleteEpics();

        List<Epic> epicsAfterAllWasDeleted = taskManager.getEpicList();
        List<Subtask> subtasksAfterAllWasDeleted = taskManager.getSubtaskList();

        assertEquals(0, epicsAfterAllWasDeleted.size(), "Неверное количество задач после удаления.");
        assertEquals(0, subtasksAfterAllWasDeleted.size(),
                "Неверное количество подзадач после удаления.");
    }

    @Test
    void deleteSubtasksTest() {
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");

        taskManager.deleteSubtasks();

        List<Subtask> subtasksAfterAllWasDeleted = taskManager.getSubtaskList();

        assertEquals(0, subtasksAfterAllWasDeleted.size(),
                "Неверное количество подзадач после удаления.");
    }

    @Test
    void deleteTaskByIdTestWith1TaskCreatedAndCorrectId() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");

        taskManager.deleteTaskById(2);

        List<Task> tasksAfterAllWasDeleted = taskManager.getTaskList();

        assertEquals(0, tasksAfterAllWasDeleted.size(), "Неверное количество задач после удаления.");
    }

    @Test
    void deleteTaskByIdTestWith1TaskCreatedAndIncorrectId() {
        List<Task> tasks = taskManager.getTaskList();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");

        taskManager.deleteTaskById(0);

        List<Task> tasksAfterNoneWasDeleted = taskManager.getTaskList();

        assertEquals(1, tasksAfterNoneWasDeleted.size(), "Неверное количество задач после удаления.");
        assertEquals(task, tasksAfterNoneWasDeleted.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteEpicByIdTestWith1EpicAnd1SubtaskCreatedAndCorrectId() {
        List<Epic> epics = taskManager.getEpicList();
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");

        taskManager.deleteEpicById(1);

        List<Epic> epicsAfterAllWasDeleted = taskManager.getEpicList();
        List<Subtask> subtasksAfterAllWasDeleted = taskManager.getSubtaskList();

        assertEquals(0, epicsAfterAllWasDeleted.size(), "Неверное количество задач после удаления.");
        assertEquals(0, subtasksAfterAllWasDeleted.size(),
                "Неверное количество подзадач после удаления.");
    }

    @Test
    void deleteEpicByIdTestWith1EpicAnd1SubtaskCreatedAndIncorrectId() {
        List<Epic> epics = taskManager.getEpicList();
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(0), "Задачи не совпадают.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Задачи не совпадают.");

        taskManager.deleteEpicById(0);

        List<Epic> epicsAfterNoneWasDeleted = taskManager.getEpicList();
        List<Subtask> subtasksAfterNoneWasDeleted = taskManager.getSubtaskList();

        assertEquals(1, epicsAfterNoneWasDeleted.size(), "Неверное количество задач после удаления.");
        assertEquals(epic, epicsAfterNoneWasDeleted.get(0), "Задачи не совпадают.");
        assertEquals(1, subtasksAfterNoneWasDeleted.size(),
                "Неверное количество подзадач после удаления.");
        assertEquals(subtask, subtasksAfterNoneWasDeleted.get(0), "Задачи не совпадают.");
    }

    @Test
    void deleteSubtaskByIdTestWith1SubtaskCreatedAndCorrectId() {
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");

        taskManager.deleteSubtaskById(3);

        List<Subtask> subtasksAfterAllWasDeleted = taskManager.getSubtaskList();

        assertEquals(0, subtasksAfterAllWasDeleted.size(),
                "Неверное количество подзадач после удаления.");
    }

    @Test
    void deleteSubtaskByIdTestWith1SubtaskCreatedAndIncorrectId() {
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");

        taskManager.deleteSubtaskById(0);

        List<Subtask> subtasksAfterNoneWasDeleted = taskManager.getSubtaskList();

        assertEquals(1, subtasksAfterNoneWasDeleted.size(),
                "Неверное количество подзадач после удаления.");
        assertEquals(subtask, subtasksAfterNoneWasDeleted.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getSubsByEpicIdTestWith1EpicAnd1SubtaskCreatedAndCorrectId() {
        List<Epic> epics = taskManager.getEpicList();
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtasks.get(0).getId(), epics.get(0).getSubtaskIds().get(0),
                "ID подзадачи в списке эпика и в хранилище подзадач не совпадают.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");

        List<Subtask> subtasksOfEpic = taskManager.getSubsByEpicId(1);

        assertNotNull(subtasksOfEpic, "Подзадачи не возвращаются.");
        assertEquals(1, subtasksOfEpic.size(), "Неверное количество задач.");
        assertEquals(subtask, subtasksOfEpic.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void getSubsByEpicIdTestWith1EpicAnd1SubtaskCreatedAndIncorrectId() {
        List<Epic> epics = taskManager.getEpicList();
        List<Subtask> subtasks = taskManager.getSubtaskList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtasks.get(0).getId(), epics.get(0).getSubtaskIds().get(0),
                "ID подзадачи в списке эпика и в хранилище подзадач не совпадают.");
        assertEquals(subtask, subtasks.get(0), "Подзадачи не совпадают.");

        List<Subtask> subtasksOfEpic = taskManager.getSubsByEpicId(0);

        assertNotNull(subtasksOfEpic, "Подзадачи не возвращаются.");
        assertEquals(0, subtasksOfEpic.size(), "Неверное количество задач.");
        assertEquals(new ArrayList<>(), subtasksOfEpic, "Список не пустой.");
    }

    @Test
    void getSubsByEpicIdTestWithEmptyMapAndCorrectId() {
        taskManager.deleteEpics();
        List<Epic> epics = taskManager.getEpicList();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(0, epics.size(), "Неверное количество задач.");

        List<Subtask> subtasksOfEpic = taskManager.getSubsByEpicId(1);

        assertNotNull(subtasksOfEpic, "Подзадачи не возвращаются.");
        assertEquals(0, subtasksOfEpic.size(), "Неверное количество задач.");
        assertEquals(new ArrayList<>(), subtasksOfEpic, "Список не пустой.");
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
    void getHistoryTestWithEmptyHistoryList() {
        List<Task> historyOfView = taskManager.getHistory();

        assertNotNull(historyOfView, "Подзадачи не возвращаются.");
        assertEquals(0, historyOfView.size(), "Неверное количество просмотров.");
        assertEquals(new ArrayList<>(), historyOfView, "Список не пустой.");
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
