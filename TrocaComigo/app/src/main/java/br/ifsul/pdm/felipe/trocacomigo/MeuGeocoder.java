package br.ifsul.pdm.felipe.trocacomigo;

import android.app.Activity;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;


public class MeuGeocoder implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{


    GoogleApiClient mGoogleApiClient;

    protected Location mLastLocation;
    protected String endereco;
    private AddressResultReceiver mResultReceiver;
    private CoordinatesResultReceiver mCoordinatesResultReceiver;

    private boolean mAddressRequested;
    private boolean mCoordinatesRequested;

    //necessario para startar o service
    private Activity mActivity;

    //construtor
    public MeuGeocoder(Activity activity){

        mResultReceiver = new AddressResultReceiver(new Handler());

        mAddressRequested = false;
        mCoordinatesRequested = false;

        mActivity = activity;

        //seta a localização para buscar o endereco. busca da classe UserControl
        if (UserControlV2.getInstance().latitude != 0 && UserControlV2.getInstance().longitude != 0) {
            mLastLocation = new Location("");
            mLastLocation.setLatitude(UserControlV2.getInstance().latitude);
            mLastLocation.setLongitude(UserControlV2.getInstance().longitude);
        }

        buildGoogleApiClient();
    }

    //construtor
    public MeuGeocoder(Activity activity, String endereco){

        mCoordinatesResultReceiver = new CoordinatesResultReceiver(new Handler());

        mAddressRequested = false;
        mCoordinatesRequested = false;

        mActivity = activity;

        this.endereco = endereco;

        buildGoogleApiClient();
    }

    /**
     * Builds a GoogleApiClient. Uses {@code #addApi} to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
    }

    protected  void startIntentService(){
        Intent intent = new Intent(mActivity, FetchAddressIntentService.class);
        intent.putExtra(Constantes.RECEIVER, mResultReceiver);
        intent.putExtra(Constantes.LOCATION_DATA_EXTRA, mLastLocation);
        mActivity.startService(intent);
    }

    protected void startIntentService(String address){
        Intent intent = new Intent(mActivity, FetchCoordinatesIntentService.class);
        intent.putExtra(Constantes.RECEIVER, mCoordinatesResultReceiver);
        intent.putExtra(Constantes.GET_COORDINATES, address);
        mActivity.startService(intent);
    }

    public void buscaEndereco() {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient.isConnected() && mLastLocation != null) {
            startIntentService();
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address.
        mAddressRequested = true;
        //updateUIWidgets();
    }

    public void buscaCoordenadas() {

        if (mGoogleApiClient.isConnected()) {
            startIntentService(endereco);
        }
        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address.
        mCoordinatesRequested = true;
        //updateUIWidgets();
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Gets the best and most recent location currently available,
        // which may be null in rare cases when a location is not available.
       // mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        //LatLng saoJeo = new LatLng(-29.9645676,-51.7274363);

        if (mLastLocation != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(mActivity, "Geocoder nao habilitado",
                        Toast.LENGTH_LONG).show();
                return;
            }

            //variavel setada quando o metodo buscaEndereco é chamado
            if (mAddressRequested) {
                startIntentService();
            }

        }

        if (endereco != null) {
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(mActivity, "Geocoder nao habilitado",
                        Toast.LENGTH_LONG).show();
                return;
            }

            //variavel setada quando o metodo buscaEndereco é chamado
            if (mCoordinatesRequested) {
                startIntentService(endereco);
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

            // Salva o endereco encontrado na classe singleton do usuario
            if (resultCode == Constantes.SUCCESS_RESULT) {
                //Toast.makeText(MeuGeocoder.this, mAddressOutput, Toast.LENGTH_LONG).show();
                Log.i("Script", "ACHOU ENDERECO: " + mAddressOutput);
                UserControlV2.getInstance().endereco = mAddressOutput;
            }

        }
    } //fim AdressResultReceiver


    class CoordinatesResultReceiver extends ResultReceiver {

        public String coordendas;

        public CoordinatesResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            coordendas = resultData.getString(Constantes.RESULT_DATA_KEY);

            // Salva o endereco encontrado na classe singleton do usuario
            if (resultCode == Constantes.SUCCESS_RESULT) {
                //Toast.makeText(MeuGeocoder.this, mAddressOutput, Toast.LENGTH_LONG).show();
                Log.i("Script", "ACHOU ENDERECO: " + coordendas);
                //UserControlV2.getInstance().endereco = mAddressOutput;

            }
        }
    } //fim CoordinatesResultReceiver
}
