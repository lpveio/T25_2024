package br.cta.ipev.t25;

import android.graphics.Color;
import android.util.Log;

public class Alerta {

    public static int setAlertZPI(double valor) {
        int color;

        if (valor >= 4000 && valor <= 6000) {
            color = Color.parseColor("#00bb4c"); // Faixa verde
        } else {
            color = Color.YELLOW; // Faixa amarela
        }
        return color;
    }

    public static int setAlertTCC(double valor) {
        int color;

        if ((valor >= 100 && valor <= 150)  || (valor >= 435 && valor <= 475)) {
            color = Color.YELLOW; // Faixa verde
        } else {
            color = Color.parseColor("#00bb4c"); // Faixa amarela
        }
        return color;
    }

    public static int setAlertTOleo(double valor) {
        int color;

        if (valor >= 50 && valor <= 140) {
            color = Color.YELLOW; // Faixa verde
        } else {
            color = Color.parseColor("#00bb4c"); // Faixa amarela
        }
        return color;
    }

    public static int setAlertPoleo(double valor) {
        int color;

        if ((valor >= 25 && valor <= 60)  || (valor >= 90 && valor <= 100)) {
            color = Color.YELLOW; // Faixa verde
        } else {
            color = Color.parseColor("#00bb4c"); // Faixa amarela
        }
        return color;
    }
}
