package br.ifsul.pdm.felipe.trocacomigo;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by PC on 23/07/2015.
 */
public class FetchCoordinatesIntentService extends IntentService {

    protected ResultReceiver mReceiver;

    static String TAG = "TAG-FETCH-COORDINATES";

    public FetchCoordinatesIntentService(String name) {
        super(name);
    }

    public FetchCoordinatesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i("Script", "Iniciou a busca das coords");
        //passando o Locale como parametro, o resultado irá retornar no formato correto (lingua e numeros)
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        String mensagemErro = "";

        mReceiver = intent.getParcelableExtra(Constantes.RECEIVER);

        //pega o local passado via extras
        String endereco = intent.getStringExtra(Constantes.GET_COORDINATES);
        Log.i("Script", "TESTE ENDERECO " + endereco);

        List<Address> enderecos = null;

        try{
            //o último parametro é referente aos resultados, neste caso pega somente um endereco
            enderecos = geocoder.getFromLocationName(endereco, 1);
        } catch (IOException e) {
            mensagemErro = "Serviço de codificação de endereços não disponível.";
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            mensagemErro = "Latitude ou longitude incorretas";

            Log.e("Erro", mensagemErro, e);
        }


        //verifica se retornou algum resultado
        if (enderecos == null || enderecos.size() == 0){
            if (mensagemErro.isEmpty()){
                mensagemErro = "Não foi possível encontrar as coordenadas do endereço";
            }
            deliverResultToReceiver(Constantes.FAILURE_RESULT, mensagemErro);
        } else {
            Address enderecoEncontrado = enderecos.get(0);

            double lat = enderecoEncontrado.getLatitude();
            double lng = enderecoEncontrado.getLongitude();

            //cria uma string para devolver as coordenadas ao receiver no formato lat;long
            String coordenadas = "";
            coordenadas = String.valueOf(lat) + ";" + String.valueOf(lng);

            deliverResultToReceiver(Constantes.SUCCESS_RESULT, coordenadas);
        }

    }

    private void deliverResultToReceiver(int codigoResultado, String mensagem) {
        Bundle extras = new Bundle();
        extras.putString(Constantes.RESULT_DATA_KEY, mensagem);
        mReceiver.send(codigoResultado, extras);
    }

}
