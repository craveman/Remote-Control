package ru.inspirationpoint.inspirationrc.ui.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.inspirationpoint.inspirationrc.R;
import ru.inspirationpoint.inspirationrc.manager.helpers.UDPHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.CommandHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.TCPHelper;
import ru.inspirationpoint.inspirationrc.tcpHandle.commands.CommandsContract;

public class TestActivity extends AppCompatActivity implements TCPHelper.TCPListener{

    Map<String, String> addresses = Collections.synchronizedMap(new HashMap<String, String>());
    UDPHelper udp;
    Pinger pinger;
    Handler handler;
    String ipSelected;
    TCPHelper tcpHelper;
    Boolean timerStarted = false;
    Boolean isPriorityLeft = false;
    Boolean isPriorityRight = false;
    int code;
    Boolean isCardLeft = false;
    Boolean isCardRight = false;

    @BindView(R.id.udp)
    Button udpBtn;
    @BindView(R.id.tcp_init)
    Button init;
    @BindView(R.id.tcp_start_timer)
    Button start;
    @BindView(R.id.tcp_set_card_left)
    Button cardLeft;
    @BindView(R.id.tcp_set_card_right)
    Button cardRight;
    @BindView(R.id.tcp_set_name_left)
    Button nameLeft;
    @BindView(R.id.tcp_set_name_right)
    Button nameRight;
    @BindView(R.id.tcp_set_name_referee)
    Button nameReferee;
    @BindView(R.id.tcp_set_period)
    Button period;
    @BindView(R.id.tcp_set_priority_left)
    Button priorityLeft;
    @BindView(R.id.tcp_set_priority_right)
    Button priorityRight;
    @BindView(R.id.tcp_set_score_left)
    Button scoreLeft;
    @BindView(R.id.tcp_set_score_right)
    Button scoreRight;
    @BindView(R.id.tcp_set_timer)
    Button timer;
    @BindView(R.id.tcp_set_weapon)
    Button weapon;
    @BindView(R.id.et_left_name)
    EditText etLeftName;
    @BindView(R.id.et_right_name)
    EditText etRightName;
    @BindView(R.id.et_referee_name)
    EditText etRefereeName;
    @BindView(R.id.et_left_score)
    EditText etLeftScore;
    @BindView(R.id.et_right_score)
    EditText etRightScore;
    @BindView(R.id.et_left_period)
    EditText etPeriod;
    @BindView(R.id.et_timer)
    EditText etTimer;
    @BindView(R.id.et_weapon)
    EditText etWeapon;

    @OnClick({R.id.udp, R.id.tcp_init, R.id.tcp_start_timer, R.id.tcp_set_card_left, R.id.tcp_set_card_right,
            R.id.tcp_set_name_left, R.id.tcp_set_name_right, R.id.tcp_set_period, R.id.tcp_set_priority_left,
            R.id.tcp_set_priority_right, R.id.tcp_set_score_left, R.id.tcp_set_score_right, R.id.tcp_set_timer,
            R.id.tcp_set_weapon, R.id.tcp_set_name_referee})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.udp:
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pinger = new Pinger();
                        pinger.run();
                    }
                });
                t.start();
                break;
            case R.id.tcp_init:
                try {
                    tcpHelper.send(CommandHelper.init(code));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_start_timer:
                timerStarted = !timerStarted;
                try {
                    tcpHelper.send(CommandHelper.startTimer(timerStarted));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_card_left:
                try {
                    isCardLeft = !isCardLeft;
                    tcpHelper.send(CommandHelper.setCard(CommandsContract.PERSON_TYPE_LEFT, isCardLeft));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_card_right:
                try {
                    isCardRight = !isCardRight;
                    tcpHelper.send(CommandHelper.setCard(CommandsContract.PERSON_TYPE_RIGHT, isCardRight));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_name_left:
                try {
                    tcpHelper.send(CommandHelper.setName(CommandsContract.PERSON_TYPE_LEFT,
                            String.valueOf(TextUtils.isEmpty(etLeftName.getText()) ? "testLeft" : etLeftName.getText())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_name_right:
                try {
                    tcpHelper.send(CommandHelper.setName(CommandsContract.PERSON_TYPE_RIGHT,
                            String.valueOf(TextUtils.isEmpty(etRightName.getText()) ? "testRight" : etRightName.getText())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_name_referee:
                try {
                    tcpHelper.send(CommandHelper.setName(CommandsContract.PERSON_TYPE_REFEREE,
                            String.valueOf(TextUtils.isEmpty(etRefereeName.getText()) ? "testReferee" : etRefereeName.getText())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_period:
                try {
                    tcpHelper.send(CommandHelper.setPeriod(TextUtils.isEmpty(etPeriod.getText())?1: Integer.parseInt(etPeriod.getText().toString())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_priority_left:
                try {
                    isPriorityLeft = !isPriorityLeft;
                    tcpHelper.send(CommandHelper.setPriority(CommandsContract.PERSON_TYPE_LEFT, isPriorityLeft));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_priority_right:
                try {
                    isPriorityRight = !isPriorityRight;
                    tcpHelper.send(CommandHelper.setPriority(CommandsContract.PERSON_TYPE_RIGHT, isPriorityRight));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_score_left:
                try {
                    tcpHelper.send(CommandHelper.setScore(CommandsContract.PERSON_TYPE_LEFT,
                            TextUtils.isEmpty(etLeftScore.getText())?0: Integer.parseInt(etLeftScore.getText().toString())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_score_right:
                try {
                    tcpHelper.send(CommandHelper.setScore(CommandsContract.PERSON_TYPE_RIGHT,
                            TextUtils.isEmpty(etRightScore.getText())?0: Integer.parseInt(etRightScore.getText().toString())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_timer:
                try {
                    tcpHelper.send(CommandHelper.setTimer(TextUtils.isEmpty(etTimer.getText())?0: Long.parseLong(etTimer.getText().toString())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tcp_set_weapon:
                try {
                    tcpHelper.send(CommandHelper.setWeapon(TextUtils.isEmpty(etWeapon.getText())?1:
                                    Integer.parseInt(etWeapon.getText().toString())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        ButterKnife.bind(this);
        handler = new Handler();
        enableButtons(false);
    }

    private void enableButtons(boolean enable) {
        init.setClickable(enable);
        start.setClickable(enable);
        cardLeft.setClickable(enable);
        cardRight.setClickable(enable);
        nameLeft.setClickable(enable);
        nameRight.setClickable(enable);
        nameReferee.setClickable(enable);
        period.setClickable(enable);
        priorityLeft.setClickable(enable);
        priorityRight.setClickable(enable);
        scoreLeft.setClickable(enable);
        scoreRight.setClickable(enable);
        timer.setClickable(enable);
        weapon.setClickable(enable);
        init.setEnabled(enable);
        start.setEnabled(enable);
        cardLeft.setEnabled(enable);
        cardRight.setEnabled(enable);
        nameLeft.setEnabled(enable);
        nameRight.setEnabled(enable);
        nameReferee.setEnabled(enable);
        period.setEnabled(enable);
        priorityLeft.setEnabled(enable);
        priorityRight.setEnabled(enable);
        scoreLeft.setEnabled(enable);
        scoreRight.setEnabled(enable);
        timer.setEnabled(enable);
        weapon.setEnabled(enable);
    }

    @Override
    public void onReceive(byte[] message) {
        Log.d("ACTIVITY", "Bytes: " + Arrays.toString(message));

    }

    private class Pinger extends Thread {
        private boolean running;

        @Override
        public void run() {
            try {
                udp = new UDPHelper(getApplicationContext(), new UDPHelper.BroadcastListener() {
                    @Override
                    public void onReceive(String msg, final String ip) {
                        Log.d("RECEIVED", "receive message "+msg+" from "+ip);
                        if (!addresses.keySet().contains(ip) && Integer.parseInt(msg) < 10000) {
                            addresses.put(ip, msg);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayout linearLayout = new LinearLayout(TestActivity.this);
                                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(TestActivity.this, android.R.layout.select_dialog_singlechoice);
                                    for (String key : addresses.keySet()) {
                                        arrayAdapter.add(key);
                                    }
                                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(TestActivity.this);
                                    builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ipSelected = arrayAdapter.getItem(i);
                                            code = Integer.parseInt(addresses.get(ipSelected));
                                            Thread t = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    tcpHelper = new TCPHelper(ipSelected);
                                                    tcpHelper.setListener(TestActivity.this);
                                                    tcpHelper.run();
                                                }
                                            });
                                            t.start();
                                            enableButtons(true);
                                            dialogInterface.dismiss();
                                            end();
                                        }
                                    });
                                    builderSingle.show();
                                }
                            });
                        }
                    }
                });
                udp.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
            running = true;
            while (running) {
                try {
                    udp.send("PIN");
                    Log.d("sended", "ping sended ....");
                    sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        public void end() {
            running = false;
            udp.end();
        }
    }
}
