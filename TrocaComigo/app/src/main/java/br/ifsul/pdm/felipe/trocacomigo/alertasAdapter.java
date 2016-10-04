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
public class alertasAdapter extends RecyclerView.Adapter<alertasAdapter.AlertaViewHolder> {

    private List<Alerta> alertas;
    private Context context;

    public static class AlertaViewHolder extends RecyclerView.ViewHolder {

        public TextView tvTitulo;
        public TextView tvMensagem;


        public AlertaViewHolder(View itemView) {
            super(itemView);

            tvTitulo = (TextView) itemView.findViewById(R.id.tvTituloAlerta);
            tvMensagem = (TextView) itemView.findViewById(R.id.tvTextoNotificacao);
        }
    }

    public alertasAdapter(List<Alerta> alertas, Context context) {
        this.alertas = alertas;
        this.context = context;
    }

    @Override
    public alertasAdapter.AlertaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.alerta, parent, false);

        return new AlertaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final alertasAdapter.AlertaViewHolder holder, final int position) {
        final Alerta alerta = alertas.get(position);

        holder.tvTitulo.setText(alerta.tituloAnuncio);

        //se o alerta que foi gerado é um comentário e o dono é o próprio usuário, então a mensagem
        //é porque tem interessado em um produto dele. Se não for o dono, pode comentário de outro
        //usuario no mesmo produto em que ele tem interesse
        if (alerta.tipoAlerta.equals("lista_desejo")){
            holder.tvMensagem.setText("Heyy. Achamos este produto que está na sua lista de desejo.");
        } else {

            if (alerta.dono == UserControlV2.getInstance().userId) {
                holder.tvMensagem.setText("Psiu. Tem gente interessada neste produto. Deixaram um comentário nele." +
                        " Não deixe os interessados no produto sem uma resposta.");
            } else {
                holder.tvMensagem.setText("Alguém comentou o mesmo produto que você estava de olho. Confere lá!");
            }
        }


        holder.tvMensagem.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), atvDetalhesProduto.class);
                Bundle params = new Bundle();
                params.putInt("idProduto", alerta.idProduto);
                intent.putExtras(params);
                view.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return alertas.size();
    }


}
