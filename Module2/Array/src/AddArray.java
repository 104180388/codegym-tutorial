import java.util.Arrays;
import java.util.Scanner;

public class AddArray {
    public static void main(String[] args) {
        int[] arr = {10, 4, 6, 7, 8, 0, 0, 0, 0, 0};
        System.out.print("Enter a number: ");
        Scanner scanner = new Scanner(System.in);
        int addNumber = scanner.nextInt();
        System.out.print("Enter position: ");
        int numberIndex = scanner.nextInt();

        for(int i = arr.length - 1; i > numberIndex; i--) {
            arr[i] = arr[i - 1];
        }
        arr[numberIndex] = addNumber;

        System.out.print(Arrays.toString(arr));
    }
}
