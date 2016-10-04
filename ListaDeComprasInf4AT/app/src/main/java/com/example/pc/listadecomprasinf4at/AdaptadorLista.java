package com.example.pc.listadecomprasinf4at;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import java.util.List;

/**
 * Created by PC on 12/05/2016.
 */
public class AdaptadorLista extends ArrayAdapter<Produto> {

    private LayoutInflater inflater;
    private int resourceId;

    public AdaptadorLista(Context context, int resource, List<Produto> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //return super.getView(position, convertView, parent);

        final Produto prod = getItem(position);

        convertView = inflater.inflate(resourceId, parent, false);

        CheckBox chkProduto = (CheckBox) convertView.findViewById(R.id.chkProduto);
        chkProduto.setChecked(prod.checado);
        chkProduto.setText(prod.nomeProduto
                + " (R$" + prod.valor + ")");

        return convertView;

    }
}
