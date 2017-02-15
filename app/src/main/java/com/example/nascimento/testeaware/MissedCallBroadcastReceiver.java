package com.example.nascimento.testeaware;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by nascimento on 02/02/17.
 */

public class MissedCallBroadcastReceiver extends ContextBroadcastReceiver {

    @Override
    public boolean doConditionWhenBroadcastIsReceived(Context contextReceived, Intent intentReceived) {
        //Encapsula o estado de uma AwarenessFence
        FenceState fenceState = FenceState.extract(intentReceived);

        return fenceState.getCurrentState() == FenceState.TRUE || isContextSuccessfullyVerified();
    }

    @Override
    protected ContextBroadcastAction doDefineContextBroadcastAction() {
        return new MissedCallBroadcastAction();
    }
}
