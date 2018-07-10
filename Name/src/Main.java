import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        System.out.println("Hello World!");
        System.out.print("Anders");
        int[] array = {1,2,3};
        printArray(array);

        double[] doubleArray = {1.2,2.2,3.2};
        printDoubleArray(doubleArray);

        sum(20,30);

        System.out.println(factorial(6));
    }

    public static void printArray(int[] array){
        System.out.println(Arrays.toString(array));
    }
    public static void printDoubleArray(double[] array){
        System.out.println(Arrays.toString(array));
    }

    public static int factorial(int n){
        int factorial = n;

        for(int i = n - 1; i > 1; i--){
            factorial *= i;
        }

        return factorial;
    }

    public static void sum(int a, int b){
        System.out.println(a+b);
    }
}
