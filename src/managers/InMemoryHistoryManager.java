package managers;

import tasks.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private CustomLinkedList history = new CustomLinkedList();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.linkLast(task);
        } else {
            System.out.println("Предотвращена попытка передать null в метод по добавлению просмотренной задачи");
        }

    }

    @Override
    public void remove(long id) { //удаление записи из мапы нод, с последующим удалением самой ноды
        history.removeNode(history.nodes.remove(id));
    }

    @Override
    public List<Task> getHistory() {
        return history.getTasks();
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
                return new ArrayList<>();
            }

            List<Task> tasks = new ArrayList<>();
            Node node = first;
            while (node != null) {
                tasks.add(node.task);
                node = node.next;
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
