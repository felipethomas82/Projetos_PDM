package com.example.pc.listacomplexacomfiltro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by PC on 08/05/2016.
 */
public class Adaptador extends ArrayAdapter<Pessoa> {

    private LayoutInflater inflater;
    private int resourceId;

    public Adaptador(Context context, int resource, List<Pessoa> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Pessoa pessoa = getItem(position);
        convertView = inflater.inflate(resourceId, parent, false);
        TextView tvNome = (TextView) convertView.findViewById(R.id.tvNomePessoa);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);

        tvNome.setText(pessoa.nome);
        tvEmail.setText(pessoa.email);

        return convertView;

    }
}
