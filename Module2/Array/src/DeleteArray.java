import java.util.Arrays;

public class DeleteArray {
    public static void main(String[] args) {
        int[] arr = {10, 4, 6, 7, 8, 6, 0, 0, 0, 0};
        int indexDel = -1; // Initialize with -1 to check if element was found
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 7) {
                indexDel = i;
                break;
            }
        }

        if (indexDel != -1) {
            for (int j = indexDel; j < arr.length - 1; j++) {
                arr[j] = arr[j + 1];
            }
            arr[arr.length - 1] = 0;
        }
        System.out.println(Arrays.toString(arr));
    }
}
