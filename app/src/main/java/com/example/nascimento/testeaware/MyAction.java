package com.example.nascimento.testeaware;

import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by nascimento on 14/02/17.
 */

public class MyAction extends ContextBroadcastAction {
    @Override
    public void doActionWhenBroadcastIsReceived(Context contextReceived, Intent intentReceived) {

        Log.i("TESTE", "Conditions reached");
        Vibrator vibrator = (Vibrator) contextReceived.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
//        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
//        toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 4000);
        contextReceived.stopService(new Intent(contextReceived, ContextRegisterService.class));
    }
}
