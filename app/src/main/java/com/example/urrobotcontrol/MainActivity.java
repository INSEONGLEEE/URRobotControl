package com.example.urrobotcontrol;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import android.os.StrictMode;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import android.widget.Adapter;
import android.content.SharedPreferences;

public class MainActivity extends AppCompatActivity {

    public Socket socket[], socket1, socket2, socket3;
    public boolean bl_connected[];
    public EditText edittext_robot1_address, edittext_message_robot1, edittext_pname_robot1;
    public CheckBox checkbox_robot1_p1, checkbox_robot1_p2, checkbox_robot1_p3;

    public EditText edittext_robot2_address, edittext_message_robot2, edittext_pname_robot2;
    public CheckBox checkbox_robot2_p1, checkbox_robot2_p2, checkbox_robot2_p3;

    public EditText edittext_robot3_address, edittext_message_robot3, edittext_pname_robot3;
    public CheckBox checkbox_robot3_p1, checkbox_robot3_p2, checkbox_robot3_p3;
    public Spinner spinner_finename1, spinner_finename2, spinner_finename3;

    public SharedPreferences shared_pref;          // 프리퍼런스
    public SharedPreferences.Editor pref_editor; // 에디터

    public String[] spinner_items1, spinner_items2, spinner_items3;



    class ConnectThread extends Thread {
        String hostname;
        int sock_num;
        String st_recv;

        public ConnectThread(String addr, int num) {
            hostname = addr;
            sock_num = num;
        }

        public void run() {
            try { //Create socket

                //if(socket[sock_num].isConnected() == true)
                //    socket[sock_num].close();

                bl_connected[sock_num] = false;

                int port = 29999;
                socket[sock_num] = new Socket(hostname, port);

                bl_connected[sock_num] = true;

                sleep(100);

                byte[] in_buf = new byte[1024];
                int in_num = 0;
                InputStream istream = socket[sock_num].getInputStream();
                in_num = istream.read(in_buf);

                String st = new String(in_buf, StandardCharsets.UTF_8);
                st_recv = st.substring(0,in_num);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message_robot("Connection success.\n", sock_num);
                        message_robot(st_recv+"\n", sock_num);
                    }
                });

            } catch (java.io.IOException ex){

                bl_connected[sock_num] = false;
            }
            catch (Exception ex2)
            {

            }
            finally {

            }


        }
    }

    class DisconnectThread extends Thread {

        int sock_num;
        String st_recv;

        public DisconnectThread(int num) {
            sock_num = num;
        }

        public void run() {
            try { //Close socket

                int port = 29999;
                socket[sock_num].close();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message_robot("Disconnected.", sock_num);
                    }
                });


            } catch (java.io.IOException ex){

            }
        }
    }

    class SendThread extends Thread {

        String st_send = "";
        int sock_num;
        String st_recv;

        public SendThread(String msg, int num) {
            st_send = msg;
            sock_num = num;
        }

        public void run() {
            try { //Create socket

                if(socket[sock_num].isConnected() == false)
                    throw new Exception();

                byte[] data = st_send.getBytes();
                OutputStream ostream = socket[sock_num].getOutputStream();
                ostream.write(data);

                sleep(100);

                byte[] in_buf = new byte[1024];
                int in_num = 0;
                InputStream istream = socket[sock_num].getInputStream();
                in_num = istream.read(in_buf);

                String st = new String(in_buf, StandardCharsets.UTF_8);
                st_recv = st.substring(0,in_num);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message_robot(st_recv+"\n", sock_num);
                    }
                });


            } catch (java.io.IOException ex){

            }
            catch(Exception ex2)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message_robot("Fail.\n", sock_num);
                    }
                });
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int SDK_INT = android.os.Build.VERSION.SDK_INT;

        if (SDK_INT > 8){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        shared_pref = getSharedPreferences("pref", Activity.MODE_PRIVATE);
        pref_editor = shared_pref.edit();

        socket = new Socket[3];
        bl_connected = new boolean[3];

        for(int i=0;i<3;i++)
        {
            socket[i] = new Socket();
            bl_connected[i] = false;
        }

        edittext_robot1_address = (EditText)findViewById(R.id.robot1_address);
        edittext_message_robot1 = (EditText)findViewById(R.id.robot1_message);
        edittext_pname_robot1 = (EditText)findViewById(R.id.robot1_editpname);

        //checkbox_robot1_p1= (CheckBox) findViewById(R.id.checkbox_robot1_1);
        //checkbox_robot1_p2= (CheckBox) findViewById(R.id.checkbox_robot1_2);
        //checkbox_robot1_p3= (CheckBox) findViewById(R.id.checkbox_robot1_3);

        edittext_robot2_address = (EditText)findViewById(R.id.robot2_address);
        edittext_message_robot2 = (EditText)findViewById(R.id.robot2_message);
        edittext_pname_robot2 = (EditText)findViewById(R.id.robot2_editpname);

        //checkbox_robot2_p1= (CheckBox) findViewById(R.id.checkbox_robot2_1);
        //checkbox_robot2_p2= (CheckBox) findViewById(R.id.checkbox_robot2_2);
        //checkbox_robot2_p3= (CheckBox) findViewById(R.id.checkbox_robot2_3);

        edittext_robot3_address = (EditText)findViewById(R.id.robot3_address);
        edittext_message_robot3 = (EditText)findViewById(R.id.robot3_message);
        edittext_pname_robot3 = (EditText)findViewById(R.id.robot3_editpname);

        //checkbox_robot3_p1= (CheckBox) findViewById(R.id.checkbox_robot3_1);
        //checkbox_robot3_p2= (CheckBox) findViewById(R.id.checkbox_robot3_2);
        //checkbox_robot3_p3= (CheckBox) findViewById(R.id.checkbox_robot3_3);

        spinner_items1 = new String[5];
        spinner_items2 = new String[5];
        spinner_items3 = new String[5];

        spinner_items1[0] = "1_1.urp";
        spinner_items1[1] = "1_2.urp";
        spinner_items1[2] = "1_3.urp";
        spinner_items1[3] = "1_4.urp";
        spinner_items1[4] = "1_5.urp";

        spinner_items2[0] = "2_1.urp";
        spinner_items2[1] = "2_2.urp";
        spinner_items2[2] = "2_3.urp";
        spinner_items2[3] = "2_4.urp";
        spinner_items2[4] = "2_5.urp";

        spinner_items3[0] = "3_1.urp";
        spinner_items3[1] = "3_2.urp";
        spinner_items3[2] = "3_3.urp";
        spinner_items3[3] = "3_3.urp";
        spinner_items3[4] = "3_3.urp";

        loadSharedPref();

        updateProgramSpinners();

        spinner_finename1.setSelection(0);
        spinner_finename2.setSelection(0);
        spinner_finename3.setSelection(0);

        saveSharedPref();
    }

    public void button_robot1_setpname(View view) {
        int idx = 0;
        String pname = "";
        idx = spinner_finename1.getSelectedItemPosition();

        pname = String.valueOf(edittext_pname_robot1.getText());
        spinner_items1[idx] = pname;

        updateProgramSpinners();
        saveSharedPref();

        spinner_finename1.setSelection(idx);
    }

    public void button_robot2_setpname(View view) {
        int idx = 0;
        String pname = "";
        idx = spinner_finename2.getSelectedItemPosition();

        pname = String.valueOf(edittext_pname_robot2.getText());
        spinner_items2[idx] = pname;

        updateProgramSpinners();
        saveSharedPref();

        spinner_finename2.setSelection(idx);
    }

    public void button_robot3_setpname(View view) {
        int idx = 0;
        String pname = "";
        idx = spinner_finename3.getSelectedItemPosition();

        pname = String.valueOf(edittext_pname_robot3.getText());
        spinner_items3[idx] = pname;

        updateProgramSpinners();
        saveSharedPref();

        spinner_finename3.setSelection(idx);
    }

    public void updateProgramSpinners()
    {
        spinner_finename1 = (Spinner) findViewById(R.id.spinner_robot1);
        spinner_finename2 = (Spinner) findViewById(R.id.spinner_robot2);
        spinner_finename3 = (Spinner) findViewById(R.id.spinner_robot3);

        ArrayAdapter<String> adapter1  = new ArrayAdapter<String>(this, R.layout.spinner_item, spinner_items1);
        ArrayAdapter<String> adapter2  = new ArrayAdapter<String>(this, R.layout.spinner_item, spinner_items2);
        ArrayAdapter<String> adapter3  = new ArrayAdapter<String>(this, R.layout.spinner_item, spinner_items3);

        spinner_finename1.setAdapter(adapter1);
        spinner_finename2.setAdapter(adapter2);
        spinner_finename3.setAdapter(adapter3);
    }

    public void loadSharedPref()
    {
        spinner_items1[0] = shared_pref.getString("FNAME1_1", "1_1.urp");
        spinner_items1[1] = shared_pref.getString("FNAME1_2", "1_2.urp");
        spinner_items1[2] = shared_pref.getString("FNAME1_3", "1_3.urp");
        spinner_items1[3] = shared_pref.getString("FNAME1_4", "1_4.urp");
        spinner_items1[4] = shared_pref.getString("FNAME1_5", "1_5.urp");


        spinner_items2[0] = shared_pref.getString("FNAME2_1", "2_1.urp");
        spinner_items2[1] = shared_pref.getString("FNAME2_2", "2_2.urp");
        spinner_items2[2] = shared_pref.getString("FNAME2_3", "2_3.urp");
        spinner_items2[3] = shared_pref.getString("FNAME2_4", "2_4.urp");
        spinner_items2[4] = shared_pref.getString("FNAME2_5", "2_5.urp");

        spinner_items3[0] = shared_pref.getString("FNAME3_1", "3_1.urp");
        spinner_items3[1] = shared_pref.getString("FNAME3_2", "3_2.urp");
        spinner_items3[2] = shared_pref.getString("FNAME3_3", "3_3.urp");
        spinner_items3[3] = shared_pref.getString("FNAME3_4", "3_4.urp");
        spinner_items3[4] = shared_pref.getString("FNAME3_5", "3_5.urp");
    }

    public void saveSharedPref()
    {
        pref_editor.putString("FNAME1_1", spinner_items1[0]);
        pref_editor.putString("FNAME1_2", spinner_items1[1]);
        pref_editor.putString("FNAME1_3", spinner_items1[2]);
        pref_editor.putString("FNAME1_4", spinner_items1[3]);
        pref_editor.putString("FNAME1_5", spinner_items1[4]);

        pref_editor.putString("FNAME2_1", spinner_items2[0]);
        pref_editor.putString("FNAME2_2", spinner_items2[1]);
        pref_editor.putString("FNAME2_3", spinner_items2[2]);
        pref_editor.putString("FNAME2_4", spinner_items2[3]);
        pref_editor.putString("FNAME2_5", spinner_items2[4]);

        pref_editor.putString("FNAME3_1", spinner_items3[0]);
        pref_editor.putString("FNAME3_2", spinner_items3[1]);
        pref_editor.putString("FNAME3_3", spinner_items3[2]);
        pref_editor.putString("FNAME3_4", spinner_items3[3]);
        pref_editor.putString("FNAME3_5", spinner_items3[4]);

        pref_editor.apply();

    }

    public void message_robot1(String st){
        edittext_message_robot1.append(st);
    }

    public void clear_message_robot1(){
        edittext_message_robot1.setText("");
    }

    public void message_robot2(String st){
        edittext_message_robot2.append(st);
    }

    public void clear_message_robot2(){
        edittext_message_robot2.setText("");
    }

    public void message_robot3(String st){
        edittext_message_robot3.append(st);
    }

    public void clear_message_robot3(){
        edittext_message_robot3.setText("");
    }

    public void message_robot(String msg, int idx)
    {
        if(idx == 0)
            message_robot1(msg);
        else if(idx == 1)
            message_robot2(msg);
        else if(idx == 2)
            message_robot3(msg);
    }

    public void button_robot1_messageclear(View view) {
        clear_message_robot1();
    }

    public void button_robot2_messageclear(View view) {
        clear_message_robot2();
    }

    public void button_robot3_messageclear(View view) {
        clear_message_robot3();
    }

    public void button_robot1_connect(View view) {
        message_robot1("Start connection to (" + edittext_robot1_address.getText() + ").\n");

        String addr = edittext_robot1_address.getText().toString();
        ConnectThread thread = new ConnectThread(addr,0);
        thread.start();
    }

    public void button_robot2_connect(View view) {
        message_robot2("Start connection to (" + edittext_robot2_address.getText() + ").\n");

        String addr = edittext_robot2_address.getText().toString();
        ConnectThread thread = new ConnectThread(addr,1);
        thread.start();
    }

    public void button_robot3_connect(View view) {
        message_robot3("Start connection to (" + edittext_robot3_address.getText() + ").\n");

        String addr = edittext_robot3_address.getText().toString();
        ConnectThread thread = new ConnectThread(addr,2);
        thread.start();
    }

    public void button_robot1_disconnect(View view) {

        message_robot1("Start disconnection.\n");

        DisconnectThread thread = new DisconnectThread(0);
        thread.start();
    }

    public void button_robot2_disconnect(View view) {
        message_robot2("Start disconnection.\n");

        DisconnectThread thread = new DisconnectThread(1);
        thread.start();
    }

    public void button_robot3_disconnect(View view) {
        message_robot3("Start disconnection.\n");

        DisconnectThread thread = new DisconnectThread(2);
        thread.start();
    }

    public void button_robot1_loadprogram(View view) {
        String st_progname = "1_1.urp";
        String st_send = "";

        /*
        if(checkbox_robot1_p1.isChecked() == true)
            st_progname = "1_1.URP";
        else if(checkbox_robot1_p2.isChecked()== true)
            st_progname = "1_2.URP";
        else if(checkbox_robot1_p3.isChecked()== true)
            st_progname = "1_3.URP";
        else
            return;
        */

        st_progname = spinner_finename1.getSelectedItem().toString();
        st_send = "LOAD " + st_progname + '\n';

        SendThread thread = new SendThread(st_send,0);
        thread.start();
    }

    public void button_robot2_loadprogram(View view) {
        String st_progname = "<2_1.urp>";
        String st_send = "";

        /*
        if(checkbox_robot2_p1.isChecked() == true)
            st_progname = "<2_1.urp>";
        else if(checkbox_robot2_p2.isChecked()== true)
            st_progname = "<2_2.urp>";
        else if(checkbox_robot2_p3.isChecked()== true)
            st_progname = "<2_3.urp>";
        else
            return;
        */

        st_progname = spinner_finename2.getSelectedItem().toString();
        st_send = "LOAD " + st_progname + '\n';

        SendThread thread = new SendThread(st_send,1);
        thread.start();
    }

    public void button_robot3_loadprogram(View view) {
        String st_progname = "<3_1.urp>";
        String st_send = "";

        /*
        if(checkbox_robot3_p1.isChecked() == true)
            st_progname = "<3_1.urp>";
        else if(checkbox_robot3_p2.isChecked()== true)
            st_progname = "<3_2.urp>";
        else if(checkbox_robot3_p3.isChecked()== true)
            st_progname = "<3_3.urp>";
        else
            return;
        */

        st_progname = spinner_finename3.getSelectedItem().toString();
        st_send = "LOAD " + st_progname + '\n';

        SendThread thread = new SendThread(st_send,2);
        thread.start();
    }


    public void button_robot1_play(View view) {
        SendThread thread = new SendThread("PLAY\n",0);
        thread.start();
    }

    public void button_robot2_play(View view) {
        SendThread thread = new SendThread("PLAY\n",1);
        thread.start();
    }

    public void button_robot3_play(View view) {
        SendThread thread = new SendThread("PLAY\n",2);
        thread.start();
    }

    public void button_robot1_pause(View view) {
        SendThread thread = new SendThread("PAUSE\n",0);
        thread.start();
    }

    public void button_robot2_pause(View view) {
        SendThread thread = new SendThread("PAUSE\n",1);
        thread.start();
    }

    public void button_robot3_pause(View view) {
        SendThread thread = new SendThread("PAUSE\n",2);
        thread.start();
    }

    public void button_robot1_stop(View view) {

        SendThread thread = new SendThread("STOP\n",0);
        thread.start();
    }

    public void button_robot2_stop(View view) {
        SendThread thread = new SendThread("STOP\n",1);
        thread.start();
    }

    public void button_robot3_stop(View view) {
        SendThread thread = new SendThread("STOP\n",2);
        thread.start();
    }


    public void checkbox_click_robot1_1(View view) {
        if(checkbox_robot1_p1.isChecked())
        {
            checkbox_robot1_p2.setChecked(false);
            checkbox_robot1_p3.setChecked(false);
        }
    }

    public void checkbox_click_robot1_2(View view) {
        if(checkbox_robot1_p2.isChecked())
        {
            checkbox_robot1_p1.setChecked(false);
            checkbox_robot1_p3.setChecked(false);
        }
    }

    public void checkbox_click_robot1_3(View view) {
        if(checkbox_robot1_p3.isChecked())
        {
            checkbox_robot1_p1.setChecked(false);
            checkbox_robot1_p2.setChecked(false);
        }
    }

    public void checkbox_click_robot2_1(View view) {
        if(checkbox_robot2_p1.isChecked())
        {
            checkbox_robot2_p2.setChecked(false);
            checkbox_robot2_p3.setChecked(false);
        }
    }

    public void checkbox_click_robot2_2(View view) {
        if(checkbox_robot2_p2.isChecked())
        {
            checkbox_robot2_p1.setChecked(false);
            checkbox_robot2_p3.setChecked(false);
        }
    }

    public void checkbox_click_robot2_3(View view) {
        if(checkbox_robot2_p3.isChecked())
        {
            checkbox_robot2_p1.setChecked(false);
            checkbox_robot2_p2.setChecked(false);
        }
    }

    public void checkbox_click_robot3_1(View view) {
        if(checkbox_robot3_p1.isChecked())
        {
            checkbox_robot3_p2.setChecked(false);
            checkbox_robot3_p3.setChecked(false);
        }
    }

    public void checkbox_click_robot3_2(View view) {
        if(checkbox_robot3_p2.isChecked())
        {
            checkbox_robot3_p1.setChecked(false);
            checkbox_robot3_p3.setChecked(false);
        }
    }

    public void checkbox_click_robot3_3(View view) {
        if(checkbox_robot3_p3.isChecked())
        {
            checkbox_robot3_p1.setChecked(false);
            checkbox_robot3_p2.setChecked(false);
        }
    }

    public void button_exit_program(View view) {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override

    public void onDestroy() {

        super.onDestroy();

        for(int i=0;i<3;i++)
        {
            try {
                socket[i].close();
            } catch (java.io.IOException ex){

            }
            catch (Exception ex2)
            {

            }

        }
    }
}