package br.ifsul.pdm.felipe.trocacomigo;

import com.parse.Parse;
import com.parse.ParseInstallation;

/**
 * Created by felipe on 05/01/16.
 */
public class TrocaComigoApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //This will only be called once in your app's entire lifecycle.
        Parse.initialize(this, "6IvjogCHcMhPdpwWkoeAhvU4cfqVo1o0YAKpfT2T", "hVS3YgxYOopI9FxR8TnkbLC7Zei4zJvAbjSxGNB1");
        ParseInstallation.getCurrentInstallation().saveInBackground();

    }

}
