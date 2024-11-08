package br.cta.ipev.t25.telas;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.cta.ipev.t25.Alerta;
import br.cta.ipev.t25.AppManager;
import br.cta.ipev.t25.CoefsSAD1;
import br.cta.ipev.t25.Index;
import br.cta.ipev.t25.R;


import br.cta.ipev.t25.databinding.ActivityTelaUmBinding;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class TelaUM extends AppCompatActivity implements Display {

    private ActivityTelaUmBinding binding;
    private Handler handler = new Handler();
    private long startTime = 0L;
    private long pauseOffset = 0L;
    private boolean isRunning = false;
    private boolean isFlashing = false;
    public static final String PREF_FILE_NAME = "T25_DETOT";
    public int detot;
    private Runnable blinkRunnable;
    private boolean isBlinking = false;
    public AppManager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        init();

    }

    private Runnable updateChronometer = new Runnable() {
        @Override
        public void run() {
            // Calcula o tempo decorrido
            long elapsedMillis = SystemClock.elapsedRealtime() - startTime;
            int minutes = (int) (elapsedMillis / 60000);
            int seconds = (int) (elapsedMillis % 60000) / 1000;

            // Atualiza o TextView no formato MM:SS:MS
            binding.textViewTimer.setText(String.format("%02d:%02d", minutes, seconds));

            // Checa se está entre 55 e 60 segundos para piscar o texto em vermelho
            if (seconds >= 55) {
                if (!isFlashing) {
                    isFlashing = true;
                    startFlashingCHRONO();
                }
            } else {
                // Para de piscar e reseta a cor quando fora do intervalo
                if (isFlashing) {
                    isFlashing = false;
                    handler.removeCallbacks(flashRunnable);
                    binding.textViewTimer.setTextColor(Color.BLACK); // Volta à cor original
                    binding.textViewTimer.setVisibility(TextView.VISIBLE);
                }
            }

            // Executa novamente a cada 10 ms para atualizar os milissegundos
            handler.postDelayed(this, 10);
        }
    };

    private void startFlashingCHRONO() {
        handler.post(flashRunnable);
    }

    private Runnable flashRunnable = new Runnable() {
        @Override
        public void run() {
            // Alterna a cor e visibilidade do texto para criar o efeito de piscar
            if ( binding.textViewTimer.getVisibility() == TextView.VISIBLE) {
                binding.textViewTimer.setVisibility(TextView.INVISIBLE);
            } else {
                binding.textViewTimer.setVisibility(TextView.VISIBLE);
                binding.textViewTimer.setTextColor(Color.RED); // Define a cor vermelha
            }

            // Configura o próximo "piscar" em 500 ms
            handler.postDelayed(this, 100);
        }
    };


    private void startBlinkingTextView() {
        blinkRunnable = new Runnable() {
            @Override
            public void run() {
                if (binding.txtTempo.getCurrentTextColor() == Color.RED) {
                    binding.txtTempo.setTextColor(Color.BLACK);
                } else {
                    binding.txtTempo.setTextColor(Color.RED);
                }
                handler.postDelayed(this, 500); // Alterna a cada 500ms
            }
        };
        handler.post(blinkRunnable);
        isBlinking = true;
    }

    private void stopBlinkingTextView() {
        if (isBlinking) {
            handler.removeCallbacks(blinkRunnable);
            binding.txtTempo.setTextColor(Color.WHITE); // Restaura a cor original
            isBlinking = false;
        }
    }

    private void checkBlinkingCondition(double tempoEmSegundos) {
        // Converte para minutos e segundos
        int minutos = (int) (tempoEmSegundos / 60);
        int segundos = (int) (tempoEmSegundos % 60);

        // Verifica se está no intervalo de 29m30s a 30m30s ou de 1h14m30s a 1h15m30s
        boolean isInFirstInterval = (minutos == 29 && segundos >= 30) || (minutos == 30 && segundos <= 30);
        boolean isInSecondInterval = (minutos == 74 && segundos >= 30) || (minutos == 75 && segundos <= 30);

        if (isInFirstInterval || isInSecondInterval) {
            if (!isBlinking) {
                startBlinkingTextView();
            }
        } else {
            stopBlinkingTextView();
        }
    }


    @Override
    public void update(double[] CVT) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.txtTempo.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
                    checkBlinkingCondition(CVT[Index.TEMPO.ordinal()]);
                    binding.txtTOP.setValue(CVT[Index.TOP.ordinal()]);
                    binding.txtZPI.setValue(CVT[Index.ZPI.ordinal()]);
                    binding.txtZPI.setBackgroundColor(Alerta.setAlertZPI(CVT[Index.ZPI.ordinal()]));
                    binding.txtVI.setValue(CVT[Index.VI.ordinal()]);
                    binding.txtTREM.setStringValue(tremPosicao(CVT[Index.LUZES_TREM_1.ordinal()], CVT[Index.LUZES_TREM_2.ordinal()], CVT[Index.LUZES_TREM_3.ordinal()], CVT[Index.LUZES_TRANSITO_1.ordinal()], CVT[Index.LUZES_TRANSITO_2.ordinal()], CVT[Index.LUZES_TRANSITO_3.ordinal()]));
                    binding.txtFLAPE.setValue(CVT[Index.FLAPE_SYNCHRO.ordinal()]);
                    binding.txtCapota.setStringValue(CVT[Index.CAPOTA.ordinal()] == 0.0 ? "FECHADA" : "ABERTA");
                    binding.txtCapota.setBackgroundColor(CVT[Index.CAPOTA.ordinal()] == 0 ? getResources().getColor(R.color.bgValorParametro) : getResources().getColor(R.color.bgValorParametroVermelho));
                    binding.txtCapota.setTextColor(CVT[Index.CAPOTA.ordinal()] == 0 ? getResources().getColor(R.color.bgNomeParametro) : getResources().getColor(R.color.white));
                    binding.txtBooster.setStringValue(CVT[Index.BOOSTER.ordinal()] == 0.0 ? "OFF" : "ON");
                    binding.txtEGT.setValue(CVT[Index.EGT.ordinal()]);
                    binding.txtTCC.setValue(CVT[Index.T5.ordinal()]);
                    binding.txtTCC.setBackgroundColor(Alerta.setAlertTCC(CVT[Index.T5.ordinal()]));
                    binding.txtPadm.setValue(CVT[Index.P_ADMIN.ordinal()]);
                    binding.txtRPM.setValue(CVT[Index.RPM.ordinal()]);
                    binding.txtFF.setValue(CVT[Index.FF.ordinal()]);
                    binding.txtDETOT.setValue(CVT[Index.DETOT.ordinal()]);
                    binding.txtDETOT2.setValue(CVT[Index.DETOT_SEG.ordinal()]);
                    binding.txtTA.setValue(CVT[Index.TA.ordinal()]);
                    binding.txtPOLEO.setValue(CVT[Index.POLEO.ordinal()]);
                    binding.txtPOLEO.setBackgroundColor(Alerta.setAlertPoleo(CVT[Index.POLEO.ordinal()]));
                    binding.txtTOLEO.setValue(CVT[Index.TOLEO.ordinal()]);
                    binding.txtTOLEO.setBackgroundColor(Alerta.setAlertTOleo(CVT[Index.TOLEO.ordinal()]));

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private String tremPosicao(double trem_1, double trem_2, double trem_3, double transito_1, double transito_2, double transito_3){

        if (trem_1 == 0.0 && trem_2 == 0.0 && trem_3 == 0.0 && transito_1 == 1.0 && transito_2 == 1.0 && transito_3 == 1.0){
            return "ESTENDIDO";
        } else if (trem_1 == 1.0 && trem_2 == 1.0 && trem_3 == 1.0 && transito_1 == 0.0 && transito_2 == 0.0 && transito_3 == 0.0){
           return "RECOLHIDO";
        } else {
            return "EM TRANSITO";
        }
    }

    private void setLayout(){
        binding = ActivityTelaUmBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void saveDETOT(){
        SharedPreferences preferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("detot", detot); // value to store
        editor.commit();
        hideKeyboard();
        CoefsSAD1 coefsSAD1 = new CoefsSAD1(this);
        coefsSAD1.UpdateDETOT(detot);
    }

    private int loadSharedDETOT(){
        SharedPreferences prefs = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        return prefs.getInt("detot", 0); // default value
    }

    private void hideKeyboard() {
        // Obtém o serviço de input do teclado
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // Esconde o teclado para a view atual com o token do EditText
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.insereDETOT.getWindowToken(), 0);
        }

        // Remove o foco do EditText
        binding.insereDETOT.clearFocus();
    }

    private void init(){
        manager =  ((AppManager)getApplicationContext());
        manager.addDisplay(this);

        binding.buttonSTART.setOnClickListener(v -> {

            if (!isRunning) {

                startTime = SystemClock.elapsedRealtime() - pauseOffset;
                handler.postDelayed(updateChronometer, 0);
                isRunning = true;
            }
        });

        // Para o cronômetro
        binding.buttonSTOP.setOnClickListener(v -> {
            if (isRunning) {
                handler.removeCallbacks(updateChronometer);
                pauseOffset = SystemClock.elapsedRealtime() - startTime;
                isRunning = false;
            }
        });

        // Reseta o cronômetro
        binding.buttonZERO.setOnClickListener(v -> {
            handler.removeCallbacks(updateChronometer);
            handler.removeCallbacks(flashRunnable);
            startTime = SystemClock.elapsedRealtime();
            pauseOffset = 0;
            binding.textViewTimer.setText("00:00");
            binding.textViewTimer.setTextColor(Color.BLACK); // Reseta a cor
            binding.textViewTimer.setVisibility(TextView.VISIBLE);
            isRunning = false;
            isFlashing = false;
        });

        binding.insereDETOT.setText(String.valueOf(loadSharedDETOT()));


        binding.buttonDETOT.setOnClickListener(v -> {

            detot = binding.insereDETOT.getText().toString().isEmpty() ? 0 : Integer.parseInt(binding.insereDETOT.getText().toString());
            saveDETOT();

        });

    }


}
