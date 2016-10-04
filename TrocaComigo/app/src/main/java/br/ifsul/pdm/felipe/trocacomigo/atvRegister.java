package br.ifsul.pdm.felipe.trocacomigo;

import android.app.ActionBar;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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


public class atvRegister extends ActionBarActivity {

    EditText etUsername;
    EditText etEmail;
    EditText etPass1;
    EditText etPass2;

    Map<String, String> params;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_register);

        etUsername = (EditText) findViewById(R.id.etUsernameRegister);
        etEmail = (EditText) findViewById(R.id.etEmailRegister);
        etPass1 = (EditText) findViewById(R.id.etPasswordRegister);
        etPass2 = (EditText) findViewById(R.id.etPassword2Register);

        Toolbar actionBar = (Toolbar) findViewById(R.id.actionBar);
        setSupportActionBar(actionBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_register, menu);
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

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Definição do Host");
            alert.setMessage("Informe o host abaixo e clique em OK");

            // Cria uma EditText para que o usuario possa inserir a informaçcao
            final EditText input = new EditText(this);
            final URIWebservice uri = new URIWebservice(atvRegister.this);
            input.setText(uri.getHost());
            alert.setView(input);

            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    //pega o valor digitado na alertdialog
                    String value = input.getText().toString();
                    uri.setHost(value, atvRegister.this);
                    //Toast.makeText(atvRegister.this, value, Toast.LENGTH_LONG).show();
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void btnRegisterClick(View v) {
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
        params.put("login", etUsername.getText().toString());
        params.put("email", etEmail.getText().toString());
        params.put("senha", etPass1.getText().toString());

        URIWebservice uri = new URIWebservice(this);
        String url = uri.getHost() + uri.URI_REGISTER_USER;

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
                                //pega o id retornado na insercaoo do produto
                                int idUser = jsonObject.getInt("inserted_id");
                                String api_key = jsonObject.getString("api_key");

                                //chama a outra activity, passando o id do user e a api_key que foi cadastrada
                                Intent intent = new Intent();
                                intent.setClass(atvRegister.this, atvListaProdutos.class);
                                //cria a bundle para passagem de parametros entre as activities
                                Bundle bundle = new Bundle();
                                bundle.putInt("userId", idUser);
                                bundle.putString("apiKey", api_key);
                                //insere os parametros na intent
                                intent.putExtras(bundle);
                                //chama a outra tela
                                startActivity(intent);
                            } else {
                                btn.setEnabled(true);
                                Toast.makeText(atvRegister.this, "Ocorreu um erro ao registrar o usuário: " + jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } //fim if
                        catch (JSONException e)
                        {
                            btn.setEnabled(true);
                            e.printStackTrace();
                            Toast.makeText(atvRegister.this, "Ocorreu um erro ao registrar o usuário: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                },//fim listerner json
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        btn.setEnabled(true);
                        Toast.makeText(atvRegister.this, "Ocorreu um erro ao registrar o usuário: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        jsonRequest.setTag("tag");
        queue.add(jsonRequest);
    }
}
