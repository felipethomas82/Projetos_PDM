package com.example.pc.aula_volley_httprequest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by PC on 03/10/2016.
 */

public class AdaptadorPost extends RecyclerView.Adapter<AdaptadorPost.PostViewHolder> {

    private List<Post> posts;
    private Context context;

    public AdaptadorPost(List<Post> posts, Context context) {
        this.posts = posts;
        this.context = context;
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //preciamos criar uma view para que o nosso código funcione
        View view = LayoutInflater.from(context).inflate(R.layout.post, parent, false);
        PostViewHolder holder = new PostViewHolder(view); //passa a view que foi instanciada
        return holder;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

        //pega o post na posição da lista
        Post post = posts.get(position);
        //seta os valores dos elementos de interface
        holder.tvTitulo.setText(post.getTitulo());
        holder.tvSubtitulo.setText(post.getSubtitulo());
        holder.tvUsuario.setText(post.getUsuario());
        holder.tvTexto.setText(post.getTexto());

    }

    @Override
    public int getItemCount() {
        return posts.size();
        //return 0;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {

        final ImageView ivFoto;
        final TextView tvTitulo;
        final TextView tvSubtitulo;
        final TextView tvUsuario;
        final TextView tvTexto;

        public PostViewHolder(View itemView) {
            super(itemView);

            //instancia os elementos de interface
            ivFoto = (ImageView) itemView.findViewById(R.id.ivPost);
            tvTitulo = (TextView) itemView.findViewById(R.id.tvTitulo);
            tvSubtitulo = (TextView) itemView.findViewById(R.id.tvSubtitulo);
            tvUsuario = (TextView) itemView.findViewById(R.id.tvUsuario);
            tvTexto = (TextView) itemView.findViewById(R.id.tvTextoPost);
        }
    }
}
