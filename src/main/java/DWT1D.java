import java.util.Arrays;

public class DWT1D {

    private static final double S0 = 0.5 * Math.sqrt(2);
    private static final double S1 = 0.5 * Math.sqrt(2);
    private static final double H0 = 0.5 * Math.sqrt(2);
    private static final double H1 = -0.5 * Math.sqrt(2);
    private static final double G0 = 0.5 * Math.sqrt(2);
    private static final double G1 = 0.5 * Math.sqrt(2);

    /**
     * Forward discrete wavelet transform (DWT) of a 1D signal using Haar wavelet.
     *
     * @param input  the 1D signal to transform.
     * @return the forward DWT coefficients.
     */
    public static double[] forwardHaar(double[] input) {
        if (input.length % 2 != 0) {
            throw new IllegalArgumentException("Input must have even length");
        }

        double[] temp = new double[input.length];
        int h = input.length / 2;
        for (int i = 0; i < h; i++) {
            temp[i] = (input[2 * i] + input[2 * i + 1]) / 2;
            temp[h + i] = (input[2 * i] - input[2 * i + 1]) / 2;
        }
        return temp;
    }

    /**
     * Forward discrete wavelet transform (DWT) of a 1D signal using Daubechies-4 wavelet.
     *
     * @param input  the 1D signal to transform.
     * @return the forward DWT coefficients.
     */
    public static double[] forwardDaubechies4(double[] input) {
        if (input.length % 2 != 0) {
            input = Arrays.copyOf(input, input.length + 1);
            input[input.length - 1] = 0;
        }

        if (input.length % 2 != 0) {
            throw new IllegalArgumentException("Input must have even length");
        }

        double[] temp = new double[input.length];
        int h = input.length / 2;
        for (int i = 0; i < h; i++) {
            temp[i] = H0 * input[2 * i] + H1 * input[2 * i + 1];
            temp[h + i] = G0 * input[2 * i] + G1 * input[2 * i + 1];
        }
        return temp;
    }

    // 反向离散小波变换
    public static double[] inverseHaar(double[] coeffs) {
        if (coeffs.length % 2 != 0) {
            throw new IllegalArgumentException("Number of coefficients must be even");
        }

        double[] temp = new double[coeffs.length];
        int h = coeffs.length / 2;
        for (int i = 0; i < h; i++) {
            temp[2 * i] = (coeffs[i] + coeffs[h + i]) / S0;
            temp[2 * i + 1] = (coeffs[i] - coeffs[h + i]) / S1;
        }
        return temp;
    }

    // 反向离散小波变换
    public static double[] inverseDaubechies4(double[] coeffs) {
        if (coeffs.length % 2 != 0) {
            throw new IllegalArgumentException("Number of coefficients must be even");
        }

        double[] temp = new double[coeffs.length];
        int h = coeffs.length / 2;
        for (int i = 0; i < h; i++) {
            temp[2 * i] = H0 * coeffs[i] + G0 * coeffs[h + i];
            temp[2 * i + 1] = H1 * coeffs[i] + G1 * coeffs[h + i];
        }
        return temp;
    }
}
