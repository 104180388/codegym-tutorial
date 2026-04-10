import java.util.Scanner;

public class PrimaryNumberCount {
    public static boolean checkPrime(int x) {
        if (x < 2){
            return false;
        } else {
            int i = 2;
            boolean check = true;
            while (i <= Math.sqrt(x)) {
                if (x % i == 0) {
                    check = false;
                    break;
                }
                i++;
            }
            return check;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int numbers = sc.nextInt();
        int count = 0;
        int n=2;

        while(count<numbers){
            if(checkPrime(n)){
                System.out.print(n+" ");
                count++;
            }
            n++;
        }
    }
}
