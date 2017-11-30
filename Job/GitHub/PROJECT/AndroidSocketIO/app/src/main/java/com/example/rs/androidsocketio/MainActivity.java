package com.example.rs.androidsocketio;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //Khai vao bien de connect
    //SocketIO.socket.client
    private Socket mSocket;
    ListView lv_User, lv_Chat;
    EditText edt_type;
    ImageButton btn_add, btn_send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMappedAndEvent();

        try {
            mSocket = IO.socket("http://192.168.1.5:3000/");
        } catch (URISyntaxException e) {
            System.out.println(e + "");
            ;
        }

        mSocket.connect();
        mSocket.on("Server-Send-Registed", checkRegisted);
        mSocket.on("Server-Send-List-User", recivedDataList);


    }

    private void initMappedAndEvent() {
        lv_User = (ListView) findViewById(R.id.lv_user);
        lv_Chat = (ListView) findViewById(R.id.lv_chat);
        edt_type = (EditText) findViewById(R.id.edt_contain);
        btn_add = (ImageButton) findViewById(R.id.btn_add);
        btn_send = (ImageButton) findViewById(R.id.btn_send);
        //Set Event
        btn_add.setOnClickListener(this);
        btn_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add:
                try {
                    addUser();
                } catch (Exception ex) {
                    Toast.makeText(this, ex + "", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_send:
                try {
                    sendDataToServer();
                } catch (Exception ex) {
                    Toast.makeText(this, ex + "", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private void sendDataToServer() {

    }

    private void addUser() {

        //NOTE: check value in edt
        if (edt_type.getText().toString().trim().length() > 0) {
            mSocket.emit("Client-Create-User", edt_type.getText().toString());

        } else {
            Toast.makeText(this, "Please type INPUT", Toast.LENGTH_SHORT).show();
        }

        //NOTE: emit Result from Server


    }

    private Emitter.Listener checkRegisted = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject objectCheckRegiste = (JSONObject) args[0];
            try {
                boolean check = objectCheckRegiste.getBoolean("registered");
                //Check True False
                if (check) {
                    Log.e("REGISTERED", "FALSE");
                } else {
                    Log.e("REGISTERED", "TRUE");
                    mSocket.on("Server-Send-List-User", recivedDataList);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
    private Emitter.Listener recivedDataList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject object = (JSONObject) args[0];
            try {
                JSONArray array = object.getJSONArray("listuser");

                for (int i = 0; i < array.length(); i++) {
                    String username = array.getString(i);
                    Toast.makeText(MainActivity.this, username+"", Toast.LENGTH_SHORT).show();
                    Log.e("LIST",username);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };
}
