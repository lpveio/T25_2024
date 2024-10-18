package br.cta.ipev.t25;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public void setAlerts(Context context) {

    }


}
