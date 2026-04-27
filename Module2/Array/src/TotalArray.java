import java.util.Arrays;
import java.util.Scanner;

public class TotalArray {
    public static void main(String[]args){
        int sumCol = 0;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the row of the array");
        int rows = sc.nextInt();
        System.out.println("Enter the column of the array");
        int cols = sc.nextInt();
        int[][] array = new int[rows][cols];

        for(int i=0;i<rows;i++){
            for(int j=0;j<cols;j++){
                System.out.println("Enter the elements of the array");
                array[i][j] = sc.nextInt();
            }
        }
        System.out.println("Enter column you want to calculate");
        int column = sc.nextInt();
        for(int k=0;k<cols;k++){
            sumCol = sumCol + array[k][column];
        }
        System.out.println(Arrays.deepToString(array));
        System.out.println("Sum of column is: "+sumCol);
    }
}
