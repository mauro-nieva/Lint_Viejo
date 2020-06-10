package com.example.lint;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Service_Post extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private Exception mException;
    private HttpURLConnection httpConnection;
    private URL mUrl;

    public Service_Post() {
        super("Service_Post");

        Log.i("LOG_SERVICE","Constructor Service_Post().");
    }

    public void onCreate(){

        super.onCreate();

        Log.i("LOG_SERVICE","Service onCreate()");
    }

    protected void onHandleIntent(Intent intent){
        try{
            String url=intent.getExtras().getString("url");
            JSONObject datosJson=new JSONObject(intent.getExtras().getString(("datosJson")));

            ejecutarPost(url,datosJson);

            Log.i("LOG_SERVICE","Se ejecuto onHandleIntent()");
        }
        catch(Exception Exc)
        {
            Log.e("LOG_SERVICE","Service Error: "+Exc.getMessage());
        }
    }

    protected void ejecutarPost(String url,JSONObject datosJson)
    {

        String result=Post(url,datosJson);

        Intent i=new Intent("com.example.intentservice.intent.action.RESPUESTA_OPERACION");
        i.putExtra("datosJson",result);

        sendBroadcast(i);
    }

    private String Post(String url,JSONObject datosJson)
    {
        HttpURLConnection urlConnection=null;
        String result="";
        String line="";

        try
        {
            URL mUrl=new URL(url);

            urlConnection=(HttpURLConnection) mUrl.openConnection();
            urlConnection.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setConnectTimeout(5000);
            urlConnection.setRequestMethod("POST");

            DataOutputStream wr=new DataOutputStream(urlConnection.getOutputStream());

            wr.write(datosJson.toString().getBytes("UTF-8"));

            Log.i("LOG_SERVICE","Se va a enviar al servidor: "+datosJson.toString());

            wr.flush();
            wr.close();

            urlConnection.connect();
            int responseCode=urlConnection.getResponseCode();

            BufferedReader bufferedReader;

            if((responseCode==HttpURLConnection.HTTP_OK)||(responseCode==HttpURLConnection.HTTP_CREATED))
            {
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            }
            else
            {
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
            }

            while ((line = bufferedReader.readLine()) != null) {
                result += line + "\n";
            }
            bufferedReader.close();


            Log.i("LOG_SERVICE","ResponseCode: "+ Integer.toString(responseCode));
            Log.i("LOG_SERVICE","Result: "+ result);

            mException=null;
            urlConnection.disconnect();
            return result;
        }
        catch(Exception Exc)
        {
            Log.e("LOG_SERVICE","Service Error: "+Exc.getMessage());
            return null;
        }

    }

}
