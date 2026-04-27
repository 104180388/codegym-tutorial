import java.util.Stack;
import java.util.Arrays;

public class Reverse {
    public static void main(String[] args) {
        int[] a = {3, 7, 1, 4, 5};
        System.out.println("Mảng ban đầu: " + Arrays.toString(a));

        Stack<Integer> stack = new Stack<>();

        for (int i = 0; i < a.length; i++) {
            stack.push(a[i]);
        }

        for (int i = 0; i < a.length; i++) {
            a[i] = stack.pop();
        }

        System.out.println("Mảng sau khi đảo: " + Arrays.toString(a));
    }
}