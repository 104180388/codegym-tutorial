import java.util.Scanner;

public class Money {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("USD: ");
        int money = Integer.parseInt(scanner.nextLine());
        int vnd = money * 23000;

        System.out.println(vnd+ " VND");
        scanner.close();
    }
}
