package com.sei.geographicstory;

import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.sei.geographicstory.location.LocationMonitor;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ERROR = 0;

    //User Interface Objects
    private TextView mdisplayLocation;
    //private Button mplayRecordButton;
    //private Spinner mselectWordType;

    private ArrayList<String> partsOfSpeech = new ArrayList<String>(
                                                      Arrays.asList("unlabeled", "noun","pronoun","verb", "adjective",
                                                                    "adverb", "preposition", "conjunction", "interjection"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mdisplayLocation = (TextView) findViewById(R.id.display_coordinates);
        //mplayRecordButton = (Button) findViewById(R.id.play_record_button);
        //mselectWordType = (Spinner) findViewById(R.id.wordType_spinner);

        ActionCoordinator.getInstance().initializeActionCoordinator(getApplicationContext(), this);

        /**mplayRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });**/


        /**ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, partsOfSpeech);
        mselectWordType.setAdapter(adapter);
        mselectWordType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, partsOfSpeech.get(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });**/
    }

    @Override
    public void onStart(){
        super.onStart();
        ActionCoordinator.getInstance().startMonitoring();

    }

    @Override
    public void onStop(){
        super.onStop();
        ActionCoordinator.getInstance().stopMonitoring();
    }

    @Override
    protected void onResume(){
        super.onResume();

        int errorCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if(errorCode != ConnectionResult.SUCCESS){
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(errorCode, this, REQUEST_ERROR,
                    new  DialogInterface.OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialog){
                            finish();
                        }
                    });
            errorDialog.show();
        }
    }


    public void setDisplayLocation(Location l){
        String displayText = Double.toString(l.getLatitude()) + Double.toString(l.getLongitude());
        mdisplayLocation.setText(displayText);
    }


}
