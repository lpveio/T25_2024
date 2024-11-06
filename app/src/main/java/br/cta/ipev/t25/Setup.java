package br.cta.ipev.t25;

import android.content.Context;
import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import br.cta.ipev.commom.compat.alerts.RangeColor;
import br.cta.ipev.commom.instruments.odometers.AlertRange;
import br.cta.ipev.commom.screen.BaseSetup;
import br.cta.ipev.commom.screen.Tab;
import br.cta.ipev.t25.telas.TelaUM;

public class Setup extends BaseSetup{

    @Override
    public List<Tab> getScreenTabs(boolean forTablets) {
        List<Tab>screenTabs = new ArrayList<Tab>();
        screenTabs.add(0,new Tab("1b","APLICATIVO PARA H135 (IPEV)", TelaUM.class,true,true));
        return (super.getScreenForTablets(screenTabs,forTablets));

    }


    public static class AlertColors {

        public static final RangeColor alertRed = new RangeColor(100d, 120d, Color.RED);
        public static final RangeColor RxEH = new RangeColor(45d, 100d, Color.YELLOW);
        public static final RangeColor RxEV = new RangeColor(50d, 100d, Color.YELLOW);
        public static final RangeColor RxLa = new RangeColor(-100d, -35d, Color.YELLOW);

    }

    @Override
    public void setAlerts(Context context) {

    }


}
