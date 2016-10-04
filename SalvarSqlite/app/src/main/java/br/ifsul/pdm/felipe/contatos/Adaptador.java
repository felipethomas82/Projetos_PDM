package br.ifsul.pdm.felipe.contatos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.ifsul.pdm.felipe.salvarsqlite.R;

/**
 * Created by PC on 08/06/2015.
 */
public class Adaptador extends ArrayAdapter<Contato> {
    private int resourceId;
    private LayoutInflater inflater;

    public Adaptador(Context context, int resource, List<Contato> objects){
        super(context, resource, objects);
        this.resourceId = resource;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Contato contato = getItem(position);
        convertView = inflater.inflate(resourceId, null);
        TextView tvNome = (TextView) convertView.findViewById(R.id.tvNome);
        TextView tvTelefone = (TextView) convertView.findViewById(R.id.tvTelefone);
        TextView tvEmail = (TextView) convertView.findViewById(R.id.tvEmail);
        tvNome.setText(contato.nome);
        tvTelefone.setText(contato.telefone);
        tvEmail.setText(contato.email);
        return convertView;
    }
}
