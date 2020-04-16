import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Solver {


    public static double getSX(double[] arr, int n) {
        double s = 0;
        for (int i = 0; i < n; i++) {
            s += arr[i];
        }
        return s;
    }

    public static double getSXX(double[] xArr, int n) {
        double sxx = 0;
        for (int i = 0; i < n; i++) {
            sxx += Math.pow(xArr[i], 2);
        }
        return sxx;
    }

    public static double getSXXX(double[] xArr, int n) {
        double sxxx = 0;
        for (int i = 0; i < n; i++) {
            sxxx += Math.pow(xArr[i], 3);
        }
        return sxxx;
    }

    public static double getSXXXX(double[] xArr, int n) {
        double sxxxx = 0;
        for (int i = 0; i < n; i++) {
            sxxxx += Math.pow(xArr[i], 4);
        }
        return sxxxx;
    }

    public static double getSXY(double[] xArr, double[] yArr, int n) {
        double sxy = 0;
        for (int i = 0; i < n; i++) {
            sxy += xArr[i] * yArr[i];
        }
        return sxy;
    }

    public static double getSXXY(double[] xArr, double[] yArr, int n) {
        double sxxy = 0;
        for (int i = 0; i < n; i++) {
            sxxy += Math.pow(xArr[i], 2) * yArr[i];
        }
        return sxxy;
    }

    public static double[] linearApprox(double[] xArr, double[] yArr, int n) {
        double a, b, s = 0, cigma;
        a = (getSXY(xArr, yArr, n) * n - getSX(xArr, n) * getSX(yArr, n)) / (getSXX(xArr, n) * n - Math.pow(getSX(xArr, n), 2));
        b = (getSXX(xArr, n) * getSX(yArr, n) - getSX(xArr, n) * getSXY(xArr, yArr, n)) / (getSXX(xArr, n) * n - Math.pow(getSX(xArr, n), 2));
        for (int i = 0; i < n; i++) {
            s += Math.pow((a * xArr[i] + b - yArr[i]), 2);
        }
        cigma = Math.sqrt(s/n);
        return new double[]{a, b, s, cigma};
    }

    public static double[] quadraticApprox(double[] xArr, double[] yArr, int n) {
        double a, b, c, s = 0, cigma;
        RealMatrix coefficients =
                new Array2DRowRealMatrix(new double[][]{
                        {n, getSX(xArr, n), getSXX(xArr, n)},
                        {getSX(xArr, n), getSXX(xArr, n), getSXXX(xArr, n)},
                        {getSXX(xArr, n), getSXXX(xArr, n), getSXXXX(xArr, n)}
                },
                        false);
        DecompositionSolver solver = new LUDecomposition(coefficients).getSolver();
        RealVector constants = new ArrayRealVector(new double[]{getSX(yArr, n), getSXY(xArr, yArr, n), getSXXY(xArr, yArr, n)}, false);
        RealVector solution = solver.solve(constants);
        a = solution.getEntry(2);
        b = solution.getEntry(1);
        c = solution.getEntry(0);
        for (int i = 0; i < n; i++) {
            s += Math.pow((a * xArr[i] * xArr[i] + b * xArr[i] + c - yArr[i]), 2);
        }
        cigma = Math.sqrt(s/n);
        return new double[]{a, b, c, s, cigma};
    }


    public static double[] powApprox(double[] xArr, double[] yArr, int n) {
        double a, b, s = 0, cigma;
        for (int i = 0; i < n; i++) {
            xArr[i] = Math.log(xArr[i]);
            yArr[i] = Math.log(yArr[i]);
        }
        double[] linansw = linearApprox(xArr, yArr, n);
        a = Math.pow(Math.E, linansw[1]);
        b = linansw[0];
        for (int i = 0; i < n; i++) {
            xArr[i] = Math.pow(Math.E, xArr[i]);
            yArr[i] = Math.pow(Math.E, yArr[i]);
            s += Math.pow((a * Math.pow(xArr[i], b) - yArr[i]), 2);
        }
        cigma = Math.sqrt(s/n);
        return new double[]{a, b, s, cigma};
    }

    public static double[] expApprox(double[] xArr, double[] yArr, int n) {
        double a, b, s = 0, cigma;
        for (int i = 0; i < n; i++) {
            yArr[i] = Math.log(yArr[i]);
        }
        double[] linansw = linearApprox(xArr, yArr, n);
        a = Math.pow(Math.E, linansw[1]);
        b = linansw[0];
        for (int i = 0; i < n; i++) {
            yArr[i] = Math.pow(Math.E, yArr[i]);
            s += Math.pow((a * Math.pow(Math.E, b * xArr[i]) - yArr[i]), 2);
        }
        cigma = Math.sqrt(s/n);
        return new double[]{a, b, s, cigma};
    }

    public static double[] logApprox(double[] xArr, double[] yArr, int n) {
        double a, b, s = 0, cigma;
        for (int i = 0; i < n; i++) {
            xArr[i] = Math.log(xArr[i]);
        }
        double[] linansw = linearApprox(xArr, yArr, n);
        a = linansw[0];
        b = linansw[1];
        for (int i = 0; i < n; i++) {
            xArr[i] = Math.pow(Math.E, xArr[i]);
            s += Math.pow((a * Math.log(xArr[i]) + b - yArr[i]), 2);
        }
        cigma = Math.sqrt(s/n);
        return new double[]{a, b, s, cigma};
    }
}
