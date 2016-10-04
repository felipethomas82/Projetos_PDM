package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
//import com.parse.ParseInstallation;
//import com.parse.ParsePush;
//import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class atvDetalhesProduto extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{

//    UserControl userControl;
    int idProduto;

    Map<String, String> params;

    TextView tvProductPrice;
    TextView tvProductDescription;
    TextView tvWishList;
    TextView tvProductTitle;
    TextView tvRegisterDate;
    TextView tvType;
    TextView tvNomeUsuario;
    TextView tvCidade;
    CircularNetworkImageView cnvFotoUsuario;
    EditText etComentario;
    String emailUsuario;

    //é necessário amarrar o layout onde está o nome e foto do usuário para o clickListener
    LinearLayout llNomeUsuario;

    private ArrayList<Comentario> comments;
    Context context;

    CollapsingToolbarLayout collapsingToolbar;

    boolean veioDoCadastro;
    boolean flagBuscaCoordenadas = false;

    Anuncio mAnuncio;

    GoogleMap myMap;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_detalhes_produto);

        context = this;
        
        //suporte a appBar do Material design
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbarLayout);

        tvProductDescription = (TextView) findViewById(R.id.tvDescricaoProduto);
        tvProductPrice = (TextView) findViewById(R.id.tvPrecoProduto);
        tvProductTitle = (TextView) findViewById(R.id.tvTituloProduto);
        tvWishList = (TextView) findViewById(R.id.tvListaDeDesejos);
        tvRegisterDate = (TextView) findViewById(R.id.tvDataCadastroAnuncio);
        tvType = (TextView) findViewById(R.id.tvTipoAnuncio);
        etComentario = (EditText) findViewById(R.id.etComentario);
        tvNomeUsuario = (TextView) findViewById(R.id.tvNomeUsuarioDetalhesAnuncio);
        tvCidade = (TextView) findViewById(R.id.tvLocalizacaoAnuncio);
        cnvFotoUsuario = (CircularNetworkImageView) findViewById(R.id.imgFotoUsuarioDetalhesAnuncio);
        llNomeUsuario = (LinearLayout) findViewById(R.id.layoutNomeUsuarioDetalhesProduto);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();


        //Log.i("Script", "TESTE " + params.getString("apiKey") + " - " + params.getInt("userId"));

        if (params != null)
        {
            /*
            try {
                userControl = new UserControl(params);
            } catch (UserControl.MissingUserParamsException e) {
                e.printStackTrace();
            }
            */

            idProduto = params.getInt("idProduto");

            veioDoCadastro = params.getBoolean("veioDoCadastro", false);
        }
        else //TODO: Else para testes. Remover na versão final
        {
            idProduto = 10;
            //userControl = new UserControl(16, "b2f05bc10c77c0a3399a2be9410c92a0");
        }

        mAnuncio = new Anuncio();

        //chama a biblioteca de suporte do maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //tenta conectar com a api do google
        buildGoogleApiClient();

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

        if (id == android.R.id.home){

            //se nao veio da tela de cadastro, apenas volta a tela principal (volta ancorado ao mesmo ponto onde se encontrava
            if (!veioDoCadastro){
                super.onBackPressed();
            }
            else {
                //se veio da tela de cadastro do produto, deve retornar para a tela inicial, fazendo com que os produtos sejam recarregados
                Intent intent = new Intent();
                intent.setClass(atvDetalhesProduto.this, atvListaProdutos.class);

                /*
                Bundle params = new Bundle();
                params.putString("apiKey", userControl.getApiKey());
                params.putInt("userId", userControl.getUserId());

                intent.putExtras(params);
                */
                startActivity(intent);
            }

        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();
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
                                JSONObject produto = jsonObject.getJSONObject("products");

                                String productTitle = jsonObject.getJSONObject("products").getString("titulo");
                                String productDescription = jsonObject.getJSONObject("products").getString("descricao");
                                String wishList = jsonObject.getJSONObject("products").getString("aceitaTrocar");
                                Double productPrice = jsonObject.getJSONObject("products").getDouble("preco");
                                String registerDate = jsonObject.getJSONObject("products").getString("dataCadastro");
                                String type = jsonObject.getJSONObject("products").getString("tipoAnuncio");
                                String username = jsonObject.getJSONObject("products").getString("nomeUsuario");
                                String pathFotoUsuario = jsonObject.getJSONObject("products").getString("fotoUsuario");
                                final int idUsuario = jsonObject.getJSONObject("products").getInt("idUsuario");

                                //seta os dados de localização do anuncio
                                if (!produto.isNull("latitude")) {
                                    mAnuncio.latitude = produto.getDouble("latitude");
                                    mAnuncio.longitude = produto.getDouble("longitude");
                                } else {
                                    mAnuncio.latitude = 0;
                                    mAnuncio.longitude = 0;
                                }
                                mAnuncio.endereco = jsonObject.getJSONObject("products").getString("endereco");
                                mAnuncio.bairro = produto.getString("bairro");
                                mAnuncio.cidade = produto.getString("cidade");
                                mAnuncio.estado = produto.getString("estado");

                                buscaCoordenadas();

                                //MeuGeocoder meuGeocoder = new MeuGeocoder(atvDetalhesProduto.this, enderecoBusca);
                                //meuGeocoder.buscaCoordenadas();

                                NumberFormat numberFormat = NumberFormat.getCurrencyInstance();
                                String formatedPrice = numberFormat.format(productPrice);

                                tvProductTitle.setText(productTitle);
                                tvProductDescription.setText(productDescription);
                                tvWishList.setText(wishList);
                                tvProductPrice.setText(formatedPrice);
                                tvRegisterDate.setText(registerDate);
                                tvType.setText(type);
                                tvNomeUsuario.setText(username);
                                tvCidade.setText(produto.getString("cidade") + " - " + produto.getString("estado"));
                                emailUsuario = jsonObject.getJSONObject("products").getString("emailUsuario");

                                //faz o download da imagem do usuário
                                ImageLoader.ImageCache imageCache = new BitmapLruCache();
                                ImageLoader imgFotoUsuario = new ImageLoader(VolleySingleton.getInstance().newRequestQueue(atvDetalhesProduto.this), imageCache);
                                URIWebservice uri = new URIWebservice(atvDetalhesProduto.this);
                                pathFotoUsuario = uri.getHost() + pathFotoUsuario;
                                cnvFotoUsuario.setImageUrl(pathFotoUsuario, imgFotoUsuario);

                                llNomeUsuario.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setClass(atvDetalhesProduto.this, atvPerfilUsuario.class);
                                        Bundle params = new Bundle();
                                        params.putInt("idUsuario", idUsuario);
                                        intent.putExtras(params);
                                        startActivity(intent);
                                    }
                                });

                                collapsingToolbar.setTitle(productTitle);

                                //URIWebservice uri = new URIWebservice(atvDetalhesProduto.this);
                                //array que vai conter as urls das fotos para adicionar o viewpager
                                ArrayList<String> urlImages = new ArrayList<String>();

                                //O retornno do path das fotos � em uma array
                                JSONArray pathPhotos = jsonObject.getJSONObject("products").getJSONArray("foto");
                                for (int i = 0; i < pathPhotos.length(); i++)
                                {
                                    //monta a string da url concatenando o host, porque o retorno do
                                    urlImages.add(uri.getHost() + pathPhotos.getString(i));
                                }

                                ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
                                ImageAdapter adapter = new ImageAdapter(atvDetalhesProduto.this);
                                adapter.setUrlImages(urlImages);
                                viewPager.setAdapter(adapter);

                                try {
                                    JSONArray comentarios = jsonObject.getJSONObject("products").getJSONArray("comentarios");

                                    if (comentarios != null) {
                                        comments = new ArrayList<Comentario>();
                                        for (int i = 0; i < comentarios.length(); i++) {
                                            JSONObject c = comentarios.getJSONObject(i);
                                            String pathFotoUser = uri.getHost() + c.getString("pathFoto");
                                            String mensagem = c.getString("comentario");
                                            String nomeUsuario = c.getString("nomeUsuario");
                                            Comentario comment = new Comentario(pathFotoUser, mensagem, nomeUsuario);

                                            //adiciona o comentario (objeto) a array list comments
                                            comments.add(comment);
                                        }

                                        ArrayAdapter<Comentario> commentAdapter = new CommentAdapter(context, R.layout.comentario, comments);
                                        ListView lvComentarios = (ListView) findViewById(R.id.lvComentarios);

                                        lvComentarios.setAdapter(commentAdapter);

                                        //captura o touch no listview para dar o scroll
                                        lvComentarios.setOnTouchListener(new ListView.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                int action = event.getAction();
                                                switch (action) {
                                                    case MotionEvent.ACTION_DOWN:
                                                        // Disallow ScrollView to intercept touch events.
                                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                                        break;

                                                    case MotionEvent.ACTION_UP:
                                                        // Allow ScrollView to intercept touch events.
                                                        v.getParent().requestDisallowInterceptTouchEvent(false);
                                                        break;
                                                }

                                                // Handle ListView touch events.
                                                v.onTouchEvent(event);
                                                return true;
                                            }
                                        });
                                    }//fim do comentarios != null
                                }catch (JSONException e){
                                    //se o json comentarios vier vazio do webservice, entao seta o height para 10..
                                    ListView listView = (ListView) findViewById(R.id.lvComentarios);
                                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                                    params.height = 10;
                                    listView.setLayoutParams(params);
                                    listView.requestLayout();
                                }

                            } else {
                                Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao consultar o produto: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } //fim if
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao consultar o produto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },//fim listerner json
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(atvDetalhesProduto.this, "Sistema fora do Ar. " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

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



    public void clickBtnSalvarComentario(View V) {
        //desabilita o botao para nao ocorrer de o usuario chamar duas vezes o cadastro do produto
        final Button b = (Button) V;
        b.setEnabled(false);

        //monta os parametros. Para ser passado ao request
        params = new HashMap<String, String>();
        params.put("mensagem", etComentario.getText().toString());


        //codigo para limpar o cache
        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        File cacheDir = new File(this.getCacheDir(), "teste");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        // limpa cache
        queue.add(new ClearCacheRequest(cache, null));

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_PRODUCT_COMMENTS + "/" + idProduto;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(url,
                new JSONObject(params),
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            //pega o id retornado na insercao do produto
                            int idComment = jsonObject.getInt("comment_id");
                            b.setEnabled(true);

                            etComentario.setText("");

                            ParseQuery pushQuery = ParseInstallation.getQuery();
                            pushQuery.whereEqualTo("email", emailUsuario);

                            ParsePush push = new ParsePush();
                            push.setQuery(pushQuery);
                            push.setMessage("Tem gente interessada no seu produto, dá uma olhada lá.");
                            push.sendInBackground();

                            //em caso de sucesso, recarrega o anúncio para mostrar o comentário
                            commentsUpdate();

                        } catch (JSONException e) {
                            Log.i("Script", "SUCCES: erro" + jsonObject.toString());
                            b.setEnabled(true);
                            e.printStackTrace();
                        }
                    }
                },//fim Listener<JSONObject>
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        b.setEnabled(true);
                        Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao registrar o comentários: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
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

    }//fim clickBtnSalvar



    public void commentsUpdate() {

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

                                try {

                                    JSONArray comentarios = jsonObject.getJSONObject("products").getJSONArray("comentarios");

                                    if (comentarios != null) {
                                        //se for o primeiro comentario que estiver sendo inserido, deve-se aumentar o tamanho do height do listview
                                        if (comentarios.length() == 1){
                                            ListView listView = (ListView) findViewById(R.id.lvComentarios);
                                            ViewGroup.LayoutParams params = listView.getLayoutParams();
                                            params.height = 300;
                                            listView.setLayoutParams(params);
                                            listView.requestLayout();
                                        }

                                        comments = new ArrayList<Comentario>();
                                        for (int i = 0; i < comentarios.length(); i++) {
                                            JSONObject c = comentarios.getJSONObject(i);
                                            URIWebservice uri = new URIWebservice(context);
                                            String pathFotoUser = uri.getHost() + c.getString("pathFoto");
                                            String mensagem = c.getString("comentario");
                                            String nomeUsuario = c.getString("nomeUsuario");
                                            Comentario comment = new Comentario(pathFotoUser, mensagem, nomeUsuario);

                                            //adiciona o comentario (objeto) a array list comments
                                            comments.add(comment);
                                        }

                                        ArrayAdapter<Comentario> commentAdapter = new CommentAdapter(context, R.layout.comentario, comments);
                                        ListView lvComentarios = (ListView) findViewById(R.id.lvComentarios);

                                        lvComentarios.setAdapter(commentAdapter);

                                        //captura o touch no listview para dar o scroll
                                        lvComentarios.setOnTouchListener(new ListView.OnTouchListener() {
                                            @Override
                                            public boolean onTouch(View v, MotionEvent event) {
                                                int action = event.getAction();
                                                switch (action) {
                                                    case MotionEvent.ACTION_DOWN:
                                                        // Disallow ScrollView to intercept touch events.
                                                        v.getParent().requestDisallowInterceptTouchEvent(true);
                                                        break;

                                                    case MotionEvent.ACTION_UP:
                                                        // Allow ScrollView to intercept touch events.
                                                        v.getParent().requestDisallowInterceptTouchEvent(false);
                                                        break;
                                                }

                                                // Handle ListView touch events.
                                                v.onTouchEvent(event);
                                                return true;
                                            }
                                        });
                                    }//fim do comentarios != null
                                }catch (JSONException e){
                                    //se o json comentarios vier vazio do webservice, entao seta o height para 10..
                                    ListView listView = (ListView) findViewById(R.id.lvComentarios);
                                    ViewGroup.LayoutParams params = listView.getLayoutParams();
                                    params.height = 10;
                                    listView.setLayoutParams(params);
                                    listView.requestLayout();
                                }

                            } else {
                                Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao consultar o produto: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } //fim if
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                            Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao consultar o produto: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },//fim listerner json
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(atvDetalhesProduto.this, "Sistema fora do Ar. " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

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

        jsonRequest.setTag("comments");
        queue.add(jsonRequest);
    }

    @Override
    public void onMapReady(GoogleMap map) {

        myMap = map;
        // Add a marker in Sydney, Australia, and move the camera.
        // LatLng sydney = new LatLng(-34, 151);
        LatLng endereco;
        if (mAnuncio.latitude != 0){
            endereco = new LatLng(mAnuncio.latitude, mAnuncio.longitude);
        }
        else {
            //coordenadas do brasil (centro)
            endereco = new LatLng(-14.235004, -51.92528);
            //map.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        }

        myMap.addMarker(new MarkerOptions().position(endereco).title("Brasil"));
        myMap.moveCamera(CameraUpdateFactory.newLatLng(endereco));
        myMap.animateCamera(CameraUpdateFactory.zoomTo(1.0f));
    }



    @Override
    public void onConnected(Bundle bundle) {

        // Determine whether a Geocoder is available.
        if (!Geocoder.isPresent()) {
            Toast.makeText(this, "Geocoder nao habilitado", Toast.LENGTH_LONG).show();
            return;
        }

        if (flagBuscaCoordenadas)
            new BuscaCoordenadas().execute();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }



    public void buscaCoordenadas() {

        if (mGoogleApiClient.isConnected()) {
            new BuscaCoordenadas().execute();
        }
        // Se a googleApi nao estiver conectada, processa a requisicao setando a flag
        // para quando a api conectar iniciar a busca da coordenada com a AsyncTask
        flagBuscaCoordenadas = true;
        //updateUIWidgets();
    }



    class BuscaCoordenadas extends AsyncTask<Void, Void, LatLng>{


        @Override
        protected LatLng doInBackground(Void... voids) {

            String enderecoBusca = mAnuncio.endereco + ", " + mAnuncio.bairro + " - " + mAnuncio.cidade + " - " + mAnuncio.estado;
            enderecoBusca = enderecoBusca.replace("null", "");

            LatLng coordenadas;

            Geocoder geocoder = new Geocoder(atvDetalhesProduto.this, Locale.getDefault());

            String mensagemErro = "";

            //pega o local passado via extras
            String endereco = enderecoBusca;

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
                //INFORMA O ERRO

            } else {
                Address enderecoEncontrado = enderecos.get(0);

                double lat = enderecoEncontrado.getLatitude();
                double lng = enderecoEncontrado.getLongitude();

                coordenadas = new LatLng(lat, lng);
                //Log.i("Script", "Achou coordenadas --> " + coordenadas.toString());

                return coordenadas;
            }

            return null;
        }

        @Override
        protected void onPostExecute(LatLng coordenadas) {

            try {
                Log.i("Script", "Achou coordendas: " + coordenadas.toString());

                LatLng endereco = coordenadas;

                myMap.addMarker(new MarkerOptions().position(endereco).title(tvProductTitle.getText().toString()));
                myMap.moveCamera(CameraUpdateFactory.newLatLng(endereco));
                myMap.animateCamera(CameraUpdateFactory.zoomTo(15.0f));

                salvaCoordenadas(coordenadas);
            } catch (NullPointerException e) {
                Log.i("Script", "Geocoder não retornou nenhum valor, ou sem internet");
            }
        }

    }






    public void salvaCoordenadas2(LatLng coordenadas)
    {
        //Log.i("Script", "COORDENADAS" + coordenadas.toString());
        params = new HashMap<String, String>();
        params.put("latitude", String.valueOf(coordenadas.latitude));
        params.put("longitude", String.valueOf(coordenadas.longitude));

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_PRODUCT_COORDINATES + "/" + idProduto;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.PUT,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            boolean resp = jsonObject.getBoolean("error");

                            Log.i("Script", "SUCCESSO: " + jsonObject.toString());
                        } catch (JSONException e) {
                            Log.i("Script", "SUCCES: erro" + jsonObject.toString());
                            e.printStackTrace();
                        }
                    }
                },//fim Listener<JSONObject>
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao salvar as coordenadas: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
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

        RequestQueue mRequestQueue;

        // Instancia o cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // setup da rede.
        Network network = new BasicNetwork(new HurlStack());
        // instancia a requestQueue com o cache e a rede
        mRequestQueue = new RequestQueue(cache, network);
        // antes de adicionar o request, deve-se startar a queu
        mRequestQueue.start();

        //define que a reposta não sera enviada ao cache
        jsonRequest.setShouldCache(false);
        mRequestQueue.add(jsonRequest);

        //todos os comandos acima poderiam ser substituidos pela classe singleton abaixo, contudo,
        // estava estourando a memoria

        // VolleySingleton.getInstance().addToRequestQueue(jsonRequest, "reqEndereco");


    }//fim salvarCoordenadas


    public void salvaCoordenadas(final LatLng coordenadas)
    {

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_PRODUCT_COORDINATES + "/" + idProduto;

        StringRequest putRequest = new StringRequest(Request.Method.PUT,
                url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                            Log.i("Script", "SUCCESSO: " + response);

                    }
                },//fim Listener<String>
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(atvDetalhesProduto.this, "Ocorreu um erro ao salvar as coordenadas: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            //sobrescrevendo o header
            @Override
            public HashMap<String, String> getParams(){
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", UserControlV2.getInstance().apiKey);
                params.put("User_id", String.valueOf(UserControlV2.getInstance().userId));
                params.put("latitude", String.valueOf(coordenadas.latitude));
                params.put("longitude", String.valueOf(coordenadas.longitude));

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

        RequestQueue mRequestQueue;

        // Instancia o cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap
        // setup da rede.
        Network network = new BasicNetwork(new HurlStack());
        // instancia a requestQueue com o cache e a rede
        mRequestQueue = new RequestQueue(cache, network);
        // antes de adicionar o request, deve-se startar a queu
        mRequestQueue.start();

        //define que a reposta não sera enviada ao cache
        putRequest.setShouldCache(false);
        mRequestQueue.add(putRequest);

    }//fim salvarCoordenadas
}
