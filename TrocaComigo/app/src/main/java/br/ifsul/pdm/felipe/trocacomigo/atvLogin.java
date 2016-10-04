package br.ifsul.pdm.felipe.trocacomigo;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class atvLogin extends Activity {

    EditText etEmail;
    EditText etPass;

    Map<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_login);

        etEmail = (EditText) findViewById(R.id.etEmailLogin);
        etPass = (EditText) findViewById(R.id.etPasswordLogin);
    }

    public void btnCreateAccountCLick(View v){

        Intent intent = new Intent(atvLogin.this, atvRegister.class);
        startActivity(intent);

        //finish();

    }

    public void btnLoginClick(View v) {
        final Button btn = (Button) v;
        btn.setEnabled(false);

        //TODO: Criar validacoes para as editTexts username, password e email

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        File cacheDir = new File(this.getCacheDir(), "teste");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

// clear all volley caches.
        queue.add(new ClearCacheRequest(cache, null));

        //RequestQueue queue = Volley.newRequestQueue(this);

        //monta os parametros para passar para o request
        params = new HashMap<String, String>();
        params.put("email", etEmail.getText().toString());
        params.put("senha", etPass.getText().toString());

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_LOGIN_USER;

        //faz o request
        JsonObjectRequest jsonRequest = new JsonObjectRequest(url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        try {
                            //Log.i("Script", URIWebservice.URI_REGISTER_USER);
                            //Log.i("Script", "SUCCES: " + jsonObject.get("message").toString());

                            if (jsonObject.getBoolean("error") == false) {

                                btn.setEnabled(true);
                                //pega o id retornado na insercao do produto
                                int userId = jsonObject.getInt("id");
                                String apiKey = jsonObject.getString("api_key");

                                //chama a outra activity, passando o id do user e a api_key que foi cadastrada
                                Intent intent = new Intent();
                                intent.setClass(atvLogin.this, atvListaProdutos.class);
                                //cria a bundle para passagem de parametros entre as activities
                                Bundle bundle = new Bundle();
                                bundle.putInt("userId", userId);
                                bundle.putString("apiKey", apiKey);

                                UserControlV2.getInstance().setLoginParameters(bundle);
                                UserControlV2.getInstance().username = jsonObject.getString("nomeCompleto");
                                UserControlV2.getInstance().email = jsonObject.getString("email");
                                UserControlV2.getInstance().pathUserPhoto = jsonObject.getString("foto_usuario");
                                UserControlV2.getInstance().latitude = 0;
                                UserControlV2.getInstance().longitude = 0;

                                //insere os parametros na intent
                                intent.putExtras(bundle);
                                //chama a outra tela
                                startActivity(intent);
                            } else {
                                btn.setEnabled(true);
                                Toast.makeText(atvLogin.this, "Ocorreu um erro ao registrar o usuario: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } //fim if
                        catch (JSONException e)
                        {
                            btn.setEnabled(true);
                            e.printStackTrace();
                            Toast.makeText(atvLogin.this, "Ocorreu um erro ao registrar o usuario: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (UserControlV2.MissingUserParamsException e) {
                            e.printStackTrace();
                        }
                    }
                },//fim listerner json
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        btn.setEnabled(true);
                        Toast.makeText(atvLogin.this, "Ocorreu um erro ao registrar o usuario: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        jsonRequest.setTag("tag");
        queue.add(jsonRequest);
    }
}
