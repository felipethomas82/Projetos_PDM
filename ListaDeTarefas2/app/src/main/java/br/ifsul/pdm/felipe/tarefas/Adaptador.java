package br.ifsul.pdm.felipe.tarefas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.ifsul.pdm.felipe.listadetarefas.R;

/**
 * Created by PC on 08/06/2015.
 */
public class Adaptador extends ArrayAdapter<Tarefas> {
    private int resourceId;
    private LayoutInflater inflater;

    public Adaptador(Context context, int resource, List<Tarefas> objects){
        super(context, resource, objects);
        this.resourceId = resource;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Tarefas tarefa = getItem(position);
        convertView = inflater.inflate(resourceId, null);
        TextView tvTarefa = (TextView) convertView.findViewById(R.id.tvTarefa);

        tvTarefa.setText(tarefa.tarefa);
        return convertView;
    }
}
