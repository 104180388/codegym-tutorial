import java.util.Arrays;

public class CombineArray {
    public static void main(String[] args) {
        int[] arr1 = {10, 4, 6, 7, 8, 6,7};
        int[] arr2 = {2, 5, 11, 3, 21, 9, 10};
        int[] arr3 = new int[arr1.length + arr2.length];
        for (int i = 0; i < arr1.length; i++){
            arr3[i] = arr1[i];
        };
        for (int j = arr1.length; j < arr3.length; j++){
            arr3[j] = arr2[j-arr1.length];
        }
        System.out.print(Arrays.toString(arr3));
    }
}
