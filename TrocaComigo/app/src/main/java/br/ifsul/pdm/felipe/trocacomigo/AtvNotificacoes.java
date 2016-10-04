package br.ifsul.pdm.felipe.trocacomigo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AtvNotificacoes extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_notificacoes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        carregaAlertas();
    }

    AppCompatActivity activity;

    public void carregaAlertas() {

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        File cacheDir = new File(this.getCacheDir(), "teste");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        //amarra a activity para passar pra dentro do objeto jsonRquest
        this.activity = this;

        // clear all volley caches.
        queue.add(new ClearCacheRequest(cache, null));

        //RequestQueue queue = Volley.newRequestQueue(this);

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_NOTIFICATIONS + "/" + UserControlV2.getInstance().userId;

        //faz o request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            URIWebservice uri = new URIWebservice(activity);

                            if (!jsonObject.getBoolean("error")) {

                                ArrayList<Alerta> alertas = new ArrayList<Alerta>();
                                JSONArray jsonArray = jsonObject.getJSONArray("alertas");

                                for (int i=0; i<jsonArray.length(); i++){

                                    JSONObject mAlerta = jsonArray.getJSONObject(i);

                                    Alerta a = new Alerta();
                                    a.id = mAlerta.getInt("id");
                                    a.tituloAnuncio = mAlerta.getString("tituloAnuncio");
                                    a.dono = mAlerta.getInt("dono");
                                    a.idProduto = mAlerta.getInt("idProduto");
                                    a.tipoAlerta = mAlerta.getString("tipoAlerta");

                                    alertas.add(a);
                                }

                                mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerViewNotificacoes);

                                // config para manter performance. info extraida da net
                                mRecyclerView.setHasFixedSize(true);

                                // deve-se usar um linear layout manager
                                mLayoutManager = new LinearLayoutManager(activity);
                                mRecyclerView.setLayoutManager(mLayoutManager);

                                // especifica o adaptador
                                mAdapter = new alertasAdapter(alertas, activity);
                                mRecyclerView.setAdapter(mAdapter);

                            } else {
                                Toast.makeText(activity, "Ocorreu um erro ao carregar os anúncios: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } //fim if
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(activity, "Ocorreu um erro ao carregar os anúncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },//fim listerner json
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(activity, "Sistema fora do Ar. " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){

            //sobrescrevendo o header
            @Override
            public HashMap<String, String> getParams(){
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", UserControlV2.getInstance().apiKey);
                params.put("User_id", String.valueOf(UserControlV2.getInstance().userId));

                return params;
            }

            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", UserControlV2.getInstance().apiKey);
                params.put("User_id", String.valueOf(UserControlV2.getInstance().userId));

                return params;
            }
        };

        jsonRequest.setTag("tag");
        queue.add(jsonRequest);
    }
}
