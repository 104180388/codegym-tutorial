import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the a:");
        double a  = scanner.nextDouble();
        System.out.print("Enter the b:");
        double b  = scanner.nextDouble();
        System.out.print("Enter the c:");
        double c  = scanner.nextDouble();

        QuadraticEquation demo = new QuadraticEquation(a, b, c);
        if(demo.delta > 0){
            System.out.println("The equation has 2 root: "+demo.getRoot1()+" and "+demo.getRoot2());
        }
        else if(demo.delta == 0){
            System.out.println("The equation has 1 root: "+demo.getRoot1());
        }
        else{
            System.out.println("The equation has no real root");
        }
    }
}