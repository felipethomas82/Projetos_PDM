package br.ifsul.pdm.felipe.trocacomigo;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class atvPerfilUsuario extends AppCompatActivity {

    CircularNetworkImageView cnvFotoUsuario;
    TextView tvNomeUsuario;
    TextView tvEmailUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_perfil_usuario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        cnvFotoUsuario = (CircularNetworkImageView) findViewById(R.id.fotoPerfilUsuario);
        tvEmailUsuario = (TextView) findViewById(R.id.tvEmailPerfilUsuario);
        tvNomeUsuario = (TextView) findViewById(R.id.tvNomePerfilUsuario);

        Bundle params = getIntent().getExtras();
        int idUsuario;
        if (params != null)
            idUsuario = params.getInt("idUsuario");
        else
            idUsuario = UserControlV2.getInstance().userId;

        carregaPerfil(idUsuario);
    }


    public void carregaPerfil(int idUsuario) {

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
        String url = "";
            url = uri.getHost() + uri.URI_USER_PROFILE + "/" + String.valueOf(idUsuario);

        final Activity activity = this;
        //faz o request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {


                            if (!jsonObject.getBoolean("error")) {

                                //faz o download da imagem de acordo com o path retornado pelo backend
                                URIWebservice uri = new URIWebservice(activity);
                                ImageLoader.ImageCache imageCache = new BitmapLruCache();
                                ImageLoader imgFotoUsuario = new ImageLoader(VolleySingleton.getInstance().newRequestQueue(activity), imageCache);
                                String pathFotoUsuario = uri.getHost() + jsonObject.getString("foto_usuario");
                                cnvFotoUsuario.setImageUrl(pathFotoUsuario, imgFotoUsuario);

                                //define o nome do usuário e o email
                                tvEmailUsuario.setText(jsonObject.getString("email"));
                                tvNomeUsuario.setText(jsonObject.getString("nomeCompleto"));

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
