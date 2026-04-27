public class MyLinkedListTest {
    public static void main(String[] args) {
        MyLinkedList<Integer> list = new MyLinkedList<>();
        list.addFirst(1);
        list.addLast(2);
        list.add(1, 3);
        System.out.println(list.get(1));
        System.out.println(list.remove(1));
        System.out.println(list.get(1));
    }
}
