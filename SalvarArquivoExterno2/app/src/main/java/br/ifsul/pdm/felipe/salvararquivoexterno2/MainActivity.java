package br.ifsul.pdm.felipe.salvararquivoexterno2;

import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    String caminhoArmExterno;
    String nomeArq;

    EditText etTextoArquivo;
    EditText etNomeArquivo;
    TextView tvCaminho;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etTextoArquivo = (EditText) findViewById(R.id.etTexto);
        etNomeArquivo = (EditText) findViewById(R.id.etNomeArquivo);

        caminhoArmExterno = Environment.getExternalStorageDirectory().toString();

        tvCaminho = (TextView) findViewById(R.id.txtCaminho);
        tvCaminho.setText(caminhoArmExterno);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void onClickSalvar(View v)
    {
        nomeArq = etNomeArquivo.getText().toString();
        salvarArquivo();
        etTextoArquivo.setText("");
    }

    public void onClickCarregar(View v){
        nomeArq = etNomeArquivo.getText().toString();
        carregarArquivo();
    }

    private void salvarArquivo()
    {
        File arq;
        byte[] dados;
        try{
            arq = new File(caminhoArmExterno, nomeArq);
            FileOutputStream fos;
            dados = etTextoArquivo.getText().toString().getBytes();
            fos = new FileOutputStream(arq);
            fos.write(dados);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarArquivo()
    {
        File arq;
        String lstrlinha;
        try{
            etTextoArquivo.setText("");
            arq = new File(caminhoArmExterno, nomeArq);
            BufferedReader br = new BufferedReader(new FileReader(arq));
            while ((lstrlinha = br.readLine()) != null) {
                if (!etTextoArquivo.getText().toString().equals("")) {
                    etTextoArquivo.append("\n");
                }
                etTextoArquivo.append(lstrlinha);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void criaToast(String msg){
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void imprimeTrace(String msg){
        criaToast(msg);
    }
}
