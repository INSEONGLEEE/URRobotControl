package com.example.urrobotcontrol;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import com.example.urrobotcontrol.base.base_activity.BaseActivity;
import com.example.urrobotcontrol.base.base_alert.BaseAlert;
import com.example.urrobotcontrol.databinding.ActivityMainRenewBinding;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MainRenewActivity extends BaseActivity {

    //=====================================
    // region // static, final
    //=====================================

    //=====================================
    // endregion // static, final
    //=====================================


    //=====================================
    // region // view
    //=====================================

    private ActivityMainRenewBinding mBinding;

    public String[] spinner_items1, spinner_items2, spinner_items3;


    //=====================================
    // endregion // view
    //=====================================


    //=====================================
    // region // variable
    //=====================================

    public Socket socket[], socket1, socket2, socket3;
    public boolean bl_connected[];
    public SharedPreferences shared_pref;          // 프리퍼런스
    public SharedPreferences.Editor pref_editor; // 에디터

    private long backKeyPressedTime = 0;

    //=====================================
    // endregion // variable
    //=====================================


    //=====================================
    // region // initialize
    //=====================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityMainRenewBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        initLayout();

        initialize();
    }

    @Override
    protected void initLayout() {
        // Connect
        mBinding.radioConnect1.setOnClickListener(v -> button_robot_connect(1));
        mBinding.radioConnect2.setOnClickListener(v -> button_robot_connect(2));
        mBinding.radioConnect3.setOnClickListener(v -> button_robot_connect(3));
        // disconnect
        mBinding.radioDisconnect1.setOnClickListener(v -> button_robot_disconnect(1));
        mBinding.radioDisconnect2.setOnClickListener(v -> button_robot_disconnect(2));
        mBinding.radioDisconnect3.setOnClickListener(v -> button_robot_disconnect(3));
        // 프로그램셋
        mBinding.btnSet1.setOnClickListener(v -> button_robot_setpname(1));
        mBinding.btnSet2.setOnClickListener(v -> button_robot_setpname(2));
        mBinding.btnSet3.setOnClickListener(v -> button_robot_setpname(3));
        // load program
        mBinding.btnRobot1Load.setOnClickListener(v -> button_robot_loadProgram(1));
        mBinding.btnRobot2Load.setOnClickListener(v -> button_robot_loadProgram(2));
        mBinding.btnRobot3Load.setOnClickListener(v -> button_robot_loadProgram(3));
        //play
        mBinding.radioPlay1.setOnClickListener(v -> button_robot_control("PLAY", 1));
        mBinding.radioPlay2.setOnClickListener(v -> button_robot_control("PLAY", 2));
        mBinding.radioPlay3.setOnClickListener(v -> button_robot_control("PLAY", 3));
        //pause
        mBinding.radioPause1.setOnClickListener(v -> button_robot_control("PAUSE", 1));
        mBinding.radioPause2.setOnClickListener(v -> button_robot_control("PAUSE", 2));
        mBinding.radioPause3.setOnClickListener(v -> button_robot_control("PAUSE", 3));
        //stop
        mBinding.radioStop1.setOnClickListener(v -> button_robot_control("STOP", 1));
        mBinding.radioStop2.setOnClickListener(v -> button_robot_control("STOP", 2));
        mBinding.radioStop3.setOnClickListener(v -> button_robot_control("STOP", 3));
        // 메세지 클리어
        mBinding.btnClear1.setOnClickListener(v -> {
            mBinding.etRobotMessage1.setText("");
        });
        mBinding.btnClear2.setOnClickListener(v -> {
            mBinding.etRobotMessage2.setText("");
        });
        mBinding.btnClear3.setOnClickListener(v -> {
            mBinding.etRobotMessage3.setText("");
        });
        //exit
        mBinding.btnExit.setOnClickListener(v -> BaseAlert.show(mContext, "로그아웃을 하시겠습니까?", (dialog, which) -> {
            android.os.Process.killProcess(android.os.Process.myPid());
        }, (dialog, which) -> {
        }));

    }

    @Override
    protected void initialize() {

        for (int i = 0; i < 3; i++) {
            socket[i] = new Socket();
            bl_connected[i] = false;
        }
        // 스피너설정
        initSpinner();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        for (int i = 0; i < 3; i++) {
            try {
                socket[i].close();
            } catch (java.io.IOException ex) {

            } catch (Exception ex2) {

            }

        }
    }


    //=====================================
    // endregion // initialize
    //=====================================

    //=====================================
    // region // event
    //=====================================

    //connect
    public void button_robot_connect(int number) {
        String addr = "";

        if (number == 1) {
            message_robot("Start connection to (" + mBinding.robot1Address.getText() + ").\n", 1);
            addr = mBinding.robot1Address.getText().toString();

        } else if (number == 2) {
            message_robot("Start connection to (" + mBinding.robot2Address.getText() + ").\n", 2);
            addr = mBinding.robot2Address.getText().toString();
        } else if (number == 3) {
            message_robot("Start connection to (" + mBinding.robot3Address.getText() + ").\n", 3);
            addr = mBinding.robot3Address.getText().toString();

        }
        ConnectThread thread = new ConnectThread(addr, (number - 1));

        thread.start();
    }

    // disconnect
    public void button_robot_disconnect(int number) {
        if (number == 1) {
            message_robot("Start disconnection.\n", 1);
        } else if (number == 2) {
            message_robot("Start disconnection.\n", 2);
        } else if (number == 3) {
            message_robot("Start disconnection.\n", 3);
        }
        MainRenewActivity.DisconnectThread thread = new MainRenewActivity.DisconnectThread((number - 1));
        thread.start();
    }


    // set program
    public void button_robot_setpname(int number) {
        int idx = 0;
        String pname = "";
        if (number == 1) {
            idx = mBinding.spinnerRobot1.getSelectedItemPosition();
            pname = String.valueOf(mBinding.robot1Editpname.getText());
        } else if (number == 2) {
            idx = mBinding.spinnerRobot2.getSelectedItemPosition();
            pname = String.valueOf(mBinding.robot2Editpname.getText());
        } else if (number == 3) {
            idx = mBinding.spinnerRobot3.getSelectedItemPosition();
            pname = String.valueOf(mBinding.robot3Editpname.getText());
        }

        spinner_items1[idx] = pname;

        updateProgramSpinners();
        saveSharedPref();
        if (number == 1) {
            mBinding.spinnerRobot1.setSelection(idx);
        } else if (number == 2) {
            mBinding.spinnerRobot2.setSelection(idx);
        } else if (number == 3) {
            mBinding.spinnerRobot3.setSelection(idx);
        }
    }

    // load program
    public void button_robot_loadProgram(int number) {
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
        if (number == 1) {
            st_send = "LOAD " + mBinding.spinnerRobot1.getSelectedItem().toString() + '\n';
        } else if (number == 2) {
            st_send = "LOAD " + mBinding.spinnerRobot2.getSelectedItem().toString() + '\n';
        } else if (number == 3) {
            st_send = "LOAD " + mBinding.spinnerRobot3.getSelectedItem().toString() + '\n';
        }

        MainRenewActivity.SendThread thread = new MainRenewActivity.SendThread(st_send, (number - 1));
        thread.start();
    }

    // add message
    public void message_robot(String st, int number) {
        if (number == 1) {
            mBinding.etRobotMessage1.append(st);
        } else if (number == 2) {
            mBinding.etRobotMessage2.append(st);
        } else if (number == 3) {
            mBinding.etRobotMessage3.append(st);
        }

    }

    public void addMessage(String st, int Number) {
        mBinding.etRobotMessage1.append(st);
    }

    //control robot
    public void button_robot_control(String state, int number) {
        MainRenewActivity.SendThread thread = new MainRenewActivity.SendThread(state + "\n", (number - 1));
        thread.start();
    }

    //exit


    //=====================================
    // endregion // event
    //=====================================

    //=====================================
    // region // method
    //=====================================

    public void initSpinner() {
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

        mBinding.spinnerRobot1.setSelection(0);
        mBinding.spinnerRobot2.setSelection(0);
        mBinding.spinnerRobot3.setSelection(0);

        saveSharedPref();
    }

    public void loadSharedPref() {
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

    public void updateProgramSpinners() {
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this, R.layout.spinner_item, spinner_items1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.spinner_item, spinner_items2);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(this, R.layout.spinner_item, spinner_items3);

        mBinding.spinnerRobot1.setAdapter(adapter1);
        mBinding.spinnerRobot2.setAdapter(adapter2);
        mBinding.spinnerRobot3.setAdapter(adapter3);
    }

    public void saveSharedPref() {
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


    //=====================================
    // endregion // method
    //=====================================

    //=====================================
    // region // Thread
    //=====================================


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
                st_recv = st.substring(0, in_num);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message_robot("Connection success.\n", sock_num);
                        message_robot(st_recv + "\n", sock_num);
                    }
                });

            } catch (java.io.IOException ex) {

                bl_connected[sock_num] = false;
            } catch (Exception ex2) {

            } finally {

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


            } catch (java.io.IOException ex) {

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

                if (socket[sock_num].isConnected() == false)
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
                st_recv = st.substring(0, in_num);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message_robot(st_recv + "\n", sock_num);
                    }
                });


            } catch (java.io.IOException ex) {

            } catch (Exception ex2) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        message_robot("Fail.\n", sock_num);
                    }
                });
            }

        }
    }


    //=====================================
    // endregion // Thread
    //=====================================

}