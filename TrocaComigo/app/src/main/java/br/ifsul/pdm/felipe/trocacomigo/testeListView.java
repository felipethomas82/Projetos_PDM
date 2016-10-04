package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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


public class testeListView extends ActionBarActivity {

    UserControl userControl;
    int idProduto;

    private ArrayList<Comentario> comments;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teste_list_view);

        context = this;

        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        if (params != null)
        {
            try {
                userControl = new UserControl(params);
            } catch (UserControl.MissingUserParamsException e) {
                e.printStackTrace();
            }

            idProduto = params.getInt("idProduto");

        }
        else
        {
            idProduto = 18;
            userControl = new UserControl(16, "b2f05bc10c77c0a3399a2be9410c92a0");
        }

        //chamado do metodo que preenche os textViews com os detalhes do produto
        takeProductData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_detalhes_produto, menu);
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


    public void takeProductData() {

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        File cacheDir = new File(this.getCacheDir(), "teste");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        // clear all volley caches.
        queue.add(new ClearCacheRequest(cache, null));

        //RequestQueue queue = Volley.newRequestQueue(this);

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_PRODUCTS + "/" + idProduto;

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

                            if (jsonObject.getBoolean("error") == false) {

                                URIWebservice uri = new URIWebservice(context);
                                comments = new ArrayList<Comentario>();
                                JSONArray comentarios = jsonObject.getJSONObject("products").getJSONArray("comentarios");
                                for (int i=0; i<comentarios.length(); i++){
                                    JSONObject c = comentarios.getJSONObject(i);
                                    String pathFotoUser = uri.getHost() + c.getString("pathFoto");
                                    String mensagem = c.getString("comentario");
                                    Comentario comment = new Comentario(pathFotoUser, mensagem, "teste");

                                    //adiciona o comentario (objeto) a array list comments
                                    comments.add(comment);
                                }

                                ArrayAdapter<Comentario> commentAdapter = new CommentAdapter(context, R.layout.comentario, comments);
                                ListView lvComentarios = (ListView) findViewById(R.id.lvComentarios);

                                lvComentarios.setAdapter(commentAdapter);

                            } else {
                                //Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao consultar o produto: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } //fim if
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            //Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao consultar o produto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },//fim listerner json
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(atvDetalhesProduto.this, "Sistema fora do Ar. " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            //sobrescrevendo o header
            @Override
            public HashMap<String, String> getParams(){
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", userControl.getApiKey());
                params.put("User_id", String.valueOf(userControl.getUserId()));

                return params;
            }

            @Override
            public HashMap<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", userControl.getApiKey());
                params.put("User_id", String.valueOf(userControl.getUserId()));

                return params;
            }
        };

        jsonRequest.setTag("tag");
        queue.add(jsonRequest);
    }
}
