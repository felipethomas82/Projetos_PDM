package br.ifsul.pdm.felipe.imageview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import android.widget.Filter;


/**
 * Created by PC on 11/05/2015.
 */
public class Adaptador extends ArrayAdapter<Contato>{

    private LayoutInflater inflater;
    private int resourceId;

    ArrayList<Contato> dadosFiltrados;
    ArrayList<Contato> dadosOriginais;

    public Adaptador(Context context, int resource, List<Contato> objects) {
        super(context, resource, objects);
        this.inflater = LayoutInflater.from(context);
        this.resourceId = resource;

        this.dadosFiltrados = (ArrayList) objects;
        this.dadosOriginais = (ArrayList) objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Contato person = getItem(position);
        convertView = inflater.inflate(resourceId, parent, false);
        TextView nome = (TextView) convertView.findViewById(R.id.nome);
        TextView email = (TextView) convertView.findViewById(R.id.email);

        nome.setText(person.nome);
        email.setText(person.email);

        return convertView;

    }

    public int getCount()
    {
        return dadosFiltrados.size();
    }
    public Contato getItem(int position)
    {
        return dadosFiltrados.get(position);
    }
    public long getItemId(int position)
    {
        return position;
    }

    public android.widget.Filter getFilter()
    {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence cs)
            {
                android.widget.Filter.FilterResults results = new FilterResults();
                String busca = cs.toString();

                if (busca == null || busca == "")
                {
                    results.values = dadosOriginais;
                    results.count = dadosOriginais.size();
                }
                else
                {
                    ArrayList<Contato> filterResultData = new ArrayList<Contato>();

                    for (Contato pessoa : dadosOriginais)
                    {
                        if (pessoa.nome.toLowerCase().contains(busca.toLowerCase()))
                        {
                            filterResultData.add(pessoa);
                        }
                    }
                    results.values = filterResultData;
                    results.count = filterResultData.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                dadosFiltrados = (ArrayList<Contato>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
