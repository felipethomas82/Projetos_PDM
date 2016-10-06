package com.example.pc.aula_volley_httprequest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AtvPosts extends AppCompatActivity {

    List<Post> posts = new ArrayList<Post>();
    RecyclerView recyclerView;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_posts);

        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        context = this;

        carregaPosts();
    }

    private void carregaPosts() {
        RequestQueue mRequestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // Set up the network to use HttpURLConnection as the HTTP client.
        Network network = new BasicNetwork(new HurlStack());
        // Instantiate the RequestQueue with the cache and network.
        mRequestQueue = new RequestQueue(cache, network);
        // Start the queue
        mRequestQueue.start();

        final String url = "http://10.0.3.2/WS_Aula_PDM/v1/posts";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<Post> posts = new ArrayList<Post>();
                        try {
                            //Log.i("Teste", response.toString());
                            JSONArray jsonArray = response.getJSONArray("posts");
                            Gson gson = new Gson();
                            for (int i=0; i<jsonArray.length(); i++){
                                //Log.i("Testezin", jsonArray.getJSONObject(i).toString());
                                Post post = gson.fromJson(jsonArray.getJSONObject(i).toString(), Post.class);
                                posts.add(post);
                            } //fim for

                            //seta o adapter do recyclerView
                            recyclerView.setAdapter(new AdaptadorPost(posts, context));
                            RecyclerView.LayoutManager layout = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
                            recyclerView.setLayoutManager(layout);
                            //Log.i("TESTEZAO", posts.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //tratar erro
                        Log.i("Teste", error.toString());
                    }
                });


        mRequestQueue.add(jsonObjectRequest);

    }
}
