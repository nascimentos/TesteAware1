package com.example.nascimento.testeaware;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.FenceUpdateRequest;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.DetectedActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public abstract class ContextRegisterService extends Service implements GoogleApiClient.ConnectionCallbacks {

    private int contextVerifier;
    private DataSnapshot dataSnapshot;
    private GoogleApiClient thisGoogleApiClient;
    private Intent intentReceived;
    private ContextBroadcastReceiver myBroadcastReceiver;
    public String awarenessAPIKey;
    private final String UNIQUE_INSTANCE_CODE = new SimpleDateFormat("MMddyyyyHHmmss").format(new Date());
    private final String LINK_INTENT_BROADCAST_RECEIVER = UNIQUE_INSTANCE_CODE + "l";
    private final String FENCE_NAME = UNIQUE_INSTANCE_CODE + "f";
    public static final String CONTEXT_VERIFIED_KEY = "com.example.nascimento.testeaware.contextVerifiedKey";

    @Override
    public void onCreate() {
        awarenessAPIKey = doDefineAwarenessAPIKey();
        myBroadcastReceiver = doDefineBroadcastReceiver();

        if (myBroadcastReceiver != null) {
            registerReceiver(myBroadcastReceiver, new IntentFilter(LINK_INTENT_BROADCAST_RECEIVER));
        }

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doCreateNewContext();

        if (intent.hasExtra(awarenessAPIKey)) {
            intentReceived = intent;
            thisGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Awareness.API)
                    .addConnectionCallbacks(this).build();
            thisGoogleApiClient.connect();
        }

        return START_STICKY;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (intentReceived.getExtras().get(awarenessAPIKey) instanceof DataSnapshot) {
            registerAwarenessSnapshotAPI();
        } else if (intentReceived.getExtras().get(awarenessAPIKey) instanceof AwarenessFence) {
            registerAwarenessFenceAPI();
        }
    }

    protected abstract String doDefineAwarenessAPIKey();

    protected abstract ContextBroadcastReceiver doDefineBroadcastReceiver();

    protected abstract void doCreateNewContext();

    private void registerAwarenessSnapshotAPI() {
        try {
            dataSnapshot = (DataSnapshot) intentReceived.getExtras().get(awarenessAPIKey);

            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    verifyDateContext();
                    verifyLocationContext();
                    verifyActivityContext();
                    verifyHeadphoneStateContext();

                    Log.i("TESTE", "Number of contexts: " + dataSnapshot.getNumberOfInitializedContexts());
                    Log.i("TESTE", "cont: " + contextVerifier);

                    if ((dataSnapshot.getCondition().equals(DataSnapshot.OR)
                            && contextVerifier > 0)
                            ^ (dataSnapshot.getCondition().equals(DataSnapshot.AND)
                            && dataSnapshot.getNumberOfInitializedContexts() == contextVerifier)) {
                        Log.i("TESTE", "broadcast sent");
                        sendBroadcast();
                        this.cancel();
                    }

                    contextVerifier = 0;
                }
            }, 0, dataSnapshot.getContextVerificationFrequencyInMilliseconds());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void verifyDateContext() {
        if (dataSnapshot.getStartDate() != null
                && dataSnapshot.getStopDate() != null
                && Calendar.getInstance().getTime().after(dataSnapshot.getStartDate())
                && Calendar.getInstance().getTime().before(dataSnapshot.getStopDate())) {
            Log.i("TESTE", Calendar.getInstance().getTime().toString());
            contextVerifier++;
        }
    }

    private void verifyLocationContext() {
        try {
            Awareness.SnapshotApi.getLocation(thisGoogleApiClient)
                    .setResultCallback(new ResultCallback<LocationResult>() {
                        @Override
                        public void onResult(@NonNull LocationResult locationResult) {
                            Location locationReceived = dataSnapshot.getLocation();
                            Double radius = dataSnapshot.getRadius();

                            if (locationResult != null && locationResult.getLocation() != null) {
                                Log.i("TESTE", "Result: " + locationResult.getLocation().toString());
                            }

                            Log.i("TESTE", "Received: " + locationReceived.toString());
                            Log.i("TESTE", "Radius: " + radius);
//                            Log.i("TESTE", "Distance: " + locationResult.getLocation().distanceTo(locationReceived));

                            if (locationReceived != null && radius != null
                                    && locationResult.getLocation()
                                    .distanceTo(locationReceived) < radius) {
                                Log.i("TESTE", "Entrou");
                                contextVerifier++;
                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void verifyActivityContext() {
        Awareness.SnapshotApi.getDetectedActivity(thisGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        DetectedActivity detectedActivity = detectedActivityResult
                                .getActivityRecognitionResult().getMostProbableActivity();
                        Integer activityReceived = dataSnapshot.getActivity();

                        if (activityReceived != null
                                && detectedActivity.getType() == activityReceived
                                && detectedActivity.getConfidence() > 50) {
                            contextVerifier++;
                        }
                    }
                });
    }

    private void verifyHeadphoneStateContext() {
        Awareness.SnapshotApi.getHeadphoneState(thisGoogleApiClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        Integer headphoneStateReceived = dataSnapshot.getHeadphoneState();

                        if (headphoneStateReceived != null
                                && headphoneStateResult.getHeadphoneState().getState() == headphoneStateReceived) {
                            contextVerifier++;
                        }
                    }
                });
    }

    private void registerAwarenessFenceAPI() {
        try {
            AwarenessFence awarenessFence = (AwarenessFence) intentReceived.getExtras()
                    .get(awarenessAPIKey);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                    new Intent(LINK_INTENT_BROADCAST_RECEIVER), 0);

            Awareness.FenceApi.updateFences(thisGoogleApiClient, new FenceUpdateRequest.Builder()
                    .addFence(FENCE_NAME, awarenessFence, pendingIntent).build()).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    Log.i("TESTE", "Registrou fence");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendBroadcast() {
        Intent broadcastIntent = new Intent(LINK_INTENT_BROADCAST_RECEIVER);
        broadcastIntent.putExtra(CONTEXT_VERIFIED_KEY, true);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        try {
            if (myBroadcastReceiver != null) {
                unregisterReceiver(myBroadcastReceiver);
            }

            Awareness.FenceApi.updateFences(thisGoogleApiClient, new FenceUpdateRequest.Builder()
                    .removeFence(FENCE_NAME).build());
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}
