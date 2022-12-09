public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task task1 = new Task("купить муки", "пшеничной", CurrentStatus.InProgress);
        Task task2 = new Task("Почистить машину", "от снега", CurrentStatus.New);
        Epic epic1 = new Epic("Закончить 3-й спринт", "ещё вчера");
        Subtask subtask1 = new Subtask(3, "Изучить теорию", "3-го спринта",
                CurrentStatus.InProgress);
        Subtask subtask2 = new Subtask(3, "Сдать ТЗ", "3-го спринта", CurrentStatus.New);
        Epic epic2 = new Epic("Закончить первый учебный модуль", "успеть до 19.12");
        Subtask subtask3 = new Subtask(6, "Изучить теорию", "4-го спринта", CurrentStatus.New);

        manager.createTask(task1);
        manager.createTask(task2);
        manager.createEpic(epic1);
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);
        manager.createEpic(epic2);
        manager.createSubtask(subtask3);

        System.out.println("Задачи к выполнению:\n");
        System.out.println(manager.getTaskList() + "\n");
        System.out.println(manager.getEpicById(3) + "\n");
        System.out.println(manager.getSubtaskById(4) + "\n");
        System.out.println(manager.getSubtaskById(5) + "\n");
        System.out.println(manager.getEpicById(6) + "\n");
        System.out.println(manager.getSubtaskById(7) + "\n");

        //Задачи с изменёнными статусами:
        Task updatedTask1 = new Task("купить муки", "пшеничной", CurrentStatus.Done);
        Task updatedTask2 = new Task("Почистить машину", "от снега", CurrentStatus.InProgress);
        Subtask updatedSubtask1 = new Subtask(3, "Изучить теорию", "3-го спринта",
                CurrentStatus.Done);
        Subtask updatedSubtask2 = new Subtask(3, "Сдать ТЗ", "3-го спринта", CurrentStatus.Done);
        Subtask updatedSubtask3 = new Subtask(6, "Изучить теорию", "4-го спринта",
                CurrentStatus.InProgress);

        updatedTask1.id = manager.getTaskById(1).id;
        updatedTask2.id = manager.getTaskById(2).id;
        updatedSubtask1.id = manager.getSubtaskById(4).id;
        updatedSubtask2.id = manager.getSubtaskById(5).id;
        updatedSubtask3.id = manager.getSubtaskById(7).id;

        manager.updateTask(updatedTask1);
        manager.updateTask(updatedTask2);
        manager.updateSubtask(updatedSubtask1);
        manager.updateSubtask(updatedSubtask2);
        manager.updateSubtask(updatedSubtask3);
        manager.updateEpic(epic1);
        manager.updateEpic(epic2);

        System.out.println("\nОбновление статусов:\n");
        System.out.println(manager.getTaskList() + "\n");
        System.out.println(manager.getEpicById(3) + "\n");
        System.out.println(manager.getSubsByEpicId(3) + "\n");
        System.out.println(manager.getEpicById(6) + "\n");
        System.out.println(manager.getSubtaskById(7) + "\n");

        manager.deleteTaskById(1);
        manager.deleteSubtaskById(4);
        manager.deleteEpicById(6);

        System.out.println("\nПосле удаления части задач:\n");
        System.out.println(manager.getTaskList() + "\n");
        System.out.println(manager.getEpicById(3) + "\n");
        System.out.println(manager.getSubtaskById(4) + "\n");
        System.out.println(manager.getSubtaskById(5) + "\n");
        System.out.println(manager.getEpicById(6) + "\n");
        System.out.println(manager.getSubtaskById(7) + "\n");

        manager.deleteTasks();
        manager.deleteSubtasks();
        manager.deleteEpics();

        System.out.println("\nПосле удаления всего:\n");
        System.out.println(manager.getTaskList() + "\n");
        System.out.println(manager.getEpicList() + "\n");
        System.out.println(manager.getSubtaskList() + "\n");
    }
}
