package br.cta.ipev.t25;

import android.util.Log;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Conversion {

    public static double polyval(double[] coefs, double count) {
        double[] cf = (double[])coefs.clone();
        ArrayUtils.reverse(cf);
        PolynomialFunction fcn = new PolynomialFunction(cf);
        return fcn.value(count);
    }

    public static int fuelflow(double ff_total , double data ) {
        return  (int) (ff_total - ( data / 32));
    }

    public static double[] polyfit(double[] data, int OP) {
        WeightedObservedPoints obs = new WeightedObservedPoints();

        for(int i = 0; i < data.length; ++i) {
            obs.add((double)i, data[i]);
        }

        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(OP);
        return fitter.fit(obs.toList());
    }


    public static int bcd2dec(int bcd) {
        int dec = 0;

        for(int peso = 0; bcd != 0; bcd >>= 4) {
            dec += (int)((double)(bcd & 15) * Math.pow(10.0, (double)peso));
            ++peso;
        }

        return dec;
    }

    public static double bit1(int word1, int bits) {

        int var0 = (word1 >>> bits) & 1;

        return var0;
    }

    public static double qb2vc(double qb, double [] coef) {

        double qb_ue = polyval(coef, qb);

        return 661.5 * Math.pow(5 * (Math.pow(((qb_ue / 1013.25) + 1), 0.28571) - 1), 0.5);
    }

    public static double qc2vc(int qc, double[] coef) {

        double qc_ue = polyval(coef, qc);


        double vc = 661.464 * Math.pow(5 * (Math.pow((qc_ue / 1013.25 + 1), 2.0 / 7) - 1), 0.5);


        boolean a1 = vc >= 661.464;
        if (a1) {
            vc += 257.97 * Math.pow((vc / 661.464 - 1), 2.55);
        }
        return vc;
    }

    public static int RoundedBy10(double value) {
        return (int) (Math.round(value / 10.0) * 10);
    }

    public static double pa2zp(int pa, double[] coef) {

        double pa_ue = polyval(coef, pa);
        boolean a1 = pa_ue >= 226.306;
        boolean a2 = pa_ue < 226.306;
        double zp = 0.0;

        if (a1) {
            zp = (1 - Math.pow((pa_ue / 1013.25), 0.190255)) / 6.87559e-6;
        } else if (a2) {
            zp = 11000 - Math.log(pa_ue / 226.306) / 0.000157696;
            zp /= 0.3048; // Convertendo para metros
        }
        return zp;
    }


}
