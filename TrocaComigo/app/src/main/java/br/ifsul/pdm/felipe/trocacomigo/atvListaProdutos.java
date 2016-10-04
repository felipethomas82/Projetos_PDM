package br.ifsul.pdm.felipe.trocacomigo;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v7.widget.SearchView;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.Volley;
//import com.parse.Parse;
//import com.parse.ParseInstallation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class atvListaProdutos extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int userId;
    private String apiKey;
    UserControl userControl;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private FloatingActionButton fab;

    SearchManager searchManager;
    SearchView sv;

    private ArrayList ItemArrayList;

    private List<String> items; //items do cursor para sugestoes do searchView. Busca no BD
    private boolean filtraAnuncios = false;
    private String queryFiltroAnuncios;

    private boolean inflouLayoutTabs = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_atv_lista_produtos);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        //Log.i("TESTEE", params.toString());


        //Se a activity foi chamada pela solicitação da SearchView...
        //No primeiro if o usuario submeteu a query direto da searchview, o outro if é quando clicou em uma sugestao
        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Handle the normal search query case
            String query = intent.getStringExtra(SearchManager.QUERY);
            queryFiltroAnuncios = query;
            filtraAnuncios = true;
            //doSearch(query);
        } else if (intent.ACTION_VIEW.equals(intent.getAction())) {
            // Handle a suggestions click (because the suggestions all use ACTION_VIEW)
            Uri data = intent.getData();
            //Data está vindo NULL, descobrir o motivo
            //Log.i("Script:", "TESTE 2 --> " + data.toString());
            //showResult(data);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);


        //amarração e configuração do drawwr layout, menu esquerdo.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //define o nome do usuário e o email nas textViews do drawerlayout
        TextView tvNomeUsuario = (TextView) findViewById(R.id.tvNomeUsuarioDrawer);
        tvNomeUsuario.setText(UserControlV2.getInstance().username);
        TextView tvEmailUsuario = (TextView) findViewById(R.id.tvEmailUsuario);
        tvEmailUsuario.setText(UserControlV2.getInstance().email);
        //amarra e puxa a foto do usuário no drawer layout
        CircularNetworkImageView fotoUsuarioDrawer = (CircularNetworkImageView) findViewById(R.id.imgFotoUsuarioDrawer);
        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        ImageLoader imgFotoUsuario = new ImageLoader(VolleySingleton.getInstance().newRequestQueue(this), imageCache);
        URIWebservice uri = new URIWebservice(atvListaProdutos.this);
        String pathFotoUsuario = uri.getHost() + UserControlV2.getInstance().pathUserPhoto;
        fotoUsuarioDrawer.setImageUrl(pathFotoUsuario, imgFotoUsuario);

        //o metodo carrega anuncios foi removido a partir do momento que passou a fazer parte do fragment
        //por causa do tabLayout e viewPager
        //carregaAnuncios();

//        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
//        installation.put("email", UserControlV2.getInstance().email);
//        installation.saveInBackground();

        if (UserControlV2.getInstance().latitude == 0) {
            Localizacao localizacao = new Localizacao(this, this);
            localizacao.iniciaGeoLocalizacao();
        } else {
            achouLocalizacao();
        }


        carregaAlertas();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_lista_produtos, menu);

        //config do SearchView
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        sv = (SearchView) menu.findItem(R.id.action_search).getActionView();
        sv.setOnQueryTextListener(new SearchFiltro());

        sv.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

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


    public void onClickCadastrarProduto(View v) {

        Intent intent = new Intent(atvListaProdutos.this, atvCadastrarProduto.class);
        //seta os parametros para passar a outra activity
        //TODO: Trocar pela singleton
        Bundle bundle = new Bundle();
        bundle.putString("apiKey", apiKey);
        bundle.putInt("userId", userId);

        intent.putExtras(bundle);
        startActivity(intent);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        //se a activity foi chamada pelo SearchManager, entao deve-se filtrar os anuncios
        if (filtraAnuncios)
            adapter.addFrag(new ListaProdutosFragment(this, queryFiltroAnuncios), "Lista");
        else
            adapter.addFrag(new ListaProdutosFragment(this, ""), "Lista");

        //TODO: Descomentar o mapa antes do final do TCC
        adapter.addFrag(new MapFragment(this), "Mapa");

        //adapter.addFrag(new MapFragment(), "TEST");
        viewPager.setAdapter(adapter);

        //Lister do change page para ocultar ou mostrar o floating button
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {


            }

            @Override
            public void onPageSelected(int position) {
                if (position > 0) {
                    fab.clearAnimation();
                    Animation animation = AnimationUtils.loadAnimation(atvListaProdutos.this, R.anim.pop_down);
                    fab.startAnimation(animation);
                    fab.setClickable(false);
                } else {
                    fab.clearAnimation();
                    Animation animation = AnimationUtils.loadAnimation(atvListaProdutos.this, R.anim.pop_up);
                    fab.startAnimation(animation);
                    fab.setClickable(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_busca) {
            // Handle the camera action
        } else if (id == R.id.nav_mapa_de_anuncios) {

            Intent intent = new Intent();
            intent.setClass(this, MapsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_anuncios_usuario) {

            Intent intent = new Intent();
            intent.setClass(this, atvAnunciosUsuario.class);
            startActivity(intent);

        } else if (id == R.id.nav_perfil_usuario) {

            Intent intent = new Intent();
            intent.setClass(this, atvPerfilUsuario.class);
            startActivity(intent);

        } else if (id == R.id.nav_notificacoes) {

            Intent intent = new Intent();
            intent.setClass(this, AtvNotificacoes.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //metodo que é chamado quando a localização do usuário é encontrada, para mostrar os anuncios
    //e ativar as tabs do viewpager
    public void achouLocalizacao() {

        if (!inflouLayoutTabs) {
            mViewPager = (ViewPager) findViewById(R.id.viewPagerListaProdutos);
            setupViewPager(mViewPager);

            mTabLayout = (TabLayout) findViewById(R.id.tabListaProdutos);
            mTabLayout.setupWithViewPager(mViewPager);

            inflouLayoutTabs = true;
        }
    }


    private class SearchFiltro implements SearchView.OnQueryTextListener {
        @Override
        public boolean onQueryTextSubmit(String s) {


            return false;
        }

        @Override
        public boolean onQueryTextChange(String s) {

            //buscarSugestoes();

            carregaSugestoesBD(s);

            return false;
        }
    }


    private void buscarSugestoes() {

        // Cursor
        String[] columns = new String[]{"_id", "text"};
        Object[] temp = new Object[]{0, "default"};

        MatrixCursor cursor = new MatrixCursor(columns);

        for (int i = 0; i < items.size(); i++) {

            temp[0] = i;
            temp[1] = items.get(i);

            cursor.addRow(temp);

        }
        //Log.i("Script", "TESTE: " + this.toString() + " -- " + cursor.toString() + " -- " + items.toString());
        sv.setSuggestionsAdapter(new AdapterSugestoes(this, cursor, items));


    }


    public void carregaSugestoesBD(String palavraQuery) {

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
        String url = uri.getHost() + uri.URI_PRODUCT_TITLE_SUGGESTIONS + "/" + palavraQuery;

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

                            //URIWebservice uri = new URIWebservice(atvListaProdutos.this);

                            if (!jsonObject.getBoolean("error")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("products");

                                //Configuração do SearchView - Cursor com os titulos
                                items = new ArrayList<String>();

                                for (int i = 0; i < jsonArray.length(); i++) {

                                    JSONObject mAnuncio = jsonArray.getJSONObject(i);
                                    items.add(mAnuncio.getString("titulo"));
                                }

                                buscarSugestoes();

                            }
                        } //fim if
                        catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(atvListaProdutos.this, "Ocorreu um erro ao carregar os anúncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },//fim listerner json
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(atvListaProdutos.this, "Sistema fora do Ar. " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            //sobrescrevendo o header
            @Override
            public HashMap<String, String> getParams() {
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


    private NavigationView mNavigationView;

    private int FILTER_ID = 10;

    public void setaCrachaNotificacoes(int qtdNotificacoes) {

        if (qtdNotificacoes > 0) {
            NavigationView navigation = (NavigationView) findViewById(R.id.nav_view);
            Menu menuNav = navigation.getMenu();
            MenuItem element = menuNav.findItem(R.id.nav_notificacoes);
            String before = element.getTitle().toString();

            String counter = Integer.toString(qtdNotificacoes);
            String s = before + "   " + counter + " ";
            SpannableString sColored = new SpannableString(s);

            sColored.setSpan(new BackgroundColorSpan(Color.RED), s.length() - 3, s.length(), 0);
            sColored.setSpan(new ForegroundColorSpan(Color.WHITE), s.length() - 3, s.length(), 0);


            element.setTitle(sColored);
        }

    }


    public void carregaAlertas() {

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
        String url = uri.getHost() + uri.URI_NOTIFICATIONS + "/" + UserControlV2.getInstance().userId;

        //faz o request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            if (!jsonObject.getBoolean("error")) {

                                JSONArray jsonArray = jsonObject.getJSONArray("alertas");

                                setaCrachaNotificacoes(jsonArray.length());

                            } //fim if
                        } catch (JSONException e) {
                            e.printStackTrace();
                            //Toast.makeText(activity, "Ocorreu um erro ao carregar os anúncios: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },//fim listerner json
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Toast.makeText(activity, "Sistema fora do Ar. " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {

            //sobrescrevendo o header
            @Override
            public HashMap<String, String> getParams() {
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
