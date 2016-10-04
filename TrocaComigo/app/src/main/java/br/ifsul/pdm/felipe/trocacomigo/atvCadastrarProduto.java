package br.ifsul.pdm.felipe.trocacomigo;

import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
//import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;


public class atvCadastrarProduto extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    EditText etTitulo;
    EditText etDescricao;
    EditText etPreco;
    EditText etAceitaTrocar;
    RadioGroup rgTipoAnuncio;

    Map<String, String> params;

    TextView tvTeste;

    String tipoAnuncio;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_cadastrar_produto);

        etTitulo = (EditText) findViewById(R.id.txtTitulo);
        etDescricao = (EditText) findViewById(R.id.txtDescricao);
        etPreco = (EditText) findViewById(R.id.txtPreco);
        etAceitaTrocar = (EditText) findViewById(R.id.txtAceitaTrocar);
        rgTipoAnuncio = (RadioGroup) findViewById(R.id.radio_group_anuncio);
        tvTeste = (TextView) findViewById(R.id.txtTeste);

        RadioButton rbTroca = (RadioButton) findViewById(R.id.radio_troca);
        rbTroca.setChecked(true);
        tipoAnuncio = "Troca";
        tvTeste.setText("Hey. Psiu. A troca pode ser por dinheiro também ;)");

        //pega os paramtros passados pela outra activity
        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        tvTeste = (TextView) findViewById(R.id.txtTeste);
        //tvTeste.setText(userControl.getApiKey() + " - id: " + String.valueOf(userControl.getUserId()));

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Inicia o service (geocoder) para buscar o endereço do usuário, pois quando chegar na tela onde deve-se
        //informar o endereço, o service já deve ter retornado os valores. Para funcionar, deve estar setada a localização
        //do user na classe UserControlV2
        MeuGeocoder mGeocoder = new MeuGeocoder(this);
        mGeocoder.buscaEndereco();

        // ******* Definção dos itens do spinner e suas funcionalidades

        /* Spinner desabilitado em troca do radioGroup


        Spinner spinner = (Spinner) findViewById(R.id.tiposAnuncio);
        //cria o adapterpara o spinner
        ArrayAdapter<CharSequence> typesAdapter = ArrayAdapter.createFromResource(this, R.array.types, R.layout.support_simple_spinner_dropdown_item);
        //define o layout do dropdown
        typesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        //define o adapter do spinner
        spinner.setAdapter(typesAdapter);

        spinner.setOnItemSelectedListener(this);

        //**** fim definção do spinner
        */

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_cadastrar_produto, menu);
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


    public void clickBtnSalvar(View V)
    {
        //desabilita o botao para nao ocorrer de o usuario chamar duas vezes o cadastro do produto
        final Button b = (Button) V;
        b.setEnabled(false);

        //flag para identificar quando um erro ocorreu nas validações
        boolean erro = false;

        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        double preco = 0;
        try {
            preco = numberFormat.parse(etPreco.getText().toString()).doubleValue();
        } catch (ParseException e) {
            e.printStackTrace();
            TextInputLayout tilEtPreco = (TextInputLayout) findViewById(R.id.til3);
            tilEtPreco.setErrorEnabled(true);
            tilEtPreco.setError("Informe um valor válido");
            b.setEnabled(true);
            erro = true;
        } catch (NumberFormatException e) {
            TextInputLayout tilEtPreco = (TextInputLayout) findViewById(R.id.til3);
            tilEtPreco.setErrorEnabled(true);
            tilEtPreco.setError("Informe um valor válido");
            b.setEnabled(true);
            erro = true;
        }

        if (etTitulo.getText().toString().equals("")) {
            TextInputLayout tilTitulo = (TextInputLayout) findViewById(R.id.til);
            tilTitulo.setErrorEnabled(true);
            tilTitulo.setError("Um anúncio sem título ficará no limbo dos produtos sem interessados");
            b.setEnabled(true);
            erro = true;
        }

        if (etDescricao.getText().toString().equals("")) {
            TextInputLayout tilDescricao = (TextInputLayout) findViewById(R.id.til2);
            tilDescricao.setErrorEnabled(true);
            tilDescricao.setError("Ahh, vamos lá, você pode ser mais criativo! Descreva seu produto. :P");
            b.setEnabled(true);
            erro = true;
        }

        //se ocorreu um erro nas validações, saí do método
        if (erro)
            return;

        //monta os parametros. Para ser passado ao request
        params = new HashMap<String, String>();
        params.put("titulo", etTitulo.getText().toString());
        params.put("descricao", etDescricao.getText().toString());
        params.put("preco", Double.toString(preco));
        params.put("aceitaTrocar", etAceitaTrocar.getText().toString());
        params.put("tipo", tipoAnuncio);


        //codigo para limpar o cache
        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        File cacheDir = new File(this.getCacheDir(), "teste");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        // limpa cache
        queue.add(new ClearCacheRequest(cache, null));

        //instancia a RequesQueue
        //RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://10.0.3.2/trocacomigo/produtos";

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_PRODUCTS;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(url,
                new JSONObject(params),
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            //pega o id retornado na insercao do produto
                            int idProduto = jsonObject.getInt("product_id");

                            //chama a outra activity, passando o id do produto que foi cadastrado
                            Intent intent = new Intent();
                            intent.setClass(atvCadastrarProduto.this, atvCadastrarProdutoFotos.class);
                            //cria a bundle para passagem de parametros entre as activities
                            Bundle params = new Bundle();
                            params.putInt("idProduto", idProduto);
                            params.putString("apiKey", UserControlV2.getInstance().apiKey);
                            params.putInt("userId", UserControlV2.getInstance().userId);
                            //insere os parametros na intent
                            intent.putExtras(params);
                            //chama a outra tela
                            startActivity(intent);

                            //Log.i("Script", "SUCCES: "+ jsonObject.get("idProduto").toString());
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
                        Toast.makeText(atvCadastrarProduto.this, "Ocorreu um erro ao cadastrar o produto: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
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



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch ((String)adapterView.getSelectedItem())
        {
            case "Troca": tvTeste.setText("Hey. Psiu. A troca pode ser por dinheiro também ;)");
                tipoAnuncio = "Troca";
                break;
            case "Aluguel": tvTeste.setText("Aluguel é ótimo. Sempre dá para fazer uma graninha extra com o que está parado.");
                tipoAnuncio = "Aluguel";
                break;
            case "Doação": tvTeste.setText("Eba. Como é bom o mundo poder contar com pessoas como você.");
                tipoAnuncio = "Doação";
                break;
        }


    }

    public void onRadioButtonClicked(View v){
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()){
            case R.id.radio_troca:
                if (checked) {
                    tvTeste.setText("Hey. Psiu. A troca pode ser por dinheiro também ;)");
                    tipoAnuncio = "Troca";
                    etAceitaTrocar.setText("");
                    etAceitaTrocar.setEnabled(true);
                    etPreco.setText("");
                    etPreco.setEnabled(true);
                }
                break;
            case R.id.radio_aluguel:
                if (checked) {
                    tvTeste.setText("Aluguel é ótimo. Sempre dá para fazer uma graninha extra com o que está parado.");
                    tipoAnuncio = "Aluguel";
                    etAceitaTrocar.setText(" ");
                    etAceitaTrocar.setEnabled(false);
                    etPreco.setText("");
                    etPreco.setEnabled(true);
                }
                break;
            case R.id.radio_doacao:
                if (checked) {
                    tvTeste.setText("Eba. Como é bom o mundo poder contar com pessoas como você.");
                    tipoAnuncio = "Doação";
                    etAceitaTrocar.setText("Não é preciso ofertar nada em troca, estou doando.");
                    etAceitaTrocar.setEnabled(false);
                    NumberFormat numberFormat = NumberFormat.getNumberInstance();
                    etPreco.setText(numberFormat.format(0.00));
                    etPreco.setEnabled(false);
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
