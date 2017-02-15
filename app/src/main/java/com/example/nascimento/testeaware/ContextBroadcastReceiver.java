package com.example.nascimento.testeaware;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by nascimento on 01/02/17.
 */

public abstract class ContextBroadcastReceiver extends BroadcastReceiver {

    private ContextBroadcastAction contextBroadcastAction;
    private boolean isContextSuccessfullyVerified;

    public boolean isContextSuccessfullyVerified() {
        return isContextSuccessfullyVerified;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        contextBroadcastAction = doDefineContextBroadcastAction();
        isContextSuccessfullyVerified = intent.getBooleanExtra(ContextRegisterService.CONTEXT_VERIFIED_KEY, false);

        if (doConditionWhenBroadcastIsReceived(context, intent)) {
            contextBroadcastAction.doActionWhenBroadcastIsReceived(context, intent);
        }
    }

    public abstract boolean doConditionWhenBroadcastIsReceived(Context contextReceived, Intent intentReceived);

    protected abstract ContextBroadcastAction doDefineContextBroadcastAction();
}
