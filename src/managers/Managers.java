package managers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Managers {

    public static TaskManager getDefault() { //возвращает нужную реализацию TaskManager
        return new HttpTaskManager("http://localhost:8078/");
    }

    public static HistoryManager getDefaultHistory() { //возвращает объект - историю просмотров
        return new InMemoryHistoryManager();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.serializeNulls();
        return gsonBuilder.create();
    }
}
