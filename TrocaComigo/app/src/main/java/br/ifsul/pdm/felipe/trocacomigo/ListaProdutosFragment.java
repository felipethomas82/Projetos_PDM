package br.ifsul.pdm.felipe.trocacomigo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * Created by felipe on 04/11/15.
 */
public class ListaProdutosFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private String queryFiltroAnuncios;


    private AppCompatActivity activity;

    public ListaProdutosFragment(){
        super();
    }

    public ListaProdutosFragment(AppCompatActivity activity, String filtroProdutos){
        super();
        this.activity = activity;
        this.queryFiltroAnuncios = filtroProdutos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
    Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_lista_produtos, container, false);

        carregaAnuncios(v);

        return v;
    }

    public void carregaAnuncios(final View v) {

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        File cacheDir = new File(activity.getCacheDir(), "teste");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        // clear all volley caches.
        queue.add(new ClearCacheRequest(cache, null));

        //RequestQueue queue = Volley.newRequestQueue(this);

        URIWebservice uri = new URIWebservice(activity);
        String url = "";
        String distancia = "20";
        if (queryFiltroAnuncios.equals("")) {

            url = uri.getHost() + uri.URI_PRODUCTS +
                    "/" + UserControlV2.latitude +
                    "/" + UserControlV2.longitude +
                    "/" + distancia; //o ultimo parametro é a distancia

        } else {
            url = uri.getHost() + uri.URI_PRODUCT_TITLE_SUGGESTIONS +
                    "/" + queryFiltroAnuncios +
                    "/" + UserControlV2.latitude +
                    "/" + UserControlV2.longitude +
                    "/" + distancia; //o ultimo parametro é a distancia;
        }

        Log.i("Script", url);
        //faz o request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            //Log.i("Script", URIWebservice.URI_REGISTER_USER);
                            //Log.i("Script", "SUCCES: " + jsonObject.get("message").toString());

                            URIWebservice uri = new URIWebservice(activity);

                            if (!jsonObject.getBoolean("error")) {

                                ArrayList<Anuncio> anuncios = new ArrayList<Anuncio>();
                                JSONArray jsonArray = jsonObject.getJSONArray("products");

                                for (int i=0; i<jsonArray.length(); i++){

                                    JSONObject mAnuncio = jsonArray.getJSONObject(i);

                                    Anuncio a = new Anuncio();
                                    a.id = mAnuncio.getInt("id");
                                    a.titulo = mAnuncio.getString("titulo");
                                    a.valor = mAnuncio.getDouble("preco");
                                    a.pathFotoCapa = uri.getHost() + mAnuncio.getString("capa");
                                    a.tipoAuncio = mAnuncio.getString("tipoAnuncio");
                                    a.dataAnuncio = mAnuncio.getString("dataCadastro");
                                    a.nomeUsuario = mAnuncio.getString("nomeUsuario");
                                    a.distancia = mAnuncio.getInt("distancia");
                                    a.fotoUsuario = uri.getHost() + mAnuncio.getString("fotoUsuario");

                                    //a.userControl = userControl;

                                    anuncios.add(a);
                                }

                                mRecyclerView = (RecyclerView) v.findViewById(R.id.mRecycleViewListaProdutos);

                                // config para manter performance. info extraida da net
                                mRecyclerView.setHasFixedSize(true);

                                // deve-se usar um linear layout manager
                                mLayoutManager = new LinearLayoutManager(activity);
                                mRecyclerView.setLayoutManager(mLayoutManager);

                                // especifica o adaptador
                                mAdapter = new anuncioAdapter(anuncios, activity);
                                mRecyclerView.setAdapter(mAdapter);

                            } else {
                                Toast.makeText(activity, "Ocorreu um erro ao carregar os anúncios: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } //fim if
                        catch (JSONException e)
                        {
                            mRecyclerView = (RecyclerView) v.findViewById(R.id.mRecycleViewListaProdutos);
                            mRecyclerView.setBackgroundResource(R.drawable.sem_produtos);

                            // deve-se usar um linear layout manager
                            mLayoutManager = new LinearLayoutManager(activity);
                            mRecyclerView.setLayoutManager(mLayoutManager);

                            // especifica o adaptador
                            //ArrayList<Anuncio> anuncios = null;
                            //mAdapter = new anuncioAdapter(anuncios, activity);
                            //mRecyclerView.setAdapter(mAdapter);

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
