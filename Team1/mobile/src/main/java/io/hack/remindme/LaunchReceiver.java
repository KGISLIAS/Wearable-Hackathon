package io.hack.remindme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by iQube_2 on 10/2/2015.
 */

public class LaunchReceiver extends BroadcastReceiver {

    public static final String ACTION_PULSE_SERVER_ALARM =
            "io.hack.remindme.ACTION_PULSE_SERVER_ALARM";

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent serviceIntent = new Intent(
                context,
                PollService.class);

        context.startService(serviceIntent);

    }
}