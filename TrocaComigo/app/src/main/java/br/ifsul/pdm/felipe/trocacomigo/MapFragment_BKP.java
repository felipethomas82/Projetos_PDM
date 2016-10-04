package br.ifsul.pdm.felipe.trocacomigo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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
public class MapFragment_BKP extends Fragment implements OnMapReadyCallback {

    private GoogleMap googleMap = null;

    private AppCompatActivity activity;

    public MapFragment_BKP(){
        super();
    }

    public MapFragment_BKP(AppCompatActivity activity){
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



//        mMapView = (MapView) v.findViewById(R.id.mapView);
//        mMapView.onCreate(savedInstanceState);
//
//        mMapView.onResume();// needed to get the map to display immediately
//        try {
//            MapsInitializer.initialize(getActivity().getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        googleMap = mMapView.getMap();
//        // latitude and longitude
//        double latitude = 17.385044;
//        double longitude = 78.486671;
//
//        // create marker
//        MarkerOptions marker = new MarkerOptions().position(
//                new LatLng(latitude, longitude)).title("Hello Maps");
//
//        // Changing marker icon
//        marker.icon(BitmapDescriptorFactory
//                .defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
//
//        // adding marker
//        googleMap.addMarker(marker);
//        CameraPosition cameraPosition = new CameraPosition.Builder()
//                .target(new LatLng(17.385044, 78.486671)).zoom(12).build();
//        googleMap.animateCamera(CameraUpdateFactory
//                .newCameraPosition(cameraPosition));

        // Perform any camera updates here
        return v;

        //carregaAnuncios();
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
        String url = uri.getHost() + uri.URI_PRODUCTS;

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

        googleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {

            private boolean trocouMarcadores = false;
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                //Log.i("Script", "ZOOM ==> " + cameraPosition.zoom);
                if (cameraPosition.zoom >= 15 && !trocouMarcadores){

                    googleMap.clear();
                    for (int j=0; j<marcadoresComImagem.size(); j++){
                        googleMap.addMarker(marcadoresComImagem.get(j));
                    }
                    trocouMarcadores = true;

                }
                if (cameraPosition.zoom < 15 && trocouMarcadores){
                    if (trocouMarcadores) {
                        googleMap.clear();
                        for (int j = 0; j < marcadoresSimples.size(); j++) {
                            googleMap.addMarker(marcadoresSimples.get(j));
                        }
                        trocouMarcadores = false;
                    }
                }
            }
        });
    }


    private void criaMarcadoresAnuncios(ArrayList<Anuncio> anuncios) {

        //somente cria os marcadores quando for a primeira vez que carregar o mapa...
        if (marcadoresSimples == null) {
            marcadoresSimples = new HashMap<Integer, MarkerOptions>();
            marcadoresComImagem = new HashMap<Integer, MarkerOptions>();


            for (final Anuncio anuncio : anuncios) {

                Bitmap imageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.marcador_verde);
                Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 40, 40, false);

                LatLng marcador = new LatLng(anuncio.latitude, anuncio.longitude);

                marcadoresSimples.put(i, new MarkerOptions().position(marcador).title(anuncio.titulo).icon(BitmapDescriptorFactory.fromBitmap(resizedBitmap)));

                final View marker = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);
                final NetworkImageView nivCapaMarcador = (NetworkImageView) marker.findViewById(R.id.ivCapaMarcador);
                ImageLoader.ImageCache imageCache = new BitmapLruCache();
                ImageLoader ilFotoMarcador = new ImageLoader(newRequestQueue(getActivity()), imageCache);
                nivCapaMarcador.setImageUrl(anuncio.pathFotoCapa, ilFotoMarcador);
                nivCapaMarcador.setTag(anuncio.id);

                //cria o listener do download da imagem para criar o marcador somente após o recebimento
                //senão o marcador seria criado sem a imagem dentro do imageView
                ilFotoMarcador.get(anuncio.pathFotoCapa, new ImageLoader.ImageListener() {

                    public void onErrorResponse(VolleyError error) {
                        //   imageView.setImageResource(R.drawable.icon_error); // set an error image if the download fails
                    }

                    public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                        //Log.i("Script", "Fez o download da imagem >> " + response.toString());
                        if (response.getBitmap() != null) {

                            nivCapaMarcador.setImageBitmap(response.getBitmap());

                            Bitmap bmpMarcador = createDrawableFromView(getActivity(), marker);

                            LatLng marcador = new LatLng(anuncio.latitude, anuncio.longitude);
                            marcadoresComImagem.put(j, new MarkerOptions().position(marcador).title(anuncio.titulo).icon(BitmapDescriptorFactory.fromBitmap(bmpMarcador)));
                            //Log.i("Script", "MARCADOR " + marcadoresComImagem.toString());
                            //googleMap.addMarker(new MarkerOptions().position(marcador).title(anuncio.titulo).icon(BitmapDescriptorFactory.fromBitmap(bmpTeste)));
                            j++;

                        }
                    }
                });

                i++;

            } //for

            for (int j = 0; j < marcadoresSimples.size(); j++) {
                googleMap.addMarker(marcadoresSimples.get(j));
            }


        } //if


    } //criaMarcadoresAnuncio


    // Convert a view em um bitmap
    public static Bitmap createDrawableFromView(Context context, View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        view.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }



    // Default maximum disk usage in bytes
    private static final int DEFAULT_DISK_USAGE_BYTES = 25 * 1024 * 1024;

    // Default cache folder name
    private static final String DEFAULT_CACHE_DIR = "photos";

    // Most code copied from "Volley.newRequestQueue(..)", we only changed cache directory
    private static RequestQueue newRequestQueue(Context context) {
        // define cache folder
        File rootCache = context.getExternalCacheDir();
        if (rootCache == null) {
            //L.w("Can't find External Cache Dir, switching to application specific cache directory");
            rootCache = context.getCacheDir();
        }

        File cacheDir = new File(rootCache, DEFAULT_CACHE_DIR);
        cacheDir.mkdirs();

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        DiskBasedCache diskBasedCache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);
        RequestQueue queue = new RequestQueue(diskBasedCache, network);
        queue.start();

        return queue;
    }
}