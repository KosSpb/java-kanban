import managers.HttpTaskManager;
import network.HttpTaskServer;
import network.KVServer;

public class Main {

    public static void main(String[] args) {
        try {
            new KVServer().start();
            new HttpTaskServer(new HttpTaskManager("http://localhost:8078/")).start();
        } catch (Exception exception) {
            System.out.println(exception.getMessage());
            exception.printStackTrace();
        }
    }
}
