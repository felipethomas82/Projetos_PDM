package br.ifsul.pdm.felipe.trocacomigo;

import android.os.Bundle;

/**
 * Created by PC on 31/08/2015.
 */

//Classe criada em substituição da UserControl, pois esta sera uma classe Singleton
public class UserControlV2 {

    private static UserControlV2 mUserControl;

    public static int userId;
    public static String apiKey;

    public static String username;
    public static String email;
    public static String pathUserPhoto;

    //ira guardar tbm as posicoes do GPS, ou do netowrk provider
    public static double latitude;
    public static double longitude;

    public static String endereco;

    //construtor privado para evitar que a classe seja instanciada
    private UserControlV2()
    {
        //nada a fazer
    }

    public static UserControlV2 getInstance(){
        if (mUserControl == null){
            mUserControl = new UserControlV2();
        }

        return mUserControl;
    }

    public static void setLoginParameters (Bundle params) throws MissingUserParamsException {
        if (params != null)
        {
            userId = params.getInt("userId");
            apiKey = params.getString("apiKey");
        }
        else
        {
            throw new MissingUserParamsException("Não foram setados os parâmetros de usuário");
        }
    }

    static class MissingUserParamsException extends Throwable {
        public MissingUserParamsException(String s) {
            super(s);
        }
    }

}
