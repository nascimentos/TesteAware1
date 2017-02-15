package com.example.nascimento.testeaware;

/**
 * Created by nascimento on 17/01/17.
 */

public class MissedCallService extends ContextRegisterService {
    private static boolean ring = false;
    private static boolean callReceived = false;

    @Override
    protected String doDefineAwarenessAPIKey() {
        return "Teste";
    }

    @Override
    protected ContextBroadcastReceiver doDefineBroadcastReceiver() {
        return new MissedCallBroadcastReceiver();
    }

    @Override
    protected void doCreateNewContext() {
/*        TelephonyManager myTelephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        myTelephonyManager.listen(new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (ring == true && callReceived == false) {
                            ring = false;
                            callReceived = false;
                            //Código obrigatório quando a condição do contexto for alcançada
                            MissedCallService.super.sendBroadcast();
                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        callReceived = true;
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        ring = true;
                        break;
                }
            }
        }, PhoneStateListener.LISTEN_CALL_STATE);*/
    }
}