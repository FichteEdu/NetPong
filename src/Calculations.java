import java.lang.Math;

public class Calculations {

    public double getLength(double arr[]) {
        return (Math.sqrt((arr[0] * arr[0]) + (arr[1] * arr[1])));
    }

    public double[] normVector(double arr[]) {
        arr[0] = arr[0] / getLength(arr);
        arr[1] = arr[1] / getLength(arr);

        return arr;
    }
}
