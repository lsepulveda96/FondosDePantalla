package com.lsepulveda.fondos_de_pantalla.Util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class NetworkChangeListener extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Common.isConnectedToInternet(context)) {  //Sin internet
            Toast.makeText(context, "No hay internet", Toast.LENGTH_LONG).show();
        }
    }
}