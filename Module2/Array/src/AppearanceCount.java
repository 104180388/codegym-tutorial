import java.util.Scanner;

public class AppearanceCount {
    public static void main(String args[]){
        String str = "Hello fellas";
        char[] charArray = str.toCharArray();
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the letter:");
        char letter = sc.next().charAt(0);
        int count = 0;
        for(int i=0;i<charArray.length;i++){
            if(charArray[i]==letter){
                count++;
            }
        }
        System.out.println("There are "+count+" letters "+ letter+" in "+str);
    }
}
