package br.cta.ipev.t25;

import static br.cta.ipev.t25.Conversion.TwosComplementH135;
import static br.cta.ipev.t25.Conversion.extrairArincH135;
import static br.cta.ipev.t25.Conversion.mergeWordsLong;

import android.util.Log;

import br.cta.isad.iCounts2UE;
import br.cta.isad.EV;

public class CoefsSAD1 extends CoefsSAD implements iCounts2UE {

    public static final int TOP_HI = 57 - OFFSET_IENA;
    public static final int TOP_LO = 58 - OFFSET_IENA;



    public static final int TCG102C_0_J3_HI_TI = 41 - OFFSET_IENA;
    public static final int TCG102C_0_J3_LO_TI = 48 - OFFSET_IENA;
    public static final int TCG102C_0_J3_MI_TI=  49 - OFFSET_IENA;


    public static final int RPM_HI = 30 - OFFSET_IENA;
    public static final int RPM_LO = 31 - OFFSET_IENA;

    public static final int M_GASES = 20 - OFFSET_IENA;
    public static final int M_MISTURA = 21 - OFFSET_IENA;
    public static final int M_PASSO = 22 - OFFSET_IENA;
    public static final int P_ADMIN = 24 - OFFSET_IENA;
    public static final int DSI_LO = 11 - OFFSET_IENA;
    public static final int DSI_HI = 9 - OFFSET_IENA;

    public static final int EGT = 13 - OFFSET_IENA;
    public static final int OAT = 23  - OFFSET_IENA;
    public static final int FLAPE = 14 - OFFSET_IENA;
    public static final int FLAPE_SYNCHRO = 15 - OFFSET_IENA;


    @Override
    public double[] convert(char[] counts) {
        double[] result = new double[Index.values().length];
        double[] CV;

        //TEMPO
        result[Index.TEMPO.ordinal()] = EV.sadtime2secs(0xffff & counts[TCG102C_0_J3_HI_TI], 0xffff & counts[TCG102C_0_J3_LO_TI], 0xffff & counts[TCG102C_0_J3_MI_TI]);

        // TOP
        result[Index.TOP.ordinal()] = counts[TOP_LO];

        //RPM
        CV = new double[]{1,0};
        result[Index.RPM.ordinal()] = EV.polyval(CV,counts[RPM_LO]);

        //EGT
        CV = new double[]{1,0};
        result[Index.EGT.ordinal()] = EV.polyval(CV,counts[EGT]);

        //FLAPE
        CV = new double[]{1,0};
        result[Index.FLAPE_SYNCHRO.ordinal()] = EV.polyval(CV,counts[FLAPE_SYNCHRO]);

        //OAT
        CV = new double[]{1,0};
        result[Index.OAT.ordinal()] = EV.polyval(CV,counts[OAT]);

        //P_ADMISSAO
        CV = new double[]{1,0};
        result[Index.P_ADMIN.ordinal()] = EV.polyval(CV,counts[P_ADMIN]);

        //MANETE_GASES
        CV = new double[]{1,0};
        result[Index.MANETE_GASES.ordinal()] = EV.polyval(CV,counts[M_GASES]);

        //MANETE_MISTURA
        CV = new double[]{1,0};
        result[Index.MANETE_MISTURA.ordinal()] = EV.polyval(CV,counts[M_MISTURA]);

        //MANETE_PASSO
        CV = new double[]{1,0};
        result[Index.MANETE_PASSO.ordinal()] = EV.polyval(CV,counts[M_PASSO]);

        //CAPOTA
        result[Index.CAPOTA.ordinal()] = Conversion.bit1(counts[DSI_LO], 12);


        //LUZ CIMA 1
        result[Index.LUZES_CIMA_1.ordinal()] = Conversion.bit1(counts[DSI_HI], 6);

        //LUZ CIMA 2
        result[Index.LUZES_CIMA_2.ordinal()] = Conversion.bit1(counts[DSI_HI], 7);

        //LUZ CIMA 3
        result[Index.LUZES_CIMA_3.ordinal()] = Conversion.bit1(counts[DSI_HI], 8);

        //LUZ TRANSITO 1
        result[Index.LUZES_TRANSITO_1.ordinal()] = Conversion.bit1(counts[DSI_HI], 9);

        //LUZ TRANSITO 2
        result[Index.LUZES_TRANSITO_2.ordinal()] = Conversion.bit1(counts[DSI_HI], 10);

        //LUZ TRANSITO 3
        result[Index.LUZES_TRANSITO_3.ordinal()] = Conversion.bit1(counts[DSI_HI], 11);


        this._currentCVT = result;
        return this._currentCVT;
    }



}
