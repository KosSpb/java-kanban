package managers;

import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() { //возвращает нужную реализацию TaskManager
        return new InMemoryTaskManager();
    }

    public static TaskManager getDefault(String pathToFile) { //возвращает нужную реализацию TaskManager
        return new FileBackedTasksManager((Paths.get(pathToFile).toFile()));
    }

    public static HistoryManager getDefaultHistory() { //возвращает объект - историю просмотров
        return new InMemoryHistoryManager();
    }
}
