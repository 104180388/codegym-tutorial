public class MyLinkedList<E> {
    private Node head;
    private int numNodes = 0;

    public MyLinkedList() {
        head = null;
    }

    // Thêm vào đầu danh sách
    public void addFirst(E e) {
        Node temp = head;
        head = new Node(e);
        head.next = temp;
        numNodes++;
    }

    // Thêm vào cuối danh sách
    public void addLast(E e) {
        if (head == null) {
            addFirst(e);
        } else {
            Node temp = head;
            while (temp.next != null) {
                temp = temp.next;
            }
            temp.next = new Node(e);
            numNodes++;
        }
    }

    // Thêm vào vị trí index bất kỳ
    public void add(int index, E element) {
        if (index <= 0) {
            addFirst(element);
        } else if (index >= numNodes) {
            addLast(element);
        } else {
            Node temp = head;
            for (int i = 0; i < index - 1 && temp.next != null; i++) {
                temp = temp.next;
            }
            Node holder = temp.next;
            temp.next = new Node(element);
            temp.next.next = holder;
            numNodes++;
        }
    }

    // Lấy phần tử tại vị trí index
    public E get(int i) {
        Node temp = head;
        for (int j = 0; j < i; j++) {
            temp = temp.next;
        }
        return (E) temp.data;
    }

    // Xóa phần tử tại vị trí index
    public E remove(int index) {
        if (index < 0 || index >= numNodes) return null;

        Node temp = head;
        Object data;

        if (index == 0) {
            data = head.data;
            head = head.next;
        } else {
            for (int i = 0; i < index - 1; i++) {
                temp = temp.next;
            }
            data = temp.next.data;
            temp.next = temp.next.next;
        }
        numNodes--;
        return (E) data;
    }

    public int size() {
        return numNodes;
    }

    public void clear() {
        head = null;
        numNodes = 0;
    }

    // Các phương thức khác như contains, indexOf sẽ duyệt từ đầu đến cuối để tìm data
}