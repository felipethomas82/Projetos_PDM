package com.example.pc.listacomplexacomfiltro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Created by PC on 08/05/2016.
 */
public class Adaptador extends ArrayAdapter<Pessoa> {

    private LayoutInflater inflater;
    private int resourceId;

    private ArrayList<Pessoa> dadosOriginais;
    private ArrayList<Pessoa> dadosFiltrados;

    public Adaptador(Context context, int resource, List<Pessoa> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;

        this.dadosFiltrados = (ArrayList) objects;
        this.dadosOriginais = (ArrayList) objects;

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

    public int getCount(){
        return dadosFiltrados.size();
    }

    public Pessoa getItem(int position) {
        return dadosFiltrados.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults results = new FilterResults();

                String busca = charSequence.toString();
                //se a busca for vazia
                if (busca == null || busca.length() == 0) {
                    results.values = dadosOriginais;
                    results.count = dadosOriginais.size();
                } else {
                    ArrayList<Pessoa> filterResultsData = new ArrayList<Pessoa>();

                    for (Pessoa pessoa : dadosOriginais) {
                        if (pessoa.nome.toLowerCase().contains(busca.toLowerCase())) {
                            filterResultsData.add(pessoa);
                        }
                    }

                    results.values = filterResultsData;
                    results.count = filterResultsData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                dadosFiltrados = (ArrayList<Pessoa>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
