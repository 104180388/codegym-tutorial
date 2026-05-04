public class Solution {

    public void enQueue(Queue q, int value) {
        Node newNode = new Node(value); // B1: Tạo node mới

        if (q.front == null) {
            q.front = newNode;
        } else {
            q.rear.link = newNode;
        }

        q.rear = newNode;
        q.rear.link = q.front; // Node rear luôn chứa địa chỉ của node front
    }

    public Integer deQueue(Queue q) {
        if (q.front == null) {
            System.out.println("Hàng đợi trống!");
            return null;
        }

        int value;

        if (q.front == q.rear) {
            value = q.front.data;
            q.front = null;
            q.rear = null;
        } else {
            value = q.front.data;
            q.front = q.front.link;
            q.rear.link = q.front;
        }

        return value;
    }

    public void displayQueue(Queue q) {
        if (q.front == null) {
            System.out.println("Hàng đợi đang trống.");
            return;
        }

        Node temp = q.front;
        System.out.print("Các phần tử trong hàng đợi vòng: ");
        do {
            System.out.print(temp.data + " ");
            temp = temp.link;
        } while (temp != q.front);
        System.out.println();
    }

    public static void main(String[] args) {
        Solution sol = new Solution();
        Queue q = new Queue();

        sol.enQueue(q, 10);
        sol.enQueue(q, 20);
        sol.enQueue(q, 30);

        sol.displayQueue(q);

        System.out.println("Lấy ra: " + sol.deQueue(q));
        System.out.println("Lấy ra: " + sol.deQueue(q));

        sol.displayQueue(q);

        sol.enQueue(q, 40);
        sol.displayQueue(q);
    }
}