package br.ifsul.pdm.felipe.trocacomigo;

import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Map;

public class SearchResultActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Map<String, String> map=new Hashtable<String, String>();
        map.put("Ajay", "Ajay Singh Dewari");
        map.put("Vijay", "Vijay Singh Dev");
        map.put("jay", "Jay Singh Rathor");
        map.put("Saurav", "Saurav Sharma");
        map.put("Lokendra", "Lokendra Bains");
        String result="Found nothing";
        Intent intent =getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            String query=intent.getStringExtra(SearchManager.QUERY);
            String str=map.get(query);
            if(str!=null){
                result="result: "+query+" is "+str;
            }
        }
        Log.i("Script", "TESTE SEARCH:" + result);
        TextView text=new TextView(this);
        text.setText(result);
        setContentView(text);
    }

}
