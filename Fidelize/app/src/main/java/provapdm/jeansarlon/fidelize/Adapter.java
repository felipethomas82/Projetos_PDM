package provapdm.jeansarlon.fidelize;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class Adapter extends ArrayAdapter<Company> {
    private int resourceId;
    private LayoutInflater inflater;

    public Adapter(Context context, int resource, List<Company> objects){
        super(context, resource, objects);
        this.resourceId = resource;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Company company = getItem(position);
        convertView = inflater.inflate(resourceId, null);
        TextView tvNome = (TextView) convertView.findViewById(R.id.tvName);

        tvNome.setText(company.nome);
        return convertView;
    }
}
