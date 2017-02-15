package com.example.nascimento.testeaware;

/**
 * Created by nascimento on 14/02/17.
 */

public class MyService extends ContextRegisterService {
    @Override
    protected String doDefineAwarenessAPIKey() {
        return ConfigurationActivity.AWARENESS_KEY;
    }

    @Override
    protected ContextBroadcastReceiver doDefineBroadcastReceiver() {
        return new MyBroadcastReceiver();
    }

    @Override
    protected void doCreateNewContext() {
    }
}
