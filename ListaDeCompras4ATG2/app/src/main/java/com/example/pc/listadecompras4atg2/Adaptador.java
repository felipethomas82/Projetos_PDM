package com.example.pc.listadecompras4atg2;

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
public class Adaptador extends ArrayAdapter<Produto> {

    private LayoutInflater inflater;
    private int resourceId;

    public Adaptador(Context context, int resource, List<Produto> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView,
                        ViewGroup parent) {

        Produto prod = getItem(position);
        convertView =
                inflater.inflate(resourceId, parent, false);

        CheckBox chkProduto = (CheckBox) convertView.findViewById(R.id.chkProduto);

        chkProduto.setText(prod.nome + " (R$" + prod.valor + ")");
        chkProduto.setChecked(prod.checado);

        return convertView;

    }
}
