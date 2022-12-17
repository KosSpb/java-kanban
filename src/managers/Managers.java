package managers;

public class Managers {

    public static TaskManager getDefault() { //возвращает нужную реализацию TaskManager
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() { //возвращает объект - историю просмотров
        return new InMemoryHistoryManager();
    }
}
