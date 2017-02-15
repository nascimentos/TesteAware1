package com.example.nascimento.testeaware;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.AwarenessFence;
import com.google.android.gms.awareness.fence.DetectedActivityFence;
import com.google.android.gms.awareness.fence.HeadphoneFence;
import com.google.android.gms.awareness.fence.LocationFence;
import com.google.android.gms.awareness.fence.TimeFence;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ConfigurationActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, Serializable {

    public static final String EDIT_ACTIVITY = "Edit Activity";
    private boolean isOldRecipeEdition;
    private TextView tvName;
    private TextView tvDate;
    private TextView tvHour;
    private TextView tvLocation;
    private TextView tvActivity;
    private TextView tvHeadphone;
    private Button btnChangeName;
    private Button btnChangeDate;
    private Button btnChangeHour;
    private Button btnChangeLocation;
    private Button btnChangeActivity;
    private Button btnChangeHeadphone;
    private Button btnResetDate;
    private Button btnResetHour;
    private Button btnResetLocation;
    private Button btnResetActivity;
    private Button btnResetHeadphone;
    private String fileName;
    private String[] activityChoices = new String[]{"IN_VEHICLE", "ON_BICYCLE", "ON_FOOT", "WALKING", "RUNNING", "TILTING", "STILL", "UNKNOWN"};
    private String[] headphoneChoices = new String[]{"PLUGGED_IN", "UNPLUGGED"};
    private List<String> fileContent;
    private Intent intentFence;
    private AwarenessFence allFences;
    private Place place;
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
            tvDate.setText(" " + ++selectedMonth + "/" + selectedDay + "/" + selectedYear);
        }
    };
    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            tvHour.setText(" " + selectedHour + "h" + selectedMinute + "min");
        }
    };
    private static final int NAME_ID = 0;
    private static final int DATE_ID = 1;
    private static final int HOUR_ID = 2;
    private static final int ACTIVITY_ID = 3;
    private static final int HEADPHONE_ID = 4;
    private static final int PLACE_PICKER_REQUEST = 6;
    private static final int MY_PERMISSION_REQUEST = 123;
    private final String DEFAULT_FILES_DIRECTORY = Environment.getExternalStorageDirectory() + "/TesteAware/";
    private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        ActionBar actionBar = getSupportActionBar();
        String extraMessageFromPriorActivity = getIntent().getStringExtra(ListRecipesActivity.EXTRA_MESSAGE);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this).build();
        googleApiClient.connect();

        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        isOldRecipeEdition = !extraMessageFromPriorActivity.equals(ListRecipesActivity.EXTRA_MESSAGE_NEW_RECIPE);

        if (isOldRecipeEdition) {
            processInitialFile(extraMessageFromPriorActivity);
        }

        initializeTextViews();
        addListenerOnButtons();
        verifyExternalStoragePermissions(this);
    }

/*    @Override
    public void onStart() {
        if (googleApiClient.hasConnectedApi(Places.GEO_DATA_API)) {
            Log.e(EDIT_ACTIVITY, "GEO_DATA_API conectada");
        }

        if (googleApiClient.hasConnectedApi(Places.PLACE_DETECTION_API)) {
            Log.e(EDIT_ACTIVITY, "PLACE_DETECTION_API conectada");
        }

        if (googleApiClient.isConnected()) {
            Log.e(EDIT_ACTIVITY, "GoogleClient conectado");
        }

        try {
            retrieveLocation(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnected(Bundle connectionHint) {
/*        if (googleApiClient.isConnected()
                && googleApiClient.hasConnectedApi(Places.GEO_DATA_API)
                && googleApiClient.hasConnectedApi(Places.PLACE_DETECTION_API)) {
            try {
                retrieveLocation(null);
            } catch (Exception e) {
            }
        }*/
    }

    GoogleApiClient googleApiClient;

    private void retrieveLocation(String placeId) throws Exception {
        try {
//            placeId = "ChIJd8BlQ2BZwokRAFUEcm_qrcA";
//                placeId = placeId.trim();
            PendingResult<PlaceBuffer> result = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
//                Places.GeoDataApi.getPlaceById(googleApiClient, placeId)
            ResultCallback<PlaceBuffer> r = new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                        place = places.get(0);
                    }

                    places.release();
                }
            };

            result.setResultCallback(r);
        } catch (Exception e) {
            throw new Exception();
        }
    }

    private void processInitialFile(String fileName) {
        try {
            fileContent = new ArrayList<>();
            String line;
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(DEFAULT_FILES_DIRECTORY + fileName)));

            do {
                line = bufferedReader.readLine();
                fileContent.add(line);
            } while (line != null);
        } catch (Exception e) {
            e.printStackTrace();
            fileContent = null;
        }
    }

    private void initializeTextViews() {
        getTextViews();

        if (isOldRecipeEdition && fileContent != null) {
            tvName.setText(fileContent.get(0));
            tvDate.setText(fileContent.get(1));
            tvHour.setText(fileContent.get(2));

            if (!settedLocationInformation() || fileContent.get(3).contains("--")) {
                tvLocation.setText(" --");
            }

            tvActivity.setText(fileContent.get(4));
            tvHeadphone.setText(fileContent.get(5));
        } else {
            tvName.setText(" --");
            tvDate.setText(" --");
            tvHour.setText(" --");
            tvLocation.setText(" --");
            tvActivity.setText(" --");
            tvHeadphone.setText(" --");
        }
    }

    private void getTextViews() {
        tvName = (TextView) findViewById(R.id.tvName);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvHour = (TextView) findViewById(R.id.tvHour);
        tvLocation = (TextView) findViewById(R.id.tvLocation);
        tvActivity = (TextView) findViewById(R.id.tvActivity);
        tvHeadphone = (TextView) findViewById(R.id.tvHeadphone);
    }

    private boolean settedLocationInformation() {
        try {
            retrieveLocation(fileContent.get(3));
            tvLocation.setText(" (" + place.getId() + ") " + place.getAddress());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    ResultCallback<PlaceBuffer> r = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {

        }
    };

    private void addListenerOnButtons() {
        getButtonsChange();
        getButtonsReset();
        addListenerOnButtonsChange();
        addListenerOnButtonsReset();
    }

    private void getButtonsChange() {
        btnChangeName = (Button) findViewById(R.id.btnChangeName);
        btnChangeDate = (Button) findViewById(R.id.btnChangeDate);
        btnChangeHour = (Button) findViewById(R.id.btnChangeHour);
        btnChangeLocation = (Button) findViewById(R.id.btnChangeLocation);
        btnChangeActivity = (Button) findViewById(R.id.btnChangeActivity);
        btnChangeHeadphone = (Button) findViewById(R.id.btnChangeHeadphone);
    }

    private void getButtonsReset() {
        btnResetDate = (Button) findViewById(R.id.btnResetDate);
        btnResetHour = (Button) findViewById(R.id.btnResetHour);
        btnResetLocation = (Button) findViewById(R.id.btnResetLocation);
        btnResetActivity = (Button) findViewById(R.id.btnResetActivity);
        btnResetHeadphone = (Button) findViewById(R.id.btnResetHeadphone);
    }

    private void addListenerOnButtonsChange() {
        addListenerOnButtonChangeName();
        addListenerOnButtonChangeDate();
        addListenerOnButtonChangeHour();
        addListenerOnButtonChangeLocation();
        addListenerOnButtonChangeActivity();
        addListenerOnButtonChangeHeadphone();
    }

    private void addListenerOnButtonChangeName() {
        btnChangeName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(NAME_ID);
            }
        });
    }

    private void addListenerOnButtonChangeDate() {
        btnChangeDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_ID);
            }
        });
    }

    private void addListenerOnButtonChangeHour() {
        btnChangeHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(HOUR_ID);
            }
        });
    }

    private void addListenerOnButtonChangeLocation() {
        btnChangeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                    startActivityForResult(builder.build(ConfigurationActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void addListenerOnButtonChangeActivity() {
        btnChangeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(ACTIVITY_ID);
            }
        });
    }

    private void addListenerOnButtonChangeHeadphone() {
        btnChangeHeadphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(HEADPHONE_ID);
            }
        });
    }

    private void addListenerOnButtonsReset() {
        addListenerOnButtonResetDate();
        addListenerOnButtonResetHour();
        addListenerOnButtonResetLocation();
        addListenerOnButtonResetActivity();
        addListenerOnButtonResetHeadphone();
    }

    private void addListenerOnButtonResetDate() {
        btnResetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvDate.setText(" --");
            }
        });
    }

    private void addListenerOnButtonResetHour() {
        btnResetHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvHour.setText(" --");
            }
        });
    }

    private void addListenerOnButtonResetLocation() {
        btnResetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvLocation.setText(" --");
            }
        });
    }

    private void addListenerOnButtonResetActivity() {
        btnResetActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvActivity.setText(" --");
            }
        });
    }

    private void addListenerOnButtonResetHeadphone() {
        btnResetHeadphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvHeadphone.setText(" --");
            }
        });
    }

    private static void verifyExternalStoragePermissions(Activity activity) {
        int accessPermissionFiles = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int accessPermissionLocation = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        if (accessPermissionFiles != PackageManager.PERMISSION_GRANTED
                || accessPermissionLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS, MY_PERMISSION_REQUEST);
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {

        switch (id) {
            case NAME_ID:
                return createNameDialog();
            case DATE_ID:
                return createDateDialog();
            case HOUR_ID:
                return createTimeDialog();
            case ACTIVITY_ID:
                return createActivityDialog();
            case HEADPHONE_ID:
                return createHeadphoneDialog();
            default:
                return null;
        }
    }

    private Dialog createNameDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View viewDialogName = LayoutInflater.from(this).inflate(R.layout.dialog_name, null);
            final EditText inputName = (EditText) viewDialogName.findViewById(R.id.inputName);

            if (isOldRecipeEdition) {
                inputName.setText(fileContent.get(0));
            }

            builder.setView(viewDialogName).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tvName.setText(" " + inputName.getText().toString().trim());
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            return builder.create();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can not show dialog", Toast.LENGTH_LONG).show();

            return null;
        }
    }

    private Dialog createDateDialog() {
        try {
            Calendar c = Calendar.getInstance();
            int year;
            int month;
            int day;

            if (isOldRecipeEdition && !fileContent.get(1).contains("--")) {
                String[] data = fileContent.get(1).trim().split("\\D");

                month = new Integer(data[0]) - 1;
                day = new Integer(data[1]);
                year = new Integer(data[2]);
            } else {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);
            }

            return new DatePickerDialog(this, datePickerListener, year, month, day);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can not show dialog", Toast.LENGTH_LONG).show();

            return null;
        }
    }

    private Dialog createTimeDialog() {
        try {
            Calendar c = Calendar.getInstance();
            int hour;
            int minute;

            if (isOldRecipeEdition && !fileContent.get(2).contains("--")) {
                String[] data = fileContent.get(2).trim().split("\\D");

                hour = new Integer(data[0]);
                minute = new Integer(data[1]);
            } else {
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);
            }

            return new TimePickerDialog(this, timePickerListener, hour, minute, false);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can not show dialog", Toast.LENGTH_LONG).show();

            return null;
        }
    }

    private Dialog createActivityDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setItems(activityChoices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tvActivity.setText(" " + activityChoices[which]);
                }
            });

            return builder.create();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can not show dialog", Toast.LENGTH_LONG).show();

            return null;
        }
    }

    private Dialog createHeadphoneDialog() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setItems(headphoneChoices, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    tvHeadphone.setText(" " + headphoneChoices[which]);

                }
            });

            return builder.create();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Can not show dialog", Toast.LENGTH_LONG).show();

            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Place place = PlacePicker.getPlace(this, data);
            tvLocation.setText(" (" + place.getId() + ") " + place.getAddress());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_title_bar, menu);

        if (!isOldRecipeEdition) {
            MenuItem item = menu.findItem(R.id.action_delete);
            item.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();

                return true;
            case R.id.action_save:
                return isRecipeSaved();
            case R.id.action_delete:
                return isRecipeDeleted();
            default:
                Toast.makeText(ConfigurationActivity.this, "Error", Toast.LENGTH_LONG).show();

                return super.onOptionsItemSelected(item);
        }
    }

    private boolean isRecipeSaved() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String newFileName = tvName.getText().toString().replaceAll("\\s", "") + ".txt";

                if (isOldRecipeEdition) {
                    String oldFileName = fileContent.get(0).replaceAll("\\s", "") + ".txt";

                    if (!oldFileName.equals(newFileName) && !isNameValid(newFileName)) {
                        Toast.makeText(ConfigurationActivity.this, "Invalid recipe name", Toast.LENGTH_LONG).show();

                        return true;
                    }

                    isFileDeleted(oldFileName);
                }

                if (!isNameValid(newFileName)) {
                    Toast.makeText(ConfigurationActivity.this, "Invalid recipe name", Toast.LENGTH_LONG).show();

                    return true;
                }

                String[] lines = compileUserSettings();

                if (wroteToFile(newFileName, lines)) {
                    fileName = newFileName;
                    Toast.makeText(ConfigurationActivity.this, "Recipe saved", Toast.LENGTH_LONG).show();
                    createFence();
                    returnToHome();
                } else {
                    Toast.makeText(ConfigurationActivity.this, "Could not save recipe", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(ConfigurationActivity.this, "External storage is not mounted. Impossible " +
                        "to save.", Toast.LENGTH_LONG).show();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ConfigurationActivity.this, "Error", Toast.LENGTH_LONG).show();

            return false;
        }
    }

    private boolean isRecipeDeleted() {
        try {
            if (isFileDeleted(tvName.getText().toString().replaceAll("\\s", "") + ".txt")) {
                Toast.makeText(ConfigurationActivity.this, "Recipe deleted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ConfigurationActivity.this, "Recipe does not exist", Toast.LENGTH_LONG).show();
            }

            returnToHome();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(ConfigurationActivity.this, "Error", Toast.LENGTH_LONG).show();

            return false;
        }
    }

    private boolean isNameValid(String fileName) {
        if (fileName.substring(0, fileName.length() - 4).isEmpty()) {
            return false;
        }

        for (File f : new File(DEFAULT_FILES_DIRECTORY).listFiles()) {
            if (fileName.equals(f.getName())) {
                return false;
            }
        }

        return true;
    }

    private boolean isFileDeleted(String fileName) {
        if (new File(DEFAULT_FILES_DIRECTORY + fileName).delete()) {
            deleteFence(fileName.substring(0, fileName.length() - 4));

            return true;
        } else {
            return false;
        }
    }

    private String[] compileUserSettings() {
        String[] result = new String[7];
        String location = tvLocation.getText().toString();

        result[0] = tvName.getText().toString();
        result[1] = tvDate.getText().toString();
        result[2] = tvHour.getText().toString();
        result[3] = tvLocation.getText().toString();

        if (!result[3].contains("--")) {
            result[3] = location.substring(2, location.indexOf(") "));
        }

        result[4] = tvActivity.getText().toString();
        result[5] = tvHeadphone.getText().toString();

        return result;
    }

    private boolean wroteToFile(String fileName, String[] content) {
        try {
            File defaultDirectory = new File(DEFAULT_FILES_DIRECTORY);

            if (!defaultDirectory.exists()) {
                defaultDirectory.mkdir();
            }

            File file = new File(defaultDirectory, fileName);

            if (!file.exists()) {
                file.createNewFile();
            }

            PrintWriter printWriter = new PrintWriter(new FileOutputStream(file));

            for (String line : content) {
                printWriter.println(line);
            }

            printWriter.close();

            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }

    private void returnToHome() {
        startActivity(new Intent(ConfigurationActivity.this, ListRecipesActivity.class));
    }

    public static final String AWARENESS_KEY = "configuration";

    private void createFence() {
        try {
            intentFence = new Intent(this, MyService.class);

            intentFence.putExtra(AWARENESS_KEY, compileFenceData2());
            //intentFence.putExtra(AWARENESS_KEY, compileSnapshotData());
            startService(intentFence);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DataSnapshot compileSnapshotData() {
        DataSnapshot dataSnapshot = new DataSnapshot();

        Location location = new Location("");
        location.setLatitude(-1.446906);
        location.setLongitude(-48.482215);

        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.set(Calendar.MINUTE, c1.get(Calendar.MINUTE) + 1);
        c2.set(Calendar.YEAR, c2.get(Calendar.YEAR) + 1);
//        Log.i("TESTE", "Data start: " + c1.getTime());
//        Log.i("TESTE", "Data stop: " + c2.getTime());

//        dataSnapshot.setDateInterval(c1.getTime(), c2.getTime());
        dataSnapshot.setLocationAndRadius(location, 16.);
//        dataSnapshot.setActivity(DetectedActivity.ON_FOOT);
//        dataSnapshot.setHeadphoneState(HeadphoneState.UNPLUGGED);
//        dataSnapshot.setCondition(DataSnapshot.AND);
        dataSnapshot.setContextVerificationFrequencyInMilliseconds(5000);

        return dataSnapshot;
    }

    private AwarenessFence compileFenceData2() throws SecurityException {
        List<AwarenessFence> list = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 1);
//        Log.i("TESTE", "Data start: " + c.getTime());

//        list.add(TimeFence.inInterval(c.getTimeInMillis(), Long.MAX_VALUE));
//        list.add(LocationFence.in(-1.446906, -48.482215, 20, 0));
//        list.add(DetectedActivityFence.during(DetectedActivityFence.ON_FOOT));
        list.add(HeadphoneFence.during(HeadphoneState.UNPLUGGED));

        return AwarenessFence.and(list);
    }

    private AwarenessFence compileFenceData() {
        List<AwarenessFence> transientList = new ArrayList<>();
        List<AwarenessFence> principalList = new ArrayList<>();

        transientList.add(createTimeAndDateFence());
        transientList.add(createLocationFence());
        transientList.add(createActivityFence());
        transientList.add(createHeadphoneFence());

        for (AwarenessFence f : transientList) {
            if (f != null) {
                principalList.add(f);
            }
        }

        if (principalList.isEmpty()) {
            return null;
        } else {
            return AwarenessFence.and(principalList);
        }
    }

    private AwarenessFence createTimeAndDateFence() {
        try {
            String toParse = tvDate.getText().toString() + " " + tvHour.getText().toString();
            Date d = new SimpleDateFormat("MM/dd/yyyy HH'h'mm'min'").parse(toParse);
            long actualTimeMilliseconds = d.getTime();

            return TimeFence.inInterval(actualTimeMilliseconds, Long.MAX_VALUE);
        } catch (Exception e) {
            return null;
        }
    }

    private AwarenessFence createLocationFence() {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                verifyExternalStoragePermissions(this);
            } else {
                String placeId = tvLocation.getText().toString();

                if (!placeId.contains("--")) {
                    placeId = tvLocation.getText().toString().substring(2,
                            tvLocation.getText().toString().indexOf(") "));
                    retrieveLocation(placeId);
                    LatLng queriedLocation = place.getLatLng();
                    double latitude = queriedLocation.latitude;
                    double longitude = queriedLocation.longitude;
                    double radius = 4; //in meters
                    long dwellTimeMillis = 1000;

                    return LocationFence.in(latitude, longitude, radius, dwellTimeMillis);
                }
            }
        } catch (Exception e) {
        }

        return null;
    }

    private AwarenessFence createActivityFence() {
        String text = tvActivity.getText().toString();

        if (text.contains("--")) {
            return null;
        } else if (text.contains("VEHICLE")) {
            return DetectedActivityFence.during(DetectedActivityFence.IN_VEHICLE);
        } else if (text.contains("BICYCLE")) {
            return DetectedActivityFence.during(DetectedActivityFence.ON_BICYCLE);
        } else if (text.contains("FOOT")) {
            return DetectedActivityFence.during(DetectedActivityFence.ON_FOOT);
        } else if (text.contains("WALKING")) {
            return DetectedActivityFence.during(DetectedActivityFence.WALKING);
        } else if (text.contains("RUNNING")) {
            return DetectedActivityFence.during(DetectedActivityFence.RUNNING);
        } else if (text.contains("TILTING")) {
            return DetectedActivityFence.during(DetectedActivityFence.TILTING);
        } else if (text.contains("STILL")) {
            return DetectedActivityFence.during(DetectedActivityFence.STILL);
        } else {
            return DetectedActivityFence.during(DetectedActivityFence.UNKNOWN);
        }
    }

    private AwarenessFence createHeadphoneFence() {
        String text = tvHeadphone.getText().toString();

        if (text.contains("--")) {
            return null;
        } else if (text.contains("IN")) {
            return HeadphoneFence.during(HeadphoneState.PLUGGED_IN);
        } else {
            return HeadphoneFence.during(HeadphoneState.UNPLUGGED);
        }
    }

    private void deleteFence(final String fenceKey) {
        if (intentFence != null) {
            stopService(intentFence);
        } else {
            Log.e(EDIT_ACTIVITY, "Fence unregistration could not be reached.");
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(EDIT_ACTIVITY, "onConnectionFailed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
        Toast.makeText(this, "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }
}
