package br.ifsul.pdm.felipe.trocacomigo;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by PC on 23/07/2015.
 */
public class FetchAddressIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    static String TAG = "TAG-FETCH-ADDRESS";

    public FetchAddressIntentService(String name) {
        super(name);
    }

    public FetchAddressIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        //passando o Locale como parametro, o resultado irá retornar no formato correto (lingua e numeros)
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String mensagemErro = "";

        mReceiver = intent.getParcelableExtra(Constantes.RECEIVER);

        //pega o local passado via extras
        Location location = intent.getParcelableExtra(Constantes.LOCATION_DATA_EXTRA);

        List<Address> enderecos = null;

        try{
            //o último parametro é referente aos resultados, neste caso pega somente um endereco
            enderecos = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
        } catch (IOException e) {
            mensagemErro = "Serviço de codificação de endereços não disponível.";
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mensagemErro = "Latitude ou longitude incorretas";

            Log.e("Erro", mensagemErro + ". " +
                    "Latitude = " + location.getLatitude() +
                    ", Longitude = " +
                    location.getLongitude(), e);
        }


        //verifica se retornou algum resultado
        if (enderecos == null || enderecos.size() == 0){
            if (mensagemErro.isEmpty()){
                mensagemErro = "Não foi possível encontrar o endereço que corresponda a estas coordenadas do GPS";
            }
            deliverResultToReceiver(Constantes.FAILURE_RESULT, mensagemErro);
        } else {
            Address endereco = enderecos.get(0);
            ArrayList<String> partesDoEndereco = new ArrayList<String>();

            // Busca as linhas do endereco com getAddressLine,
            // junta tudoe envia para a thread.
            for(int i = 0; i < endereco.getMaxAddressLineIndex(); i++) {
                partesDoEndereco.add(endereco.getAddressLine(i));
            }
           // Log.i("Endereco", "Endereco encontrado");
           // Log.i("Endereco", partesDoEndereco.toString());
            deliverResultToReceiver(Constantes.SUCCESS_RESULT,
                   TextUtils.join(System.getProperty("line.separator"), partesDoEndereco));
        }

    }

    private void deliverResultToReceiver(int codigoResultado, String mensagem) {
        Bundle extras = new Bundle();
        extras.putString(Constantes.RESULT_DATA_KEY, mensagem);
        mReceiver.send(codigoResultado, extras);
    }

}
