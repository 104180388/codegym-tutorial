public class MyListTest {
    public static void main(String[] args) {
        MyList<String> list = new MyList<>();
        list.add("Hello");
        list.add("World");
        list.add("Java");

        System.out.println("Size: " + list.size()); // Size: 3
        System.out.println("Element at index 1: " + list.get(1)); // Element at index 1: World

        MyList<String> clonedList = list.clone();
        System.out.println("Cloned List Size: " + clonedList.size()); // Cloned List Size: 3
        System.out.println("Cloned Element at index 1: " + clonedList.get(1)); // Cloned Element at index 1: World

        System.out.println("Contains 'Java': " + list.contains("Java")); // Contains 'Java': true
        System.out.println("Index of 'World': " + list.indexOf("World")); // Index of 'World': 1

        list.remove(1);
        System.out.println("Size after removal: " + list.size()); // Size after removal: 2
    }
}
