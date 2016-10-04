package provapdm.jeansarlon.fidelize;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class AdapterWallet extends ArrayAdapter<Wallet> {
    private int resourceId;
    private LayoutInflater inflater;

    public AdapterWallet(Context context, int resource, List<Wallet> objects){
        super(context, resource, objects);
        this.resourceId = resource;
        this.inflater = LayoutInflater.from(context);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Wallet wallet =  getItem(position);
        convertView = inflater.inflate(resourceId, null);
        TextView tvcompany = (TextView) convertView.findViewById(R.id.tvCompanyNameWallet);
        TextView tvAmount = (TextView) convertView.findViewById(R.id.tvCompanyAmountWallet);

        tvAmount.setText(wallet.amount.toString());
        tvcompany.setText(wallet.companyName);

        return convertView;
    }
}
