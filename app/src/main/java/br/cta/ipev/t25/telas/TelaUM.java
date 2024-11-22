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

import java.text.DecimalFormat;

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
    private boolean isRunningTIME = false;
    private boolean isFlashing = false;
    public static final String PREF_FILE_NAME = "T25_DETOT";
    public double detot;
    private static final String DETOT_KEY = "detot";
    private static final String BASE_TIME_KEY = "base_time_key";
    private SharedPreferences sharedPreferences;
    public AppManager manager;
    DecimalFormat formatador = new DecimalFormat("0");

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



    @Override
    public void update(double[] CVT) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    //binding.txtTempo.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
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
                    binding.txtDETOT.setValue(CVT[Index.DETOT_SEG.ordinal()]);
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
        } else if (trem_1 == 1.0 && trem_2 == 1.0 && trem_3 == 1.0 && transito_1 == 1.0 && transito_2 == 1.0 && transito_3 == 1.0){
           return "RECOLHIDO";
        } else {
            return "EM TRÂNSITO";
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
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(DETOT_KEY, String.valueOf(detot)); // Armazena como String
        editor.commit();
        hideKeyboard();
        new CoefsSAD1(this, detot);

    }


    private double loadSharedDETOT(){
        return Double.parseDouble(sharedPreferences.getString(DETOT_KEY, "0"));
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

    private void checkFlashing() {
        long elapsedMillis = SystemClock.elapsedRealtime() - binding.chronoTempoVoo.getBase();
        long seconds = elapsedMillis / 1000;

        // Verifica os intervalos para o flash em vermelho
        if (shouldFlash(seconds)) {
            startFlashing();
        }
    }

    private boolean shouldFlash(long seconds) {
        // Retorna verdadeiro para tempos exatos de alerta
        return seconds == 1770 || seconds == 3570 || seconds == 4470 || seconds == 5370;
    }

    private void startFlashing() {
        if (isFlashing) return;

        isFlashing = true;
        final long endTime = SystemClock.elapsedRealtime() + 60000; // Piscar por 1 minuto

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (SystemClock.elapsedRealtime() < endTime) {
                    int color = binding.chronoTempoVoo.getCurrentTextColor() == Color.RED ? Color.BLACK : Color.RED;
                    binding.chronoTempoVoo.setTextColor(color);
                    handler.postDelayed(this, 200);
                } else {
                    binding.chronoTempoVoo.setTextColor(Color.WHITE);
                    isFlashing = false;
                }
            }
        });
    }

    private void init(){
        manager =  ((AppManager)getApplicationContext());
        manager.addDisplay(this);
        sharedPreferences = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);

        binding.buttonSTART.setOnClickListener(v -> {

            if (!isRunning) {

                startTime = SystemClock.elapsedRealtime() - pauseOffset;
                handler.postDelayed(updateChronometer, 0);
                isRunning = true;
            }
        });

        binding.buttonSTARTTIME.setOnClickListener(v -> {

            if (!isRunningTIME) {
                binding.chronoTempoVoo.setBase(SystemClock.elapsedRealtime());
                binding.chronoTempoVoo.start();
                binding.buttonSTARTTIME.setEnabled(false); // Desativa o botão ao iniciar
                isRunningTIME = true;
                binding.chronoTempoVoo.setOnChronometerTickListener(c -> {
                    checkFlashing();
                });
            }
        });

        binding.buttonZEROTIME.setOnClickListener(v -> {
            resetChronometer();
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


        binding.insereDETOT.setText(formatador.format(loadSharedDETOT()));

        binding.buttonDETOT.setOnClickListener(v -> {

            detot = binding.insereDETOT.getText().toString().isEmpty() ? 212 : Double.parseDouble(binding.insereDETOT.getText().toString());
            saveDETOT();

        });

    }

    private void resetChronometer() {
        binding.chronoTempoVoo.stop();
        isRunningTIME = false;
        binding.buttonSTARTTIME.setEnabled(true); // Reativa o botão para reiniciar o cronômetro
        binding.chronoTempoVoo.setBase(SystemClock.elapsedRealtime());
    }


}
