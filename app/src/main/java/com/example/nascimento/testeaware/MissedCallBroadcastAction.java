package com.example.nascimento.testeaware;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by nascimento on 13/02/17.
 */

public class MissedCallBroadcastAction extends ContextBroadcastAction {
    @Override
    public void doActionWhenBroadcastIsReceived(Context contextReceived, Intent intentReceived) {
        Toast.makeText(contextReceived, "Your conditions were reached.", Toast.LENGTH_LONG).show();
        Log.i("TESTE", "Conditions reached");
        ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, ToneGenerator.MAX_VOLUME);
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 4000);
        contextReceived.stopService(new Intent(contextReceived, ContextRegisterService.class));
    }
}
