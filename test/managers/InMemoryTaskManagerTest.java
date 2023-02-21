package managers;

import enums.CurrentStatus;
import org.junit.jupiter.api.BeforeEach;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {
    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();

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
}