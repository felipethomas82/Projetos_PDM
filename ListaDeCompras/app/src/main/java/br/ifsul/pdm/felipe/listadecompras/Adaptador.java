package br.ifsul.pdm.felipe.listadecompras;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by PC on 17/05/2015.
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final  Produto prod = getItem(position);
        convertView = inflater.inflate(resourceId, parent, false);
        CheckBox chkProd = (CheckBox) convertView.findViewById(R.id.chkProduto);

        chkProd.setChecked(prod.checado);
        chkProd.setText(prod.nomeProduto + " (R$ " + prod.valor + ")");

        chkProd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prod.checado = isChecked;
            }
        });



        return convertView;
    }
}
