import allTasks.Epic;
import allTasks.Subtask;
import allTasks.Task;
import manager.HistoryManager;
import manager.Managers;
import manager.TaskManager;
import status.CurrentStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
        HistoryManager historyManager = Managers.getDefaultHistory();

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

        System.out.println("Задачи к выполнению:\n");
        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicById(3) + "\n");
        System.out.println(taskManager.getSubtaskById(4) + "\n");
        System.out.println(taskManager.getSubtaskById(5) + "\n");
        System.out.println(taskManager.getEpicById(6) + "\n");
        System.out.println(taskManager.getSubtaskById(7) + "\n");

        //Задачи с изменёнными статусами:
        Task updatedTask1 = new Task("купить муки", "пшеничной", CurrentStatus.DONE);
        Task updatedTask2 = new Task("Почистить машину", "от снега", CurrentStatus.IN_PROGRESS);
        Subtask updatedSubtask1 = new Subtask(3, "Изучить теорию", "3-го спринта",
                CurrentStatus.DONE);
        Subtask updatedSubtask2 = new Subtask(3, "Сдать ТЗ", "3-го спринта", CurrentStatus.DONE);
        Subtask updatedSubtask3 = new Subtask(6, "Изучить теорию", "4-го спринта",
                CurrentStatus.IN_PROGRESS);

        updatedTask1.setId(taskManager.getTaskById(1).getId());
        updatedTask2.setId(taskManager.getTaskById(2).getId());
        updatedSubtask1.setId(taskManager.getSubtaskById(4).getId());
        updatedSubtask2.setId(taskManager.getSubtaskById(5).getId());
        updatedSubtask3.setId(101);
        taskManager.updateSubtask(updatedSubtask3);
        updatedSubtask3.setId(taskManager.getSubtaskById(7).getId());

        taskManager.updateTask(updatedTask1);
        taskManager.updateTask(updatedTask2);
        taskManager.updateSubtask(updatedSubtask1);
        taskManager.updateSubtask(updatedSubtask2);
        taskManager.updateSubtask(updatedSubtask3);
        taskManager.updateEpic(epic2);

        System.out.println("\nОбновление статусов:\n");
        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicById(3) + "\n");
        System.out.println(taskManager.getSubsByEpicId(3) + "\n");
        System.out.println(taskManager.getEpicById(6) + "\n");
        System.out.println(taskManager.getSubtaskById(7) + "\n");

        taskManager.deleteTaskById(1);
        taskManager.deleteSubtaskById(4);
        taskManager.deleteSubtaskById(404);
        taskManager.deleteEpicById(6);
        taskManager.deleteEpicById(6);

        System.out.println("\nПосле удаления части задач:\n");
        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicById(3) + "\n");
        System.out.println(taskManager.getSubtaskById(4) + "\n");
        System.out.println(taskManager.getSubtaskById(5) + "\n");
        System.out.println(taskManager.getEpicById(6) + "\n");
        System.out.println(taskManager.getSubtaskById(7) + "\n");

        taskManager.deleteTasks();
        taskManager.deleteSubtasks();
        taskManager.deleteEpics();

        System.out.println("\nПосле удаления всего:\n");
        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicList() + "\n");
        System.out.println(taskManager.getSubtaskList() + "\n");

        System.out.println("\nИстория просмотров старых задач:\n");
        historyManager.getHistory();

        Task task11 = new Task("Закрепить розетку", "плохо держиться в стене", CurrentStatus.NEW);
        Task task22 = new Task("Почистить машину от снега", "снова", CurrentStatus.NEW);
        Epic epic11 = new Epic("Закончить 4-й спринт", "а то скоро дедлайн");
        Subtask subtask11 = new Subtask(10, "Изучить теорию", "4-го спринта",
                CurrentStatus.DONE);
        Subtask subtask22 = new Subtask(10, "Сдать ТЗ", "4-го спринта", CurrentStatus.IN_PROGRESS);
        Epic epic22 = new Epic("Закупиться к Новому году", "осталось всего 2 недели");
        Subtask subtask33 = new Subtask(13, "Заехать в Ленту", "список на столе",
                CurrentStatus.NEW);

        taskManager.createTask(task11);
        taskManager.createTask(task22);
        taskManager.createEpic(epic11);
        taskManager.createSubtask(subtask11);
        taskManager.createSubtask(subtask22);
        taskManager.createEpic(epic22);
        taskManager.createSubtask(subtask33);

        taskManager.getTaskById(8);
        taskManager.getEpicById(10);
        taskManager.getSubtaskById(11);
        taskManager.getTaskById(9);
        taskManager.getEpicById(13);
        taskManager.getTaskById(9);
        taskManager.getSubtaskById(14);
        taskManager.getTaskById(8);
        taskManager.getEpicById(10);
        taskManager.getSubtaskById(12);
        taskManager.getTaskById(9);
        taskManager.getEpicById(13);

        System.out.println("\nИстория просмотров новых задач:\n");
        historyManager.getHistory();
    }
}
