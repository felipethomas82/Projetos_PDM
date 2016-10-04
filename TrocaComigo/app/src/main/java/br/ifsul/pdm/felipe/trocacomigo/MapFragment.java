package br.ifsul.pdm.felipe.trocacomigo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
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
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * A fragment that launches other parts of the demo application.
 */
public class MapFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap googleMap = null;

    private AppCompatActivity activity;

    public MapFragment(){
        super();
    }

    public MapFragment(AppCompatActivity activity){
        this.activity = activity;
    }

    private SupportMapFragment mSupportMapFragment;

    //lista com os marcadores simples e com imagem
    private HashMap<Integer, MarkerOptions> marcadoresSimples;
    private HashMap<Integer, MarkerOptions> marcadoresComImagem;
    Integer i = 0; //key para o HashMap dos marcadores simples
    Integer j = 0; //Key para o HashMap dos marcadores com imagem

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate e retorna o layout
        View v = inflater.inflate(R.layout.activity_maps, container, false);

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.location_map);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.location_map, mSupportMapFragment).commit();
        }

        mSupportMapFragment.getMapAsync(this);

        //carregaAnuncios();

        return v;

    }

    @Override
    public void onResume(){
        super.onResume();
        carregaAnuncios();
    }


    public void carregaAnuncios() {

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        File cacheDir = new File(getActivity().getCacheDir(), "teste");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        // clear all volley caches.
        queue.add(new ClearCacheRequest(cache, null));

        URIWebservice uri = new URIWebservice(getActivity());
        String url = uri.getHost() + uri.URI_PRODUCTS +
                "/" + UserControlV2.latitude +
                "/" + UserControlV2.longitude +
                "/" + 200; //o ultimo parametro é a distancia;

        Log.i("Script", url);
        //faz o request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            URIWebservice uri = new URIWebservice(getActivity());

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
                                    a.fotoUsuario = uri.getHost() + mAnuncio.getString("fotoUsuario");
                                    a.latitude = mAnuncio.getDouble("latitude");
                                    a.longitude = mAnuncio.getDouble("longitude");

                                    anuncios.add(a);
                                }

                                criaMarcadoresAnuncios(anuncios);


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

    @Override
    public void onMapReady(GoogleMap map) {

        googleMap = map;

        googleMap.getUiSettings().setAllGesturesEnabled(true);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //TODO: Coordenadas de teste. Substituir pela localização atual do usuário
        LatLng saoJeo = new LatLng(-29.9645676,-51.7274363);

        CameraPosition cameraPosition = new CameraPosition.Builder().target(saoJeo).zoom(13.0f).build();
        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);

        googleMap.moveCamera(cameraUpdate);

    }


    private void criaMarcadoresAnuncios(ArrayList<Anuncio> anuncios) {

        //somente cria os marcadores quando for a primeira vez que carregar o mapa...
        if (marcadoresSimples == null && googleMap != null) {
            marcadoresSimples = new HashMap<Integer, MarkerOptions>();

            for (final Anuncio anuncio : anuncios) {

                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marcador_verde);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 40, 40, false);

                LatLng marcador = new LatLng(anuncio.latitude, anuncio.longitude);

                String titulo = anuncio.titulo + " - R$ " + anuncio.valor + " - " + anuncio.tipoAuncio;
                marcadoresSimples.put(i, new MarkerOptions().position(marcador).title(titulo).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

                i++;

            } //for

            for (int j = 0; j < marcadoresSimples.size(); j++) {
                googleMap.addMarker(marcadoresSimples.get(j));
            }


        } //if


    } //criaMarcadoresAnuncio




}