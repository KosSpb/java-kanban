import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import managers.Managers;
import managers.TaskManager;
import status.CurrentStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        Task task1 = new Task("купить муки", "пшеничной", CurrentStatus.IN_PROGRESS);
        Task task2 = new Task("Почистить машину", "от снега", CurrentStatus.NEW);
        Epic epic1 = new Epic("Закончить 3-й спринт", "ещё вчера");
        Subtask subtask1 = new Subtask(3, "Изучить теорию", "3-го спринта",
                CurrentStatus.IN_PROGRESS);
        Subtask subtask2 = new Subtask(3, "Сдать ТЗ", "3-го спринта", CurrentStatus.NEW);
        Epic epic2 = new Epic("Закончить первый учебный модуль", "успеть до 19.12");
        Subtask subtask3 = new Subtask(6, "Изучить теорию", "4-го спринта", CurrentStatus.NEW);

        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createEpic(epic2);
        taskManager.createSubtask(subtask3);

        System.out.println(taskManager.getHistory());

        taskManager.getEpicById(3);

        System.out.println("\nИстория просмотров после вызова 1 задачи:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getSubtaskById(4);

        System.out.println("\nИстория просмотров после вызова 2 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getSubtaskById(5);

        System.out.println("\nИстория просмотров после вызова 3 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getEpicById(6);

        System.out.println("\nИстория просмотров после вызова 4 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getSubtaskById(7);

        System.out.println("\nИстория просмотров после вызова 5 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getTaskById(1);

        System.out.println("\nИстория просмотров после вызова 6 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getTaskById(2);

        System.out.println("\nИстория просмотров после вызова 7 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getTaskById(1);

        System.out.println("\nИстория просмотров после вызова 8 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getEpicById(3);

        System.out.println("\nИстория просмотров после вызова 9 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getEpicById(6);

        System.out.println("\nИстория просмотров после вызова 10 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getSubtaskById(7);

        System.out.println("\nИстория просмотров после вызова 11 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.getTaskById(1);

        System.out.println("\nИстория просмотров после вызова 12 задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.deleteTaskById(1);

        System.out.println("\nИстория просмотров после удаления задачи 1:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.deleteEpicById(6);

        System.out.println("\nИстория просмотров после удаления эпика 6:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.deleteSubtaskById(5);

        System.out.println("\nИстория просмотров после удаления подзадачи 5:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.deleteSubtasks();

        System.out.println("\nИстория просмотров после удаления всех подзадач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.deleteEpics();

        System.out.println("\nИстория просмотров после удаления всех эпиков:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");

        taskManager.deleteTasks();

        System.out.println("\nИстория просмотров после удаления всех задач:\n");
        System.out.println(taskManager.getHistory());
        System.out.println("________________________________");
    }
}
