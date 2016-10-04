package br.ifsul.pdm.felipe.trocacomigo;

import android.os.Bundle;

/**
 * Created by PC on 09/06/2015.
 */
public class UserControl {

    private int userId;
    private String apiKey;

    UserControl(Bundle params) throws MissingUserParamsException {
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

    UserControl(int userId, String apiKey){
        this.userId = userId;
        this.apiKey = apiKey;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    class MissingUserParamsException extends Throwable {
        public MissingUserParamsException(String s) {
            super(s);
        }
    }


}
