package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList history = new CustomLinkedList();

    @Override
    public void add(Task task) {
        history.linkLast(task);
    }

    @Override
    public void remove(long id) { //удаление записи из мапы нод, с последующим удалением самой ноды
        history.removeNode(history.nodes.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
    }

    @Override
    public String toString() {
        return "InMemoryHistoryManager{" +
                "history=" + history +
                '}';
    }

    private class CustomLinkedList {
        private class Node {
            Task task;
            Node next;
            Node prev;

            Node(Node prev, Task task, Node next) {
                this.task = task;
                this.next = next;
                this.prev = prev;
            }
        }

        private Map<Long, Node> nodes = new HashMap<>();
        private Node first;
        private Node last;

        public void linkLast(Task task) { //добавляет задачу в конец списка
            if (nodes.containsKey(task.getId())){
                removeNode(nodes.remove(task.getId()));
            }
            final Node l = last;
            final Node newNode = new Node(l, task, null);
            last = newNode;
            if (l == null) {
                first = newNode;
            } else {
                l.next = newNode;
            }
            nodes.put(task.getId(), newNode);
        }

        public List<Task> getTasks() { //собирает все задачи из мапы нод в ArrayList
            if (nodes.isEmpty()) {
                System.out.println("История просмотров пока что пуста.");
                return new ArrayList<>();
            }

            List<Node> nodeTasks = new ArrayList<>();
            nodeTasks.add(first);
            if (first.next != null) {
                for (int i = 0; i < nodes.size() - 1; i++) {
                    nodeTasks.add(nodeTasks.get(i).next);
                }
            }

            List<Task> tasks = new ArrayList<>();
            for (int i = 0; i < nodeTasks.size(); i++) {
                tasks.add(nodeTasks.get(i).task);
            }
            return tasks;
        }

        public void removeNode(Node node) { //удаляет принятую ноду, перепривязывает соседние
            final Task task = node.task;
            final Node next = node.next;
            final Node prev = node.prev;

            if (prev == null) {
                first = next;
            } else {
                prev.next = next;
                node.prev = null;
            }

            if (next == null) {
                last = prev;
            } else {
                next.prev = prev;
                node.next = null;
            }

            node.task = null;
        }
    }
}
