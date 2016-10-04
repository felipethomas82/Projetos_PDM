package br.ifsul.pdm.felipe.trocacomigo;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by PC on 07/06/2015.
 */
public class URIWebservice {

    //private static final String HOST = "http://192.168.173.1/trocacomigo/v1";
    private String host; // = "http://10.0.3.2/trocacomigo/v1";
    //private static final String HOSTTESTE = "http://10.0.3.2/trocacomigo/v2";
    //private static final String HOSTTESTE = "http://192.168.173.1/trocacomigo/v2";

    public URIWebservice(Context context){

        SharedPreferences preferences = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE);
        String host = preferences.getString("host", null);
        if (host != null) {
            this.host = preferences.getString("host", null);
        }
        else {
            SharedPreferences.Editor editor = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE).edit();
            editor.putString("host", "http://10.0.3.2");
            editor.commit();

            this.host = "http://10.0.3.2";
        }
    }

    public static String URI_REGISTER_USER = "/trocacomigo/v1/register";

    public static String URI_LOGIN_USER = "/trocacomigo/v1/login";

    public static String URI_PRODUCTS = "/trocacomigo/v1/produtos";

    public static String URI_PRODUCT_PHOTOS = "/trocacomigo/v1/produtos/fotos";

    public static String URI_PRODUCT_ADDRESS = "/trocacomigo/v1/produtos/endereco";

    public static String URI_PRODUCT_COORDINATES = "/trocacomigo/v1/produtos/coordenadas";

    public static String URI_PRODUCT_STATUS = "/trocacomigo/v1/produtos/status";

    public static String URI_PRODUCT_COMMENTS = "/trocacomigo/v1/produtos/comentario";

    public static String URI_PRODUCT_USER = "/trocacomigo/v1/produtos/usuario";

    public static String URI_PRODUCT_TITLE_SUGGESTIONS = "/trocacomigo/v1/produtos/palavras";

    public static String URI_USER_PROFILE = "/trocacomigo/v1/usuario";

    public static String URI_NOTIFICATIONS = "/trocacomigo/v1/alertas";

    //public static String URI_FOTOS_TESTE = HOSTTESTE + "/produtos";



    public String getHost()
    {
        return host;
    }

    public void setHost(String host, Context context){
        SharedPreferences.Editor editor = context.getSharedPreferences("myPrefs", context.MODE_PRIVATE).edit();
        editor.putString("host", host);
        editor.commit();

        this.host = host;
    }

}
