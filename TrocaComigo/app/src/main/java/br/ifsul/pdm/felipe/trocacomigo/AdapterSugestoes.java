package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by felipe on 27/12/15.
 */
public class AdapterSugestoes extends CursorAdapter{


        private List<String> items;

        private TextView text;

        public AdapterSugestoes(Context context, Cursor cursor, List<String> items) {

            super(context, cursor, false);

            this.items = items;

        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            text.setText(items.get(cursor.getPosition()));

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.item, parent, false);

            text = (TextView) view.findViewById(R.id.tvSugestoes);

            return view;

        }
}
