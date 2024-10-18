package br.cta.ipev.t25;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Conversion {

    private static final int PACKET_HEADER_SIZE = 14;

    private int bitSinal = 1;

    public static int ARINC429DataFieldSize = 21;

    private int[] pacoteUdp;

    public static double polyval(double[] coefs, double count) {
        double[] cf = (double[])coefs.clone();
        ArrayUtils.reverse(cf);
        PolynomialFunction fcn = new PolynomialFunction(cf);
        return fcn.value(count);
    }

    public static double[] polyfit(double[] data, int OP) {
        WeightedObservedPoints obs = new WeightedObservedPoints();

        for(int i = 0; i < data.length; ++i) {
            obs.add((double)i, data[i]);
        }

        PolynomialCurveFitter fitter = PolynomialCurveFitter.create(OP);
        return fitter.fit(obs.toList());
    }

    public static long mergeWordsLong(long wHigh, long wLow){
        return ( (((long) wHigh) << 16) | (long) wLow);
    }

    public static int TwosComplementH135(long i, int numBits) {

        int msb = (int)(i >> (numBits - 1));
        long valueMask = (long)((int)((Math.pow(2.0, (double)numBits) - 1.0) / 2.0));
        int value = (int)(i & valueMask);
        return msb == 0 ? value : (int)((((long)(~value) & valueMask) - (long) ((Math.pow(2.0, (double)numBits - 5) + Math.pow(2.0, (double)numBits - 8) ))));
    }
    public static long extrairArincH135(long value) {
        long result = value & 4294965248L;
        return result >> 11;
    }

    public static int bcd2dec(int bcd) {
        int dec = 0;

        for(int peso = 0; bcd != 0; bcd >>= 4) {
            dec += (int)((double)(bcd & 15) * Math.pow(10.0, (double)peso));
            ++peso;
        }

        return dec;
    }


    public static double arinc429firstt27m(int valueHI, int valueLO, double resolution , int qtdBits) {

        int var0 = 0;
        int lsb = 0;
        int msb = 0;
        int bitsignal = (valueHI >> 12) & 1;

        if (qtdBits <= 13) {
            msb = (valueHI & (4080));
            lsb = (valueLO >> 12);
            var0 = (msb ^ lsb);
            var0 = (var0 >> 13 - qtdBits);

        } else {
            valueHI = (valueHI >> 4);
            var0 = extrairArinc429First(mergeWords(valueHI,valueLO));
            var0 = var0 >> (ARINC429DataFieldSize - qtdBits);

        }

        return var0 * resolution;

    }
    public static double tcgdoy(int valueHI) {

        int var0 = ((valueHI & 15) + 10) * (((valueHI >> 4) & 15) + 100) * (((valueHI >> 8) & 7));

        return var0;
    }

    public static String longParaBinario(long numero) {
        StringBuilder sb = new StringBuilder(64);
        // Percorre cada bit da variável long, da esquerda para a direita
        for (int i = 63; i >= 0; i--) {
            // Verifica se o bit na posição i está ligado (1) ou desligado (0)
            long bit = (numero >> i) & 1;
            sb.append(bit);
        }
        return sb.toString();
    }

    public static double iprena_float(int word1, int word2, int word3, int word4, double resolution) {

        int word_4_sh =  word4 << 24;
        int word_3_sh =  word3 << 16;
        int word_high = (word_4_sh| word_3_sh);
        int word_2_sh = word2 << 8;
        int word_low = word_2_sh | word1;
        int word_counts = word_high | word_low;


        int sign = word_counts >>> 31;
        int exp = (word_counts >>> 23 & ((1 << 8) - 1)) - ((1 << 7) - 1);
        int mantissa = word_counts & ((1 << 23) - 1);
        double LAT_raw = Float.intBitsToFloat((sign << 31) | (exp + ((1 << 7) - 1)) << 23 | mantissa);

        double LAT_UE = LAT_raw * resolution;
        return LAT_UE;
    }

    public static double iprena_uint16(int word1, int word2, double resolution) {

        int word_2_sh  = word2 >> 8;
       // Log.d("", "word_2_sh: "+word_2_sh);
        int word_counts = word_2_sh | word1;
       // Log.d("", "word_counts: "+word_counts);

        double LAT_UE = word_counts * resolution;
        return LAT_UE;
    }



    public static double iprena_int16(int word1, int word2, double resolution) {

        int word_2_sh  = word2 >> 8;
       // Log.d("", "word_2_sh: "+word_2_sh);
        int word_counts = word_2_sh | word1;
      //  Log.d("", "word_counts: "+word_counts);

        double LAT_UE = word_counts * resolution;
        return LAT_UE;
    }

    public static double iprena_uint32(int word1, int word2, double resolution) {

        int word_2_sh  = word2 >> 8;
      //  Log.d("", "word_2_sh: "+word_2_sh);
        int word_counts = word_2_sh | word1;
       // Log.d("", "word_counts: "+word_counts);

        double LAT_UE = word_counts * resolution;
        return LAT_UE;
    }

    public static double bit1(int word1, int bits) {

        int var0 = (word1 >>> bits) & 1;

        return var0;
    }

    public static double bit2(int word1, int bits) {

        int var0 = (word1 >>> bits) & 3;

        return var0;
    }

    public static double bit3(int word1, int bits) {

        int var0 = (word1 >>> bits) & 7;

        return var0;
    }

    public static double edclinsigned(int var_1, int var_2 , int var_3, int var_4, int var_5, double c0 , double c1 , double c2 , double c3 , double c4 , double c5) {

        double var0;
        double[] coefs = {c5, c4, c3, c2, c1, c0};
        int var1 = var_1 >> 8;
        int var2 = var_2 >> 8;
        int var3 = var_3 >> 8;
        int var4 = var_4 >> 8;
        int var5 = var_5 >> 8;

        if (var1 < 48 || (var1 > 57 && var1 < 65) || (var1 > 70 && var1 < 97) || var1 > 102) {
            var1 = 48;
        }

        if (var2 < 48 || (var2 > 57 && var2 < 65) || (var2 > 70 && var2 < 97) || var2 > 102) {
            var2 = 102;
        }
        if (var3 < 48 || (var3 > 57 && var3 < 65) || (var3 > 70 && var3 < 97) || var3 > 102) {
            var3 = 102;
        }
        if (var4 < 48 || (var4 > 57 && var4 < 65) || (var4 > 70 && var4 < 97) || var4 > 102) {
            var4 = 102;
        }
        if (var5 < 48 || (var5 > 57 && var5 < 65) || (var5 > 70 && var5 < 97) || var5 > 102) {
            var5 = 102;
        }

        String var0_string = "" + (char) var2 + (char) var3 + (char) var4 + (char) var5;
        int var0_int = Integer.parseInt(var0_string, 16);
        String sinal = ""+ (char) var1;


        if (sinal.contentEquals("4")) {


            var0 = polyval(coefs, var0_int);
            return  var0 * -1;
        } else{

            var0 = polyval(coefs, var0_int);
            return  var0;
        }

    }

    public static double edclinunsigned(int var_2 , int var_3, int var_4, int var_5, double c0 , double c1 , double c2 , double c3 , double c4, double c5) {

        double var0;
        double[] coefs = {c5, c4, c3, c2, c1, c0};
        int var2 = var_2 >> 8;
        int var3 = var_3 >> 8;
        int var4 = var_4 >> 8;
        int var5 = var_5 >> 8;


        if (var2 < 48 || (var2 > 57 && var2 < 65) || (var2 > 70 && var2 < 97) || var2 > 102) {
            var2 = 102;
        }
        if (var3 < 48 || (var3 > 57 && var3 < 65) || (var3 > 70 && var3 < 97) || var3 > 102) {
            var3 = 102;
        }
        if (var4 < 48 || (var4 > 57 && var4 < 65) || (var4 > 70 && var4 < 97) || var4 > 102) {
            var4 = 102;
        }
        if (var5 < 48 || (var5 > 57 && var5 < 65) || (var5 > 70 && var5 < 97) || var5 > 102) {
            var5 = 102;
        }

        String var0_string = "" + (char) var2 + (char) var3 + (char) var4 + (char) var5;

        int var0_int = Integer.parseInt(var0_string, 16);
        var0 = polyval(coefs, var0_int);
        return  var0;


    }

    public static double trig_MaiorIgual(int word1 , double[] coef){
        double var0;

        double[] coef1 = {coef[4], coef[5], coef[6], coef[7], coef[8], coef[9]};
        double[] coef2 = {coef[11], coef[10], coef[0], coef[1], coef[2], coef[3]};

        double limite = coef[12];

        if (word1 > limite ){
            var0 = polyval(coef1, word1);
            return var0;
        } else{
            var0 = polyval(coef2, word1);
            return var0;
        }
    }

    public static double trig_MenorIgual(int word1 , double[] coef){
        double var0;

        double[] coef1 = {coef[4], coef[5], coef[6], coef[7], coef[8], coef[9]};
        double[] coef2 = {coef[11], coef[10], coef[0], coef[1], coef[2], coef[3]};

        double limite = coef[12];

        if (word1 > limite ){
            var0 = polyval(coef1, word1);
            return var0;
        } else{
            var0 = polyval(coef2, word1);
            return var0;
        }
    }

    public static double iprena_float_corrigida(int word1, int word2, int word3, int word4, double resolution) {

        int word_4_sh =  word4 << 24;
        int word_3_sh =  word3 << 16;
        int word_high = (word_4_sh| word_3_sh);
        int word_2_sh = word2 << 8;
        int word_low = word_2_sh | word1;
        int word_counts = word_high | word_low;


        int sign = word_counts >>> 31;
        int exp = (word_counts >>> 23 & ((1 << 8) - 1)) - ((1 << 7) - 1);
        int mantissa = word_counts & ((1 << 23) - 1);
        double word_raw = Float.intBitsToFloat((sign << 31) | (exp + ((1 << 7) - 1)) << 23 | mantissa);

        double word_UE = word_raw * resolution;
        return word_UE * -1;
    }



    public static double iprena_double(int word1, int word2, int word3, int word4, int word5, int word6, int word7, int word8, double resolution) {

        long word_8_sh = (long) word8 << 48;
        long word_6_sh = (long) word6 << 32;
        long word_high = (word_8_sh | word_6_sh);
        long word_4_sh = (long) word4 << 16;
        long word_2_sh = (long)word2;
        long word_low = word_4_sh | word_2_sh;
        long word_counts = word_high | word_low;

        //typecast to Double 64bits
        long sign = word_counts >>> 63;
        long exp = (word_counts >>> 52 & ((1 << 11) - 1)) - ((1 << 10) - 1);
        long mantissa = word_counts & ((1L << 52) - 1);
        double word_raw = Double.longBitsToDouble((sign << 63) | (exp + ((1 << 10) - 1)) << 52 | mantissa);

        //aplicando resolution
        double word_UE = word_raw * resolution;

        return word_UE;

    }

    public static double qb2vc(double qb, double [] coef) {

        double qb_ue = polyval(coef, qb);

        return 661.5 * Math.pow(5 * (Math.pow(((qb_ue / 1013.25) + 1), 0.28571) - 1), 0.5);
    }

    public static double qc2vc(double qc, double[] coef) {

        double[] coef_ = new double[] { coef[11], coef[10], coef[0], coef[1], coef[2],coef[3],coef[4],coef[5],coef[6],coef[7],coef[8], coef[9]};

        for (double valor : coef_){
           //Log.d("coef qc2vc" , "valor dos coef : "+valor);
        }


        double qc_ue = polyval(coef_, qc);
        double vc = 661.464 * Math.pow(5 * (Math.pow((qc_ue / 1013.25 + 1), 2.0 / 7) - 1), 0.5);
       // Log.d("qc2vc" , "vc : "+vc);
        boolean a1 = vc >= 661.464;
        if (a1) {
            vc += 257.97 * Math.pow((vc / 661.464 - 1), 2.55);
        }
        //Log.d("qc2vc" , "vc : "+vc);
        return vc;
    }
    public static double pa2zp(double pa, double[] coef) {

        double[] coef_ = new double[] { coef[11], coef[10], coef[0], coef[1], coef[2],coef[3],coef[4],coef[5],coef[6],coef[7],coef[8], coef[9]};

        double pa_ue = polyval(coef_, pa);
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


    public static double tcgvel(int valueHI) {

        int var0 = ((valueHI & 15) + 10) * (((valueHI >> 4) & 15) + 100) * (((valueHI >> 8) & 15) + 1000) * (((valueHI >> 12) & 15));
        return var0;
    }


    public static double bin(int valueHI, double[] coef) {

        try {

            double[] coef_ = new double[] { coef[11], coef[10], coef[0], coef[1], coef[2],coef[3],coef[4],coef[5],coef[6],coef[7],coef[8], coef[9]};

            double var0 = polyval(coef_, valueHI);

            return var0;

        } catch (ArrayIndexOutOfBoundsException e){

            double var0 = polyval(coef, valueHI);

            return var0;

        }



    }

    public static double arinc429firstt27mbcd(int valueHI, int valueLO, double resolution, int QtdBits) {

        int var0 = 0;

        if (QtdBits == 1) {
            var0 = ((valueHI >> 10) & 7);
            return var0 * resolution;

        } else if ( QtdBits == 2) {

            int bit5 = ((valueHI >> 10) & 7) * 10;
            int bit4 = ((valueHI >> 6) & 15);
            var0 = bit4 + bit5;
            return var0 * resolution;
        } else if (QtdBits == 3) {
            int bit5 = ((valueHI >> 10) & 7) * 100;
            int bit4 = ((valueHI >> 6) & 15) * 10;
            int bit3 = (valueHI >> 4 & 3) * 4 + (((valueLO >> 14) & 3));
            var0 = bit5 + bit4 + bit3;
            return var0 * resolution;

        } else if (QtdBits == 4) {
            int bit5 = ((valueHI >> 10) & 7) * 1000;
            int bit4 = ((valueHI >> 6) & 15) * 100;
            int bit3 = (valueHI >> 4 & 3) * 4 + (((valueLO >> 14) & 3) * 10) ;
            int bit2 = ((valueLO >> 10) & 15);
            var0 = bit5 + bit4 + bit3 + bit2;
            return var0 * resolution;

        } else if (QtdBits == 5) {
            int bit5 = ((valueHI >> 10) & 7) * 10000;
            int bit4 = ((valueHI >> 6) & 15) * 1000;
            int bit3 = (valueHI >> 4 & 3) * 4 + (((valueLO >> 14) & 3) * 100) ;
            int bit2 = ((valueLO >> 10) & 15) * 10;
            int bit1 = ((valueLO >> 6) & 15);
            var0 = bit5 + bit4 + bit3 + bit2 + bit1;
            return var0 * resolution;
        }
        return var0;
    }

    public static int mergeWords(int wHigh, int wLow){
        return ( (wHigh << 16) |  wLow);
    }

    public static int extrairArinc429First(int value) {
        int result = value & 16777152;

        return result >> 6;
    }

    public static int extrairArincC105(int value) {
        int result = value & 1073739776;

        return result >> 11;
    }

    public static int extrairArinc429Last(int value) {
        //ajuste valores para extracao
        int result = value & 1073739776;
        return result >> 11;
    }

    public static int TwosComplement(long i, int numBits) {
        int msb = (int)(i >> numBits - 1);
        long valueMask = (long)((int)((Math.pow(2.0, (double)numBits) - 1.0) / 2.0));
        int value = (int)(i & valueMask);
        return msb == 0 ? value : (int)(-1L * (((long)(~value) & valueMask) + 1L));
    }

    public char[] formatInt(byte[] packet){
        int times = Short.SIZE / Byte.SIZE;
        char[] result = new char[packet.length / times];

        for (int i=0; i < result.length; i++){
            result[i] = ByteBuffer.wrap(packet,i * times,times).order(ByteOrder.BIG_ENDIAN).getChar();
        }
        return result;
    }

}
