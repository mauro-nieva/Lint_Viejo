package com.example.lint;


import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.Service;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private Button cmdAceptar;
    private TextView txtName;
    private TextView txtLastname;
    private TextView txtDni;
    private TextView txtEmail;
    private TextView txtPassword;
    private TextView txtCommission;
    private TextView txtGroup;

    public IntentFilter filtro;
    private ReceptorOperacion receiver=new ReceptorOperacion();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cmdAceptar=(Button) findViewById(R.id.btn_Aceptar);


        txtName=(TextView) findViewById(R.id.txtName);
        txtLastname=(TextView) findViewById(R.id.txtLastname);
        txtDni=(TextView) findViewById(R.id.txtDni);
        txtEmail=(TextView) findViewById(R.id.txtEmail);
        txtPassword=(TextView) findViewById(R.id.txtPassword);
        txtCommission=(TextView) findViewById(R.id.txtCommission);
        txtGroup=(TextView) findViewById(R.id.txtGroup);

        cmdAceptar.setOnClickListener(HandlerCmdAceptar);

        Log.i("LOG_MAIN","Main OnCreate.");

        configurarBroadcastreceiver();

    }

    private View.OnClickListener HandlerCmdAceptar=(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            JSONObject obj = new JSONObject();

            try {
                obj.put("env", "DEV");
                obj.put("name", txtName.getText());
                obj.put("lastname", txtLastname.getText());
                obj.put("dni", txtDni.getText());
                obj.put("email", txtEmail.getText());
                obj.put("password", txtPassword.getText());
                obj.put("commission", txtCommission.getText());
                obj.put("group", txtGroup.getText());

                Intent i = new Intent(RegisterActivity.this, Service_Post.class);

                i.putExtra("url", "http://so-unlam.net.ar/api/api/register");
                i.putExtra("datosJson", obj.toString());

                startService(i);

                Log.i("LOG_MAIN","Se hizo click.");

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("LOG_MAIN","Error Main: "+e.getMessage());
            }
        }
    }) ;

    private void configurarBroadcastreceiver(){

        filtro=new IntentFilter("com.example.intentservice.intent.action.RESPUESTA_OPERACION");

        filtro.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(receiver,filtro);

    }

    public class ReceptorOperacion extends BroadcastReceiver
    {
        public void onReceive(Context context,Intent intent){
            try{
                String datosJsonString= intent.getStringExtra("datosJson");
                JSONObject datosJson = new JSONObject(datosJsonString);
                String state = datosJson.getString("state");


                Log.i("LOG_MAIN","Datos Json Main Thread:"+datosJsonString);


                if(state.equals("success"))
                {
                    Toast.makeText(getApplicationContext(), "Usuario registrado correctamente", Toast.LENGTH_LONG).show();
                    finish();
                }
                else
                {
                    String msg=datosJson.getString("msg");
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                }
            }
            catch(Exception e)
            {
                Log.e("LOG_MAIN","Error Json Main Thread:"+e.getMessage());
            }

        }
    }
}

