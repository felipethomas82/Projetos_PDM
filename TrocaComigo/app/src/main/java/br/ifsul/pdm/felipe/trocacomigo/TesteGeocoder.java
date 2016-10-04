package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


public class TesteGeocoder extends ActionBarActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

    TextView tvGeocoder;

    GoogleApiClient mGoogleApiClient;

    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;

    private boolean mAddressRequested;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_geocoder);
        mResultReceiver = new AddressResultReceiver(new Handler());

        tvGeocoder = (TextView) findViewById(R.id.tvTesteGeocoder);
        //mGoogleApiClient =
//        mGoogleApiClient.connect();

        mAddressRequested = false;

        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teste_geocoder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected  void startIntentService(){
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(Constantes.RECEIVER, mResultReceiver);
        intent.putExtra(Constantes.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    public void fetchAddressButtonHandler(View view) {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
        //updateUIWidgets();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Gets the best and most recent location currently available,
        // which may be null in rare cases when a location is not available.
       // mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
              //  mGoogleApiClient);

        mLastLocation = new Location("");
        mLastLocation.setLatitude(-29.9645676d);
        mLastLocation.setLongitude(-51.7274363d);
        //LatLng saoJeo = new LatLng(-29.9645676,-51.7274363);

        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(this, "Geocoder nao habilitado",
                        Toast.LENGTH_LONG).show();
                return;
            }

            if (mAddressRequested) {
                startIntentService();
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    class AddressResultReceiver extends ResultReceiver {

        public String mAddressOutput;
        //Context context;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            // Display the address string
            // or an error message sent from the intent service.
            mAddressOutput = resultData.getString(Constantes.RESULT_DATA_KEY);
            //displayAddressOutput();

            // Show a toast message if an address was found.
            if (resultCode == Constantes.SUCCESS_RESULT) {
                Toast.makeText(TesteGeocoder.this, mAddressOutput, Toast.LENGTH_LONG).show();
            }

        }
    }
}
