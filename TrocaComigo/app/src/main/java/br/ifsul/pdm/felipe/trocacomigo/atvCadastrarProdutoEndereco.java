package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.ClearCacheRequest;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

public class atvCadastrarProdutoEndereco extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private String estado;

    EditText etEndereco;
    EditText etBairro;
    EditText etCidade;

    Map<String, String> params;

    int idProduto;

    /*
    * Variaveis bkp para guardar os valores iniciais do endereço retornado pelo Geocoder.
    * Caso o usuário altere alguma informação durante o cadastro do endereço, deve ser
    * buscada novamente a latitude e longitude do novo endereço.
    * Obs.: Para que o usuário não aguarde muito para o término do cadastro do produto
    * é melhor efetuar o cadastro sem informar a lat e long, e deixar para buscar estes valores
    * na tela de detalhes do produto, onde as coordenadas são necessárias. Quando as coordenadas
    * forem encontradas, a tela de detalhes do produto submete ao webservice os novos valores
    */

    String bkpEndereco;
    String bkpBairro;
    String bkpCidade;
    String bkpEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_cadastrar_produto_endereco);

        //se não estiver setado o userId, chama a tela de login
        if (UserControlV2.getInstance().userId <= 0){
            //se por algum motivo os parametro de usuário não estiverem presente, chama a tela de login
            Intent iLogin = new Intent();
            iLogin.setClass(this, atvLogin.class);
            startActivity(iLogin);
        }

        Bundle params = getIntent().getExtras();

        idProduto = params.getInt("idProduto");

        estado = "AC";

        etEndereco = (EditText) findViewById(R.id.txtEndereco);
        etBairro = (EditText) findViewById(R.id.txtBairro);
        etCidade = (EditText) findViewById(R.id.txtCidade);


        // ******* Definção dos itens do spinner e suas funcionalidades

        Spinner spinner = (Spinner) findViewById(R.id.spnEstados);
        //cria o adapterpara o spinner
        ArrayAdapter<CharSequence> typesAdapter = ArrayAdapter.createFromResource(this, R.array.estados, R.layout.support_simple_spinner_dropdown_item);
        //define o layout do dropdown
        typesAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        //define o adapter do spinner
        spinner.setAdapter(typesAdapter);

        spinner.setOnItemSelectedListener(this);

        //**** fim definção do spinner


        preencheCamposEndereco(UserControlV2.getInstance().endereco);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_cadastrar_produto_endereco, menu);
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


    public void clickBtnSalvarEndereco(View V)
    {
        //desabilita o botao para nao ocorrer de o usuario chamar duas vezes o cadastro do produto
        final Button b = (Button) V;
        b.setEnabled(false);

        //monta os parametros. Para ser passado ao request
        params = new HashMap<String, String>();
        params.put("endereco", etEndereco.getText().toString());
        params.put("bairro", etBairro.getText().toString());
        params.put("cidade", etCidade.getText().toString());
        params.put("estado", estado);

        //se o usuário não alterou os dados do endereco (retornados pelo geocoder), então passa
        //as coordenadas do GPS para serem guardadas no BD
        if (!mudouEndereco()){
            //Log.i("Script", "TESTE ENDERECO: ENTROU NO IF");
            params.put("latitude", String.valueOf(UserControlV2.getInstance().latitude));
            params.put("longitude", String.valueOf(UserControlV2.getInstance().longitude));
        }

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_PRODUCT_ADDRESS + "/" + idProduto;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(url,
                new JSONObject(params),
                new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {

                            boolean error = jsonObject.getBoolean("error");

                            if (!error) {

                                //chama a outra activity, passando o id do produto que foi cadastrado
                                Intent intent = new Intent();
                                intent.setClass(atvCadastrarProdutoEndereco.this, atvDetalhesProduto.class);
                                //cria a bundle para passagem de parametros entre as activities
                                Bundle params = new Bundle();
                                params.putInt("idProduto", idProduto);
                                params.putBoolean("veioDoCadastro", true);
                                //insere os parametros na intent
                                intent.putExtras(params);
                                //chama a outra tela
                                startActivity(intent);

                            }
                            else{
                                b.setEnabled(true);
                            }

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
                        Toast.makeText(atvCadastrarProdutoEndereco.this, "Ocorreu um erro ao cadastrar o endereço: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
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


    }//fim clickBtnSalvar


    /*
        Método para preencher as editTexts do endereço quando o Geocoder retornou algum valor
         @param String endereco: String com o endereco completo, concatenado (retornado pelo geocoder)
     */
    private void preencheCamposEndereco(String endereco){
        try {

            String[] partesEndereco = endereco.split(System.getProperty("line.separator"));

            if (partesEndereco != null) {

                //o nome da rua está junto com o bairro, deste modo é necessário separar (split) a rua do bairro
                //que estão separados por um hifen " - ". Depois do split, pega a posição zero que é da rua
                String rua = partesEndereco[0].split(" - ")[0];
                String bairro = partesEndereco[0].split(" - ")[1]; //a posicao 1 é referente ao bairro

                //mesma logica da rua e bairro, Cidade e estado na mesma linha, unidos por um hifen " - "
                String cidade = partesEndereco[1].split(" - ")[0];
                String estado = partesEndereco[1].split(" - ")[1].trim();

                etEndereco.setText(rua);
                etBairro.setText(bairro);
                etCidade.setText(cidade);

                Spinner spinner = (Spinner) findViewById(R.id.spnEstados);
                //como não tenho a referencia ao adapter, primeiro pega o adapter do proprio spinner com cast
                //para depois pegar a posição da string do estado
                spinner.setSelection(((ArrayAdapter) spinner.getAdapter()).getPosition(estado));

                //guarda os valores para verificar se o usuário alterou quando submeteu o form
                bkpEndereco = rua;
                bkpBairro = bairro;
                bkpCidade = cidade;
                bkpEstado = estado;
            }

        } catch (NullPointerException e){
            //faz nada
        } catch (IndexOutOfBoundsException e) {
            //faz nada
        }
    }


    private boolean mudouEndereco() {

        try {
            if (!bkpBairro.equals(etBairro.getText().toString()))
                return true;

            if (!bkpCidade.equals(etCidade.getText().toString()))
                return true;

            if (!bkpEstado.equals(estado))
                return true;
        } catch (NullPointerException e) {
            return true;
        }


        //para verificar o endereço (rua/avenida), deve-se desconsiderar o número
        try {
            String[] rua = bkpEndereco.split(",");
            String[] rua2 = etEndereco.getText().toString().split(",");

            //se alterou o nome da rua, avenida, etc...
            if (!rua[0].equals(rua2[0]))
                return true;

            //A verificação do nro da residencia é feita em etapas distintas.
            //o retorno do Geocoder geralmente é um range (xxx-yyy). O usuário pode livremente
            //alterar para o nro real de sua residência (formato xxxx por exemplo)
            if (!rua[1].equals(rua2[1])) {
                //se o usuário alterou o nro da residencia.

                //verifica se o retorno do geocoder foi em formato de range
                if (rua[1].contains("-")) {
                    String[] nroEndereco = rua[1].split("-");

                    int menorNro = Integer.parseInt(nroEndereco[0].trim());
                    int maiorNro = Integer.parseInt(nroEndereco[1].trim());

                    int nroResidencia = 0;
                    try {
                        nroResidencia = Integer.parseInt(rua2[1].trim());
                    } catch (NumberFormatException e) {
                        return true;
                    }

                    //se o nro que foi informado estiver fora do range, retorna true
                    if (nroResidencia < menorNro || nroResidencia > maiorNro)
                        return true;
                }
            }

        } catch (IndexOutOfBoundsException e){
            return true;
        }

        return false;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        estado = (String) adapterView.getSelectedItem();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
