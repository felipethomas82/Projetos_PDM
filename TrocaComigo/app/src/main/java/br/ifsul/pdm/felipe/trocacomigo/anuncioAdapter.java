package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by PC on 06/07/2015.
 */
public class anuncioAdapter extends RecyclerView.Adapter<anuncioAdapter.AnuncioViewHolder> {

    private List<Anuncio> anuncios;
    private Context context;

    private int lastPosition = -1;

    anuncioAdapter thisAnuncioAdapter = this;

    public static class AnuncioViewHolder extends RecyclerView.ViewHolder {

        public TextView vNomeUsuario;
        public TextView vValorProduto;
        public NetworkImageView vFotoCapa;
        public TextView vTituloAnuncio;
        public TextView vTipoAnuncio;
        public TextView vDataAnuncio;
        public TextView vDistancia;

        public CircularNetworkImageView vFotoUsuario;


        public AnuncioViewHolder(View itemView) {
            super(itemView);

            vNomeUsuario = (TextView) itemView.findViewById(R.id.tvNomeUsuarioAnuncio);
            vValorProduto = (TextView) itemView.findViewById(R.id.txtValorProdutoAnuncio);
            vFotoCapa = (NetworkImageView) itemView.findViewById(R.id.imgCapaAnuncio);
            vTituloAnuncio = (TextView) itemView.findViewById(R.id.tvTituloAlerta);
            vFotoUsuario = (CircularNetworkImageView) itemView.findViewById(R.id.imgFotoUsuarioAnuncio);
            vTipoAnuncio = (TextView) itemView.findViewById(R.id.tvTipoAnuncio);
            vDataAnuncio = (TextView) itemView.findViewById(R.id.tvDataAnuncio);
            vDistancia = (TextView) itemView.findViewById(R.id.tvDistancia);
        }
    }

    public anuncioAdapter(List<Anuncio> anuncios, Context context) {
        this.anuncios = anuncios;
        this.context = context;
    }

    @Override
    public anuncioAdapter.AnuncioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.anuncio, parent, false);

        return new AnuncioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(anuncioAdapter.AnuncioViewHolder holder, int position) {
        Anuncio anuncio = anuncios.get(position);

        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        holder.vNomeUsuario.setText(anuncio.nomeUsuario);
        holder.vValorProduto.setText(numberFormat.format(anuncio.valor));
        holder.vTituloAnuncio.setText(anuncio.titulo);
        holder.vTipoAnuncio.setText(anuncio.tipoAuncio);
        holder.vDataAnuncio.setText(anuncio.dataAnuncio);
        if (anuncio.distancia < 100)
            holder.vDistancia.setText(anuncio.distancia.toString() + " Km");
        else
            holder.vDistancia.setText("+100 Km");

        ImageLoader.ImageCache imageCacheFotoUsuario = new BitmapLruCache();
        ImageLoader imageLoaderFotousuario = new ImageLoader(newRequestQueue(context), imageCacheFotoUsuario);
        holder.vFotoUsuario.setImageUrl(anuncio.fotoUsuario, imageLoaderFotousuario);

        ImageLoader.ImageCache imageCache = new BitmapLruCache();
        ImageLoader imageLoader = new ImageLoader(newRequestQueue(context), imageCache);
        holder.vFotoCapa.setImageUrl(anuncio.pathFotoCapa, imageLoader);
        holder.vFotoCapa.setTag(position);

        holder.vFotoCapa.setOnClickListener(new View.OnClickListener(){

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

        setAnimation(holder.itemView, position);

    }

    @Override
    public int getItemCount() {
        return anuncios.size();
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


    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        //if (position > lastPosition)
        //{
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        //}
    }

}
