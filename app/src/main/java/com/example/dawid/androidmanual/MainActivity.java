package com.example.dawid.androidmanual;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.appmanago.exception.NotFoundRequiredPropertiesException;
import com.appmanago.lib.AmExtras;
import com.appmanago.lib.AmMonitor;
import com.appmanago.lib.AmMonitoring;
import com.appmanago.lib.AmProperties;
import com.appmanago.lib.gcm.AmRegistrationIntentService;
import com.appmanago.lib.helper.PreferencesGateway;
import com.appmanago.model.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MainActivity extends AppCompatActivity {

    private AmMonitoring amMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            amMonitor = AmMonitor.initLibrary(getApplicationContext());
        } catch (NotFoundRequiredPropertiesException e) {
            e.printStackTrace();
        }

        if (checkPlayServices()) {
            Intent intent = new Intent(this, AmRegistrationIntentService.class);
            startService(intent);
        }

        handleAppmanagoExtras(getIntent().getExtras());

        amMonitor.syncEmail("dupa321@o2.pl");

    }

    @Override
    protected void onResume() {
        super.onResume();
        amMonitor.eventStarted("mainActivity", new AmProperties());
    }

    @Override
    protected void onPause() {
        super.onPause();
        amMonitor.eventEnded("mainActivity", new AmProperties());
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i(Constants.LOG_TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    private void handleAppmanagoExtras(Bundle b) {
        AmExtras amExtras = amMonitor.getAmExtras(b);
        if(amExtras.getPushType() != null && amExtras.getPushType().length() > 0){
            Toast.makeText(getApplicationContext(), "TYP:" + amExtras.getPushType() + " :" + amExtras.getPayload(), Toast.LENGTH_LONG).show();
        }
    }

}
