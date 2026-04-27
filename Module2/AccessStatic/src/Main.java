public class Main {
    public static void main(String args[]) {
//        Student.change(); //calling change method
//
//        //creating objects
//        Student s1 = new Student(111, "Hoang");
//        Student s2 = new Student(222, "Khanh");
//        Student s3 = new Student(333, "Nam");
//
//        //calling display method
//        s1.display();
//        s2.display();
//        s3.display();

        Car car1 = new Car("Mazda 3", "Skyactiv 3");
        System.out.println(Car.numberOfCars);
        Car car2 = new Car("Mazda 6", "Skyactiv 6");
        System.out.println(Car.numberOfCars);
    }
}