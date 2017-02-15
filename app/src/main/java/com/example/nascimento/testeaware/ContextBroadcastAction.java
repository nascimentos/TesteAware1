package com.example.nascimento.testeaware;

import android.content.Context;
import android.content.Intent;

/**
 * Created by nascimento on 13/02/17.
 */

public abstract class ContextBroadcastAction {
    public abstract void doActionWhenBroadcastIsReceived(Context contextReceived, Intent intentReceived);
}
