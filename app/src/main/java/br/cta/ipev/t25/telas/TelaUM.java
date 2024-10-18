package br.cta.ipev.t25.telas;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import br.cta.ipev.t25.Alerta;
import br.cta.ipev.t25.AppManager;
import br.cta.ipev.t25.Index;
import br.cta.ipev.t25.R;

import br.cta.ipev.t25.databinding.ActivityTelaUmBinding;
import br.cta.isad.Display;
import br.cta.misc.Convertions;

public class TelaUM extends AppCompatActivity implements Display {

    private ActivityTelaUmBinding binding;
    int mode = 0;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setLayout();
        init();
        TesteCores();
    }


    private void TesteCores() {

        binding.txtCapota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode == 0) {
                    binding.txtCapota.setBackgroundColor(getResources().getColor(R.color.bgValorParametroVermelho));
                    binding.txtCapota.setStringValue("ABERTA");
                    binding.txtCapota.setTextColor(getResources().getColor(R.color.white));
                    mode = 1;
                } else {
                    binding.txtCapota.setBackgroundColor(getResources().getColor(R.color.bgValorParametro));
                    binding.txtCapota.setStringValue("FECHADA");
                    binding.txtCapota.setTextColor(getResources().getColor(R.color.bgNomeParametro));
                    mode = 0;
                }
            }
        });

        binding.luz1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mode == 0) {
                    binding.luz1.setImageResource(R.drawable.button_on);
                    mode = 1;

                } else {
                    binding.luz1.setImageResource(R.drawable.button_gray);
                    mode = 0;
                }

            }
        });

        binding.luz2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mode == 0) {
                    binding.luz2.setImageResource(R.drawable.button_on);
                    mode = 1;

                } else {
                    binding.luz2.setImageResource(R.drawable.button_gray);
                    mode = 0;
                }

            }
        });

        binding.luz3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mode == 0) {
                    binding.luz3.setImageResource(R.drawable.button_on);
                    mode = 1;

                } else {
                    binding.luz3.setImageResource(R.drawable.button_gray);
                    mode = 0;
                }

            }
        });

        binding.luz4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mode == 0) {
                    binding.luz4.setImageResource(R.drawable.button_off);
                    mode = 1;

                } else {
                    binding.luz4.setImageResource(R.drawable.button_gray);
                    mode = 0;
                }

            }
        });

        binding.luz5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (mode == 0) {
                    binding.luz5.setImageResource(R.drawable.button_off);
                    mode = 1;

                } else {
                    binding.luz5.setImageResource(R.drawable.button_gray);
                    mode = 0;
                }

            }
        });

        binding.luz6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mode == 0) {
                    binding.luz6.setImageResource(R.drawable.button_off);
                    mode = 1;

                } else {
                    binding.luz6.setImageResource(R.drawable.button_gray);
                    mode = 0;
                }

            }
        });

    }

    @Override
    public void update(double[] CVT) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    binding.txtTempo.setText(Convertions.sec2dhms(CVT[Index.TEMPO.ordinal()]));
                    binding.txtTOP.setValue(CVT[Index.TOP.ordinal()]);
                    binding.txtRPM.setValue(CVT[Index.RPM.ordinal()]);
                    binding.txtEGT.setValue(CVT[Index.EGT.ordinal()]);
                    binding.txtFLAPE.setValue(CVT[Index.FLAPE.ordinal()]);
                    binding.txtOAT.setValue(CVT[Index.OAT.ordinal()]);
                    binding.txtPadm.setValue(CVT[Index.P_ADMIN.ordinal()]);
                    binding.txtGases.setValue(CVT[Index.MANETE_GASES.ordinal()]);
                    binding.txtMistura.setValue(CVT[Index.MANETE_MISTURA.ordinal()]);
                    binding.txtPasso.setValue(CVT[Index.MANETE_PASSO.ordinal()]);
                    binding.txtCapota.setStringValue(CVT[Index.CAPOTA.ordinal()] == 0 ? "FECHADA" : "ABERTA");


                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void setLayout(){
        binding = ActivityTelaUmBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        getSupportActionBar().hide();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    private void init(){
        AppManager manager =  ((AppManager)getApplicationContext());
        manager.addDisplay(this);




    }

}
