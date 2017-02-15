package com.example.nascimento.testeaware;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ListRecipesActivity extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "com.example.nascimento.testeaware.MESSAGE";
    public final static String EXTRA_MESSAGE_NEW_RECIPE = "com.example.nascimento.testeaware.NEW_RECIPE";
    private final String DEFAULT_FILES_DIRECTORY = Environment.getExternalStorageDirectory() + "/TesteAware/";
    List<String> filesNameList = new ArrayList<>();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_files);
        verifyExternalStoragePermissions(this);
    }

    private void verifyExternalStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        } else {
            createList();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createList();
        } else {
            Toast.makeText(this, "TesteAware app needs access to your files", Toast.LENGTH_LONG).show();
        }
    }

    private void createList() {
        createFilesList();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.text_view_files, filesNameList);
        ListView listView = (ListView) findViewById(R.id.listItem);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListRecipesActivity.this, ConfigurationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, filesNameList.get(position));
                startActivity(intent);
            }
        });
    }

    private void createFilesList() {
        try {
            File defaultDirectory = new File(DEFAULT_FILES_DIRECTORY);

            if (defaultDirectory.exists() && defaultDirectory.listFiles() != null) {
                for (File f : defaultDirectory.listFiles()) {
                    filesNameList.add(f.getName());
                }
            } else {
                Toast.makeText(this, "There are no recipes", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_title_bar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new:
                Intent intent = new Intent(ListRecipesActivity.this, ConfigurationActivity.class);
                intent.putExtra(EXTRA_MESSAGE, EXTRA_MESSAGE_NEW_RECIPE);
                startActivity(intent);

                return true;
            default:
                Toast.makeText(ListRecipesActivity.this, "Error", Toast.LENGTH_LONG).show();

                return super.onOptionsItemSelected(item);
        }
    }
}
