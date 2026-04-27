import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
//        Scanner scanner = new Scanner(System.in);
//        System.out.print("Enter the a:");
//        double a  = scanner.nextDouble();
//        System.out.print("Enter the b:");
//        double b  = scanner.nextDouble();
//        System.out.print("Enter the c:");
//        double c  = scanner.nextDouble();
//
//        QuadraticEquation demo = new QuadraticEquation(a, b, c);
//        if(demo.delta > 0){
//            System.out.println("The equation has 2 root: "+demo.getRoot1()+" and "+demo.getRoot2());
//        }
//        else if(demo.delta == 0){
//            System.out.println("The equation has 1 root: "+demo.getRoot1());
//        }
//        else{
//            System.out.println("The equation has no real root");
//        }

        Fan fan1 = new Fan();
        fan1.setSpeed(3);
        fan1.setRadius(10);
        fan1.setColor("yellow");
        fan1.setOn(true);
        System.out.println(fan1);
        Fan fan2 = new Fan();
        fan2.setSpeed(2);
        fan2.setRadius(5);
        fan2.setColor("blue");
        fan2.setOn(false);
        System.out.println(fan2);
    }
}