import AllTasks.Epic;
import AllTasks.Subtask;
import AllTasks.Task;
import Manager.TaskManager;
import Status.CurrentStatus;

public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();

        Task task1 = new Task("купить муки", "пшеничной", CurrentStatus.InProgress);
        Task task2 = new Task("Почистить машину", "от снега", CurrentStatus.New);
        Epic epic1 = new Epic("Закончить 3-й спринт", "ещё вчера");
        Subtask subtask1 = new Subtask(3, "Изучить теорию", "3-го спринта",
                CurrentStatus.InProgress);
        Subtask subtask2 = new Subtask(3, "Сдать ТЗ", "3-го спринта", CurrentStatus.New);
        Epic epic2 = new Epic("Закончить первый учебный модуль", "успеть до 19.12");
        Subtask subtask3 = new Subtask(6, "Изучить теорию", "4-го спринта", CurrentStatus.New);

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
        Task updatedTask1 = new Task("купить муки", "пшеничной", CurrentStatus.Done);
        Task updatedTask2 = new Task("Почистить машину", "от снега", CurrentStatus.InProgress);
        Subtask updatedSubtask1 = new Subtask(3, "Изучить теорию", "3-го спринта",
                CurrentStatus.Done);
        Subtask updatedSubtask2 = new Subtask(3, "Сдать ТЗ", "3-го спринта", CurrentStatus.Done);
        Subtask updatedSubtask3 = new Subtask(6, "Изучить теорию", "4-го спринта",
                CurrentStatus.InProgress);

        updatedTask1.setId(taskManager.getTaskById(1).getId());
        updatedTask2.setId(taskManager.getTaskById(2).getId());
        updatedSubtask1.setId(taskManager.getSubtaskById(4).getId());
        updatedSubtask2.setId(taskManager.getSubtaskById(5).getId());
        updatedSubtask3.setId(101);
        System.out.println(taskManager.updateSubtask(updatedSubtask3));
        updatedSubtask3.setId(taskManager.getSubtaskById(7).getId());

        System.out.println(taskManager.updateTask(updatedTask1));
        System.out.println(taskManager.updateTask(updatedTask2));
        System.out.println(taskManager.updateSubtask(updatedSubtask1));
        System.out.println(taskManager.updateSubtask(updatedSubtask2));
        System.out.println(taskManager.updateSubtask(updatedSubtask3));
        System.out.println(taskManager.updateEpic(epic2));

        System.out.println("\nОбновление статусов:\n");
        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicById(3) + "\n");
        System.out.println(taskManager.getSubsByEpicId(3) + "\n");
        System.out.println(taskManager.getEpicById(6) + "\n");
        System.out.println(taskManager.getSubtaskById(7) + "\n");

        System.out.println(taskManager.deleteTaskById(1));
        System.out.println(taskManager.deleteSubtaskById(4));
        System.out.println(taskManager.deleteSubtaskById(404));
        System.out.println(taskManager.deleteEpicById(6));
        System.out.println(taskManager.deleteEpicById(6));

        System.out.println("\nПосле удаления части задач:\n");
        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicById(3) + "\n");
        System.out.println(taskManager.getSubtaskById(4) + "\n");
        System.out.println(taskManager.getSubtaskById(5) + "\n");
        System.out.println(taskManager.getEpicById(6) + "\n");
        System.out.println(taskManager.getSubtaskById(7) + "\n");

        System.out.println(taskManager.deleteTasks());
        System.out.println(taskManager.deleteSubtasks());
        System.out.println(taskManager.deleteEpics());

        System.out.println("\nПосле удаления всего:\n");
        System.out.println(taskManager.getTaskList() + "\n");
        System.out.println(taskManager.getEpicList() + "\n");
        System.out.println(taskManager.getSubtaskList() + "\n");
    }
}
