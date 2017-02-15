package com.example.nascimento.testeaware;

import android.content.Context;
import android.content.Intent;

import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by nascimento on 14/02/17.
 */

public class MyBroadcastReceiver extends ContextBroadcastReceiver {
    @Override
    public boolean doConditionWhenBroadcastIsReceived(Context contextReceived, Intent intentReceived) {
        FenceState fenceState = FenceState.extract(intentReceived);

        return fenceState.getCurrentState() == FenceState.TRUE;
    }

    @Override
    protected ContextBroadcastAction doDefineContextBroadcastAction() {
        return new MyAction();
    }
}
