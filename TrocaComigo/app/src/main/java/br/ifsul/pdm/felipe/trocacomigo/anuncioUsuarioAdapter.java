package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;

/**
 * Created by PC on 06/07/2015.
 */
public class anuncioUsuarioAdapter extends RecyclerView.Adapter<anuncioUsuarioAdapter.AnuncioViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;

    anuncioUsuarioAdapter thisAnuncioAdapter = this;

    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        public TextView vValorProduto;
        public NetworkImageView vFotoCapa;
        public TextView vTituloAnuncio;
        public TextView vTipoAnuncio;
        public TextView vDataAnuncio;
        public TextView vStatus;
        public Button btnAtivo;
        public Button btnVendido;



        public AnuncioViewHolder(View itemView) {
            super(itemView);

            vValorProduto = (TextView) itemView.findViewById(R.id.txtValorProdutoAnuncio);
            vFotoCapa = (NetworkImageView) itemView.findViewById(R.id.imgCapaAnuncio);
            vTituloAnuncio = (TextView) itemView.findViewById(R.id.tvTituloAlerta);
            vTipoAnuncio = (TextView) itemView.findViewById(R.id.tvTipoAnuncio);
            vDataAnuncio = (TextView) itemView.findViewById(R.id.tvDataAnuncio);
            vStatus = (TextView) itemView.findViewById(R.id.tvStatus);

            btnAtivo = (Button) itemView.findViewById(R.id.btnDesativarAnuncio);
            btnVendido = (Button) itemView.findViewById(R.id.btnInformarVenda);
        }
    }

    public anuncioUsuarioAdapter(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @Override
    public anuncioUsuarioAdapter.AnuncioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.anuncio_usuario, parent, false);

        return new AnuncioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final anuncioUsuarioAdapter.AnuncioViewHolder holder, final int position) {
        final Anuncio anuncio = anuncios.get(position);

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        holder.vValorProduto.setText(numberFormat.format(anuncio.valor));
        holder.vTituloAnuncio.setText(anuncio.titulo);
        holder.vTipoAnuncio.setText(anuncio.tipoAuncio);
        holder.vDataAnuncio.setText("Data cadastro: " + anuncio.dataAnuncio);

        trocaTextoStatus(anuncio, holder);

        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        ImageLoader imageLoader = new ImageLoader(newRequestQueue(context), imageCache);
        holder.vFotoCapa.setImageUrl(anuncio.pathFotoCapa, imageLoader);
        holder.vFotoCapa.setTag(position);

        holder.vFotoCapa.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                int pos = Integer.valueOf(view.getTag().toString());
                Anuncio anuncio1 = anuncios.get(pos);
                Intent intent = new Intent(view.getContext(), atvDetalhesProduto.class);
                Bundle params = new Bundle();
                params.putInt("userId", UserControlV2.getInstance().userId);
                params.putString("apiKey", UserControlV2.getInstance().apiKey);
                params.putInt("idProduto", anuncio1.id);
                intent.putExtras(params);
                view.getContext().startActivity(intent);
                // Toast.makeText(context, anuncio1.titulo, Toast.LENGTH_LONG).show();
            }
        });

        holder.btnAtivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //o holder e o position devem ser passados como parametros, para que as informações
                //na tela do usuário possam ser trocadas sem que haja a necessidade de recarregar
                //tudo novamente, ou o usuário enxergaria a atualização somente após sair e entrar na tela novamente
                if (anuncio.ativo) //se o anuncio estiver ativo, apenas desativa
                    clickSaveStatus(anuncio.id, !anuncio.ativo, anuncio.vendido, holder, position);
                else //se estiver desativado, quando ativer marca como não vendido
                    clickSaveStatus(anuncio.id, !anuncio.ativo, false, holder, position);
            }
        });

        holder.btnVendido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //quando o produto for vendido, deve ser setado a propriedade "ativo" como false,
                //para que o anúncio não apareça mais nas buscas
                clickSaveStatus(anuncio.id, false, true, holder, position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return anuncios.size();
    }



    //neste método, os textos das Textviews e dos buttons são alterados de acordo com o status
    //do anuncio, se foi vendido e se está ativo ainda
    private void trocaTextoStatus(Anuncio anuncio, anuncioUsuarioAdapter.AnuncioViewHolder holder) {
        //se o produto não estiver mais ativo, verifica se foi por ter sido vendido ou foi
        //desativado pelo usuário (sem a devida venda)
        if (!anuncio.ativo) {
            if (anuncio.vendido) {
                holder.vStatus.setText("Vendido");
                holder.btnVendido.setText("Marcar como não vendido");
                holder.btnAtivo.setText("Reativar anúncio");
            }
            else {
                holder.vStatus.setText("Desativado");
                holder.btnVendido.setText("Marcar como vendido");
                holder.btnAtivo.setText("Reativar anúncio");
            }
        } else {
            holder.vStatus.setText("Ativo");
            holder.btnVendido.setText("Marcar como vendido");
            holder.btnAtivo.setText("Desativar anúncio");
        }
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



    /*
        Método para mudar o status de ativo e vendido do produto.

        o holder e o position devem ser passados como parametros, para que as informações
        na tela do usuário possam ser trocadas sem que haja a necessidade de recarregar
        tudo novamente, ou o usuário enxergaria a atualização somente após sair e entrar na tela novamente
     */
    public void clickSaveStatus(int idProduto, final boolean ativo, final boolean vendido, final anuncioUsuarioAdapter.AnuncioViewHolder holderAnuncio, final int positionAnuncio)
    {
        final int ativoInt = ativo ? 1 : 0; //o valor que deve ser passado ao backend deve ser 0 ou 1
        final int vendidoInt = vendido ? 1 : 0;

        URIWebservice uri = new URIWebservice(context);
        String url = uri.getHost() + uri.URI_PRODUCT_STATUS + "/" + idProduto;

        StringRequest putRequest = new StringRequest(Request.Method.PUT,
                url,
                new Response.Listener<String>(){

                    @Override
                    public void onResponse(String response) {

                        //Log.i("Script", "SUCCES: "+ jsonObject.get("idProduto").toString());
                        try {
                            JSONObject respostaJson = new JSONObject(response);

                            //atualiza a tela do usuário informando o novo status
                            Anuncio anuncioAtualizado = anuncios.get(positionAnuncio);
                            anuncioAtualizado.ativo = ativo;
                            anuncioAtualizado.vendido = vendido;

                            trocaTextoStatus(anuncioAtualizado, holderAnuncio);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(context, "Ops! Pedimos desculpa, mas ocorreu um erro ao alterar o status do produto.", Toast.LENGTH_SHORT).show();
                        }
                    }


                },//fim Listener<JSONObject>
                new Response.ErrorListener(){

                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Toast.makeText(context, "Ocorreu um erro ao alterar o status do produto: " + volleyError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){
            //sobrescrevendo o header
            @Override
            public HashMap<String, String> getParams(){
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("Authorization", UserControlV2.getInstance().apiKey);
                params.put("User_id", String.valueOf(UserControlV2.getInstance().userId));
                params.put("ativo", String.valueOf(ativoInt));
                params.put("vendido", String.valueOf(vendidoInt));

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
        Cache cache = new DiskBasedCache(context.getCacheDir(), 1024 * 1024); // 1MB cap
        // setup da rede.
        Network network = new BasicNetwork(new HurlStack());
        // instancia a requestQueue com o cache e a rede
        mRequestQueue = new RequestQueue(cache, network);
        // antes de adicionar o request, deve-se startar a queu
        mRequestQueue.start();

        //define que a reposta não sera enviada ao cache
        putRequest.setShouldCache(false);
        mRequestQueue.add(putRequest);

        //todos os comandos acima poderiam ser substituidos pela classe singleton abaixo, contudo,
        // estava estourando a memoria

        // VolleySingleton.getInstance().addToRequestQueue(jsonRequest, "reqEndereco");


    }//fim clickBtnSalvar

}
