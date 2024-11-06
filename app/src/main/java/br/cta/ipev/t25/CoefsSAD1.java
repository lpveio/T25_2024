package br.cta.ipev.t25;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import br.cta.isad.iCounts2UE;
import br.cta.isad.EV;

public class CoefsSAD1 extends CoefsSAD implements iCounts2UE {

    public static final String PREF_FILE_NAME = "T25_DETOT";
    public static final int TOP_HI = 57 - OFFSET_IENA;
    public static final int TOP_LO = 58 - OFFSET_IENA;

    public static final int TCG102C_0_J3_HI_TI = 41 - OFFSET_IENA;
    public static final int TCG102C_0_J3_LO_TI = 48 - OFFSET_IENA;
    public static final int TCG102C_0_J3_MI_TI=  49 - OFFSET_IENA;

    public static final int FF = 26 - OFFSET_IENA;
    public static final int T5 = 55 - OFFSET_IENA;
    public static final int RPM_LO = 31 - OFFSET_IENA;

    public static final int M_GASES = 20 - OFFSET_IENA;
    public static final int M_MISTURA = 21 - OFFSET_IENA;
    public static final int M_PASSO = 22 - OFFSET_IENA;
    public static final int P_ADMIN = 24 - OFFSET_IENA;
    public static final int DSI_LO = 11 - OFFSET_IENA;
    public static final int DSI_HI = 9 - OFFSET_IENA;
    public static final int EGT = 13 - OFFSET_IENA;
    public static final int TA = 23  - OFFSET_IENA;
    public static final int QB = 29 - OFFSET_IENA;
    public static final int PB = 25 - OFFSET_IENA;
    public static final int PCOMB = 26  - OFFSET_IENA;
    public static final int POLEO = 28 - OFFSET_IENA;
    public static final int TOLEO = 59  - OFFSET_IENA;
    public static final int FLAPE = 14 - OFFSET_IENA;
    public static final int TI = 56 - OFFSET_IENA;
    public static final int FLAPE_SYNCHRO = 15 - OFFSET_IENA;
    public static int DETOT_TOTAL;
    public static double DETOT_CONSUMIDO;
    public static double DETOT_MOMENTO;
    private Context context;
    public SharedPreferences sharedPref;

    public CoefsSAD1(Context context) {
        this.context = context;
        initializeDetotTotal();
    }

    private void initializeDetotTotal() {
        sharedPref = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        DETOT_TOTAL = sharedPref.getInt("detot", 0);
    }

    public void UpdateDETOT(int detot){
        DETOT_TOTAL = detot;
    }

    public double  CalculeDETOT (double Detot_momento) {

        DETOT_MOMENTO =  (Detot_momento/ 60 * 60) / 32000;
        DETOT_CONSUMIDO += DETOT_MOMENTO;
        return DETOT_TOTAL - DETOT_CONSUMIDO;
    }


    @Override
    public double[] convert(char[] counts) {
        double[] result = new double[Index.values().length];
        double[] CV;

        //TEMPO
        result[Index.TEMPO.ordinal()] = EV.sadtime2secs(0xffff & counts[TCG102C_0_J3_HI_TI], 0xffff & counts[TCG102C_0_J3_LO_TI], 0xffff & counts[TCG102C_0_J3_MI_TI]);
        // TOP
        result[Index.TOP.ordinal()] = counts[TOP_LO];

        //RPM
        CV = new double[]{0.40268456375838926174496644295302};
        result[Index.RPM.ordinal()] = EV.polyval(CV,counts[RPM_LO]);

        //EGT
        CV = new double[]{4.519184E-02, -4.578916E02};
        result[Index.EGT.ordinal()] = EV.polyval(CV,counts[EGT]);

        //FLAPE
        CV = new double[]{1.030570E-13, -1.440329E-08, -5.643074E-04, 6.084511E01};
        result[Index.FLAPE_SYNCHRO.ordinal()] = EV.polyval(CV,counts[FLAPE_SYNCHRO]);

        //OAT
        CV = new double[]{8.894448E-12, -5.778276E-07, 2.243144E-02, -3.547702E02};
        result[Index.TA.ordinal()] = EV.polyval(CV,counts[TA]);

        //P_ADMISSAO
        CV = new double[]{2.284141E-14, -1.679870E-09, 2.609061E-04, -1.159790E-01};
        result[Index.P_ADMIN.ordinal()] = EV.polyval(CV,counts[P_ADMIN]);

        //MANETE_GASES
        CV = new double[]{-1.050471E-11, 4.873513E-07, -1.446523E-02, 1.709800E02};
        result[Index.MANETE_GASES.ordinal()] = EV.polyval(CV,counts[M_GASES]);

        //MANETE_MISTURA
        CV = new double[]{-8.986256E-13, 7.979735E-09, -4.473450E-03, 1.091125E02};
        result[Index.MANETE_MISTURA.ordinal()] = EV.polyval(CV,counts[M_MISTURA]);

        //MANETE_PASSO
        CV = new double[]{-4.837895E-12, 1.454990E-07, -7.341558E-03};
        result[Index.MANETE_PASSO.ordinal()] = EV.polyval(CV,counts[M_PASSO]);

        //CAPOTA
        result[Index.CAPOTA.ordinal()] = Conversion.bit1(counts[DSI_LO], 12);


        //LUZ CIMA 1
        result[Index.LUZES_TREM_1.ordinal()] = Conversion.bit1(counts[DSI_HI], 6);

        //LUZ CIMA 2
        result[Index.LUZES_TREM_2.ordinal()] = Conversion.bit1(counts[DSI_HI], 7);

        //LUZ CIMA 3
        result[Index.LUZES_TREM_3.ordinal()] = Conversion.bit1(counts[DSI_HI], 8);

        //LUZ TRANSITO 1
        result[Index.LUZES_TRANSITO_1.ordinal()] = Conversion.bit1(counts[DSI_HI], 9);

        //LUZ TRANSITO 2
        result[Index.LUZES_TRANSITO_2.ordinal()] = Conversion.bit1(counts[DSI_HI], 10);

        //LUZ TRANSITO 3
        result[Index.LUZES_TRANSITO_3.ordinal()] = Conversion.bit1(counts[DSI_HI], 11);

        //PB
        CV = new double[]{1.576325E-02, 3.366936E00 };
        result[Index.PB.ordinal()] = EV.polyval(CV, counts[PB]);

        //ZPI
        CV = new double[]{1.576325E-02, 3.366936E00 };
        result[Index.ZPI.ordinal()] = Conversion.RoundedBy10(Conversion.pa2zp(counts[PB], CV));

        //QB
        CV = new double[]{1.104728E-11, -1.245681E-07, 1.616881E-02, -5.190770E00 };
        result[Index.QB.ordinal()] = EV.polyval(CV, counts[QB]);

        //VI
        CV = new double[]{1.104728E-11, -1.245681E-07, 1.616881E-02, -5.190770E00 };
        result[Index.VI.ordinal()] = Conversion.qc2vc(counts[QB], CV);



        //POLEO
        CV = new double[]{1.106099E-14, -1.039409E-09, 4.609634E-03, -8.489569E-01};
        result[Index.POLEO.ordinal()] = EV.polyval(CV,counts[POLEO]);

        //TI
        CV = new double[]{1.122463E-14 , -1.572081E-09, 1.587530E-03};
        result[Index.TI.ordinal()] = EV.polyval(CV,counts[TI]);

        //TOLEO
        CV = new double[]{1.046599E-07, 1.182232E-02, -2.805004E02};
        result[Index.TOLEO.ordinal()] = EV.polyval(CV,counts[TOLEO]);

        //T5
        CV = new double[]{-6.923510E-11, 8.088771E-06, -2.921231E-01, 3.305075E03};
        result[Index.T5.ordinal()] = EV.polyval(CV,counts[T5]);

        //PCOMB
        CV = new double[]{-2.316451E-11, 2.360662E-04, -5.752361E-01};
        result[Index.PCOMB.ordinal()] = EV.polyval(CV,counts[PCOMB]);

        //FF
        CV = new double[]{-1.097021943531706e-002, 3.128981991511512e-001, -3.459836133476601e+000, 2.368354486742195e+001, 1.190697602056968e+001};
        result[Index.FF.ordinal()] = EV.polyval(CV, result[Index.PCOMB.ordinal()]);

        //DETOT
        result[Index.DETOT.ordinal()] = CalculeDETOT(result[Index.FF.ordinal()]);

        //BOOSTER
        result[Index.BOOSTER.ordinal()] = Conversion.bit1(counts[DSI_LO], 13);

        this._currentCVT = result;
        return this._currentCVT;
    }



}
