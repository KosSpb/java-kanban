package managers;

import java.nio.file.Files;
import java.nio.file.Paths;

public class Managers {

    public static TaskManager getDefault() { //возвращает нужную реализацию TaskManager
        return new InMemoryTaskManager();
    }

    public static TaskManager getFileBackedTasksManager() {
        if (Files.exists(Paths.get("resources/FileBackedTasks.csv"))) {
            return FileBackedTasksManager.loadFromFile((Paths.get("resources/FileBackedTasks.csv")).toFile());
        } else {
            return new FileBackedTasksManager((Paths.get("resources/FileBackedTasks.csv")).toFile());
        }
    }

    public static HistoryManager getDefaultHistory() { //возвращает объект - историю просмотров
        return new InMemoryHistoryManager();
    }
}
