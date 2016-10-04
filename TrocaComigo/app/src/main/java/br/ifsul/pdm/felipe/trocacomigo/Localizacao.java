package br.ifsul.pdm.felipe.trocacomigo;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;

/**
 * Created by PC on 01/09/2015.
 */
public class Localizacao implements LocationListener {

    private Activity activity;
    private atvListaProdutos objAtvListaProdutos;
    private Location locationAntiga;
    private LocationManager locationManager;

    private int contador = 0;
    //limite de requisicoes que serao feitas aos GPS ou wifi para buscar a localizacao
    private int LIMITE_REQUISICOES = 2;

    Localizacao (Activity activity, atvListaProdutos atvLista){
        this.activity = activity;
        this.objAtvListaProdutos = atvLista;
    }

    @Override
    public void onLocationChanged(Location location) {
        //o listener fica rodando mesmo que a activity seja pausada ou destruida.
        //Deve-se parar o listener quando encontrar a coordenada mais precisa

        if (isBetterLocation(location, locationAntiga)) {
            //quando chegar no limite deve-se parar com o listener
            contador++;

            locationAntiga = location;

            //grava as coordenadas na Singleton com os dados do usuario
            UserControlV2.getInstance().latitude = location.getLatitude();
            UserControlV2.getInstance().longitude = location.getLongitude();
        }


        if (contador == LIMITE_REQUISICOES)
            locationManager.removeUpdates(this);

        objAtvListaProdutos.achouLocalizacao();
        Log.i("Script", "ACHOU COORDENADAS: LAT: " + UserControlV2.getInstance().latitude + " LONG: " + UserControlV2.getInstance().longitude);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {
        //Log.i("Script", "PROVIDER: " + s);
        if (s.equals("gps"))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3, 0, this);

        if (s.equals("network"))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3, 0, this);
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    //inicia o Listener para buscar a localizacao do usuario
    public void iniciaGeoLocalizacao(){

        //pega a referencia do location manager
        locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        // Verifica se o GPS está ativo
        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        // Caso não esteja ativo abre um novo dialog com as configurações para
        // realizar o ativamento
        if (!enabled) {
            solicitaGPS();
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
        } catch (IllegalArgumentException e){
            //faz nada
        }

    } //fim metodo inicia geoLocalizacao


    //GPS desabilitado. Abre alerta e pede ao usuário para habiltar
    private void solicitaGPS(){

        /* O codigo abaixo ativa o GPS, sem a autorização do usuário, nos devices mais antigos
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings",
                    "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);
            */


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        //caso o usuario clicar em sim, abre nova activity com as settings para habilitar o gps
        alertDialogBuilder.setMessage("Oops. O GPS está desabilitado. Para o bom funcionamento deste app deve-se ser ligado o GPS.").setCancelable(false);

        alertDialogBuilder.setPositiveButton("Sim",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        activity.startActivity(callGPSSettingIntent);
                    }
                });

        alertDialogBuilder.setNegativeButton("Não",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }



    //http://developer.android.com/guide/topics/location/strategies.html

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
