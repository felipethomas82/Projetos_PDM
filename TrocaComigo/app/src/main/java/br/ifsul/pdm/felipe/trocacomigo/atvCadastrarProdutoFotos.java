package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

//import org.apache.http.HttpResponse;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicHeader;
//import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class atvCadastrarProdutoFotos extends ActionBarActivity {

    TextView teste;
    int idProduto;

    ArrayList<Photo> photos;
    private int photosQuantity;

    public static final int CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE = 100;
    public static final int SHOW_FULL_IMAGE_REQUEST_CODE = 200;
    public static final int SELECT_PHOTO_CODE = 300;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atv_cadastrar_produto_fotos);

        context = this;

        //teste = (TextView) findViewById(R.id.txtViewMensagemAtvFoto);

        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        idProduto = params.getInt("idProduto");

        //array de objetos que vai conter os nomes dos arquivos das fotos que vao ser transmitidas ao webservice
        photos = new ArrayList<Photo>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_atv_cadastrar_produto_fotos, menu);
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

    public void btnTirarFotoClick(View v)
    {
        //o nome da foto eh setado de acordo com o nro de elementos da arrayList (foto0 foto1 foto2, etc...)
        String photoName = "foto" + photos.size() + ".jpg";
        //cria um arquivo vazio para que o app da camera possa inserir a foto no arquivo
        File foto = new File(Photo.PICS_DIR, photoName);

        //intent para usar o aplicativo nativo da camera
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //a intent ira gravar a foto direto no arquivo que foi passado como parametro
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(foto));
        startActivityForResult(intent, CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        //verifica se o request code que retornou corresponde ao capture_image...
        if (requestCode == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE || requestCode == SELECT_PHOTO_CODE)
        {
            if (resultCode == RESULT_OK) {
                ImageView ivCamera = (ImageView) findViewById(R.id.ivCameraAtvFotos);
                ivCamera.setImageBitmap(null);

                Bitmap bp;
                File file = null;

                try {
                    //foto da galeria. pega o path da foto
                    if (requestCode == SELECT_PHOTO_CODE){
                            // Pega a imagem do data
                            Uri selectedImage = data.getData();
                            String[] filePathColumn = { MediaStore.Images.Media.DATA };

                            // Instancia um cursor
                            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                            // Move para a primeira linha
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String imgDecodableString = cursor.getString(columnIndex);
                            cursor.close();
                            //cria um arquivo a partir da foto selecionada
                            file = new File(imgDecodableString);
                        }
                    } catch (Exception e) {
                        Toast.makeText(this, "Ops. Algo deu errado. Tente novamente.", Toast.LENGTH_LONG).show();
                    }

                try {
                    //foto da camera. pega o path da foto
                    if (requestCode  == CAPTURE_IMAGE_FULLSIZE_ACTIVITY_REQUEST_CODE) {
                        String photoName = "foto" + photos.size() + ".jpg";
                        //cria um arquivo a partir da foto que foi tirada pela camera
                        file = new File(Photo.PICS_DIR, photoName);
                    }
                } catch (Exception e) {
                    Toast.makeText(this, "Ops. Algo deu errado. Tente novamente.", Toast.LENGTH_LONG).show();
                }

                bp = Photo.decodeSampledBitmapFromFile(file.getAbsolutePath(), 200, 200);

                //cria um novo objeto para informar o path da foto, e seta o atributo capa como false
                Photo photo = new Photo(file.getAbsolutePath(), false);
                //se for a primeira a foto, altera cover (capa) para true.
                if (photos.size() == 0)
                    photo.setCover(true);

                photos.add(photo);


                //cropa a imagem para o formato quadrado
                bp = Photo.drawBitmapRectangle(bp);

                Resources res = getResources();
                //transforma para um circulo o bitmap
                RoundedBitmapDrawable dr = RoundedBitmapDrawableFactory.create(res, bp);
                dr.setCornerRadius(Math.min(dr.getMinimumWidth(), dr.getMinimumHeight()));

                // ****** CRIA E ADICIONA UM LAYOUT VERTICAL COM UMA IMAGEVIEW E UMA TEXTVIEW DENTRO DELE
                // E ADICIONA AO LAYOUT JA EXISTENTE DAS FOTOS.

                //TODO: Fotos pequenas criam um circulo pequeno

                //cria a imageview para ser adicionada ao layout
                ImageView ivPhoto = new ImageView(this);
                ivPhoto.setImageDrawable(dr);
                ivPhoto.setTag(photos.get(photos.size()-1).getPathPhoto()); //seta a tag com o path para ser usada posteriormente
                //pega o layout onde vai ser inserida a imageview
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layPhotosAtvRegister);

                //cria um layout vertical
                LinearLayout llVertical = new LinearLayout(this);
                llVertical.setOrientation(LinearLayout.VERTICAL);

                //cria os parametros para a imageview
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 10, 0); //margin right

                llVertical.addView(ivPhoto, layoutParams);

                //textview "capa". Cria os parametros de layout e a textView e adiciona o texto capa se a imagem for a capa
                LinearLayout.LayoutParams txtParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                txtParams.gravity = Gravity.CENTER;
                TextView txtCover = new TextView(this);
                //verifica se a ultima foto eh a capa ou nao
                if (photos.get(photos.size()-1).isCover())
                    txtCover.setText("Capa");
                llVertical.addView(txtCover, txtParams);

                //adiciona o layout com a iv e o tv no layout principal da activity
                linearLayout.addView(llVertical);

                ivPhoto.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ImageView iv = (ImageView) v;
                        String photoName = iv.getTag().toString();
                        Intent intent = new Intent();
                        intent.setClass(atvCadastrarProdutoFotos.this, atvMostrarFotoFull.class);
                        Bundle params = new Bundle();
                        params.putString("photoName", photoName);
                        intent.putExtras(params);
                        startActivityForResult(intent, SHOW_FULL_IMAGE_REQUEST_CODE);
                    }
                });

                /******************** FIM DO NOVO LAYOUT *****************/

            } //fim if resultCode
        } // fim if requestcode


        //retorno da activity atvMostrarFotoFull, remover ou definir capa
        if (requestCode == SHOW_FULL_IMAGE_REQUEST_CODE){
            if (resultCode == RESULT_OK){

                String action = data.getStringExtra("action");
                String photoName = data.getStringExtra("photoName");
                switch (action){
                    case "setCover": setCover(photoName);
                        break;
                    case "removePhoto": removePhoto(photoName);
                        break;
                }
            }
        }

    }


    private void removePhoto(String photoName)
    {
        //percorre a arraylist de photos e quando encontrar o objeto com o atributo pathPhoto igual ao
        //que foi passado como parametro, remove ele
        for (int i=0; i<photos.size(); i++){
            if (photoName.equals(photos.get(i).getPathPhoto())){
                photos.remove(i);
            }
        }

        //pega o layout onde estao as imageViews
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layPhotosAtvRegister);

        //percorre o layout ateh encontrar a imageView com a tag igual ao photoName
        for (int i=0; i<linearLayout.getChildCount(); i++)
        {
            LinearLayout layoutChild = (LinearLayout) linearLayout.getChildAt(i);
            if (layoutChild.getChildAt(0).getTag().toString().equals(photoName)){
                linearLayout.removeViewAt(i);
            }
        }
    }


    private void setCover(String photoName)
    {
        //percorre a arraylist de photos e quando encontrar o objeto com o atributo pathPhoto igual ao
        //que foi passado como parametro, seta a capa como true
        for (int i=0; i<photos.size(); i++){
            if (photoName.equals(photos.get(i).getPathPhoto())){
                photos.get(i).setCover(true);
            }
        }

        //pega o layout onde estao as imageViews
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layPhotosAtvRegister);

        //percorre o layout ateh encontrar a imageView com a tag igual ao photoName
        for (int i=0; i<linearLayout.getChildCount(); i++)
        {
            LinearLayout layoutChild = (LinearLayout) linearLayout.getChildAt(i);
            TextView txtCover = (TextView) layoutChild.getChildAt(1);
            if (layoutChild.getChildAt(0).getTag().toString().equals(photoName)){
                txtCover.setText("Capa");
            }
            else {
                txtCover.setText("");
            }
        }
    }


    //click do botao selecionar foto da galeria
    public void btnSelectImageFromGallery(View v){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/");
        startActivityForResult(intent,SELECT_PHOTO_CODE);
    }


    //com o click do botao publicar foto, eh feito o upload das fotos ao server
    //as fotos sao enviadas em string codificadas em base64
    public void btnPublicarProduto(View v){

        HttpStack stack = new HurlStack();
        Network network = new BasicNetwork(stack);
        File cacheDir = new File(this.getCacheDir(), "teste");
        DiskBasedCache cache = new DiskBasedCache(cacheDir);
        RequestQueue queue = new RequestQueue(cache, network);
        queue.start();

        // clear all volley caches.
        queue.add(new ClearCacheRequest(cache, null));

        photosQuantity = photos.size();
        //instancia a RequesQueue
        //RequestQueue queue = Volley.newRequestQueue(this);
        Map<String, String> fotos = new HashMap<String, String>();

        //caso o usuario tenha escolhido ou tirado alguma foto, faz o request e envia o arquivo
        if (photos.size() > 0) {
            for (Photo photo : photos) {
                //pega o primeiro path da foto que esta na arraylist. cada chamada do metodo envia um arquivo por vez
                File file = new File(photo.getPathPhoto());
                Bitmap bitmap = Photo.decodeSampledBitmapFromFile(file.getAbsolutePath(), 1000, 700);

                //converte a foto para byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                //foto = stream.toByteArray();

                //converte os bytes da foto para uma string base64
                //String sFoto = Base64.encodeToString(foto,Base64.DEFAULT);
                String sFoto = Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT);

                //TODO: Descobrir porque a foto marcada como capa não está sendo setada no PHP ou BD
                fotos.put("foto", sFoto);
                fotos.put("capa", String.valueOf(photo.isCover()));
                fotos.put("id", String.valueOf(idProduto));

                URIWebservice uri = new URIWebservice(context);
                String url = uri.getHost() + uri.URI_PRODUCT_PHOTOS + "/:" + idProduto;

                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.POST,
                        url,
                        new JSONObject(fotos),
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject jsonObject) {
                                photosQuantity--;

                                //se não houver mais fotos para serem enviadas, chama a outra activity
                                if (photosQuantity == 0) {
                                    chamaActivity();
                                }
                                //Log.i("Script", "SUCCES: " + jsonObject.toString());
                            }
                        },//fim Listener<JSONObject>
                        new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                //b.setEnabled(true);
                                Toast.makeText(atvCadastrarProdutoFotos.this, "Ocorreu um erro ao cadastrar o produto: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("Script", "ERRO:" + volleyError.getMessage());
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
        }//fim do if
        else {

            //se o usuário não tirou nenhuma foto, vai pra outra activity
            chamaActivity();

        } //fim do else
    }



    private void chamaActivity(){
        //chama a outra activity, passando o id do produto que foi cadastrado
        Intent intent = new Intent();
        intent.setClass(atvCadastrarProdutoFotos.this, atvCadastrarProdutoEndereco.class);

        //cria a bundle para passagem de parametros entre as activities
        Bundle params = new Bundle();
        params.putInt("idProduto", idProduto);

        //insere os parametros na intent
        intent.putExtras(params);
        //chama a outra tela
        startActivity(intent);
    }

}
