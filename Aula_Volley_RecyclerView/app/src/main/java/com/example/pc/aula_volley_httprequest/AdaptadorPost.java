package com.example.pc.aula_volley_httprequest;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by PC on 03/10/2016.
 */

public class AdaptadorPost extends RecyclerView.Adapter<AdaptadorPost.PostViewHolder> {

    private List<Post> posts;
    private Context context;

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(PostViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class PostViewHolder extends RecyclerView.ViewHolder {



        public PostViewHolder(View itemView) {
            super(itemView);
        }
    }
}
