import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.print("Anders");
        int[] array = {1,2,3};
        printArray(array);

        int a= 10;
        int b= 210;

        add(a,b);
    }

    public static void printArray(int[] array){
        System.out.println(Arrays.toString(array));
    }

    public static void add (int a, int b){
        System.out.print(a+b);

    }
}
