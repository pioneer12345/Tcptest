package com.example.administrator.tcptest;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private Handler handler;
    private Button Connect;
    private Button Send;
    private Button Cmd1;
    private Button Cmd2;
    private Button Cmd3;
    private EditText et_ip;
    private EditText et_port;
    private TextView rec;
    private Socket s;
    private int offset;

    private String ip;
    private int port;
    private boolean isFirst=true;
    private byte[] flush;
    private String str;

    private DataOutputStream dos;
    private DataInputStream dis;



    /**
     * 点击事件响应
     * @param v
     */
    public void onConnectClicked(View v){

        Thread thread1 = new readThread(MainActivity.this, handler);
        thread1.start();
    }
    public void onSendClicked(View v){
        Thread thread2 = new writeThread(MainActivity.this, handler);
        thread2.start();

    }
    public void onCmd1Clicked(View v){
                isFirst = false;
    }
    public void onCmd2Clicked(View v){

    }
    public void onCmd3Clicked(View v){

    }
    public void onClearClicked(View v){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                rec.setText(null);
            }
        });


    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //组件获取
        Connect = (Button) this.findViewById(R.id.connect);
        Send = (Button) this.findViewById(R.id.send);
        Cmd1 = (Button) this.findViewById(R.id.cmd1);
        Cmd2 = (Button) this.findViewById(R.id.cmd2);
        Cmd3 = (Button) this.findViewById(R.id.cmd3);
        rec = (TextView) this.findViewById(R.id.receive);
        rec.setMovementMethod(ScrollingMovementMethod.getInstance());
        et_ip = (EditText) this.findViewById(R.id.et_ip);
        et_port= (EditText) this.findViewById(R.id.et_ip);

        String str1="192.168.16.254";
        String str2="8080";
        et_ip = (EditText) this.findViewById(R.id.et_ip);
        et_port= (EditText) this.findViewById(R.id.et_port);
        et_ip.setTextColor(Color.BLACK);
        et_ip.setText(str1.toCharArray(), 0, str1.length());
        et_port.setTextColor(Color.BLACK);
        et_port.setText(str2.toCharArray(), 0, str2.length());

        handler = new Handler() {
            public void handleMessage(Message msg) {
                MainActivity.this.rec.append(msg.obj.toString());
                offset = rec.getLineCount()*rec.getLineHeight();
                if(offset>rec.getHeight()){
                    rec.scrollTo(0,offset-rec.getHeight());
                }
            }
        };



    }
    class readThread extends Thread{
        private MainActivity ctx;
        Handler hd;
        Socket s=null;

        public readThread(MainActivity ctx,Handler hd){
            this.ctx = ctx;
            this.hd=hd;
        }
        public void run(){
            //获取输入的远方服务器的IP地址
            ctx.ip=((EditText)(ctx.findViewById(R.id.et_ip))).getText().toString();
            //获取输入的远方服务器的port
            ctx.port=Integer.parseInt(
                    ((EditText)(ctx.findViewById(R.id.et_port))).getText().toString()
            );
            //向远方发起TCP连接
            try {
                isFirst=true;
                s=new Socket(ctx.ip,ctx.port);
                //获取要发送的字符串
                DataInputStream dis=new DataInputStream(s.getInputStream());
                hd.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(MainActivity.this,"连接成功",Toast.LENGTH_LONG).show();

                    }
                });
               while(isFirst){
                   flush =new byte[512];
                   int len = 0;
                   dis.read(flush);
                   Log.i("Socket Client",new String(flush));
                   ctx.str = new String(flush, "GB2312").trim();
                   Message msg = new Message();
                   msg.obj = ctx.str;
                   MainActivity.this.handler.sendMessage(msg);
               }
                dis.close();
                s.close();

            } catch (Exception e1){
                // TODO Auto-generated catch block
                //e1.printStackTrace();
                final String errMsg=e1.getMessage();
                hd.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(ctx,"连接失败："+errMsg,Toast.LENGTH_LONG).show();
                    }
                });

            }//try

        }//run

    }//MyThread
    class writeThread extends Thread{
        private MainActivity ctx;
        Handler hd;
        Socket s=null;

        public writeThread(MainActivity ctx,Handler hd){
            this.ctx = ctx;
            this.hd=hd;
        }
        public void run(){
            //获取输入的远方服务器的IP地址
            ctx.ip=((EditText)(ctx.findViewById(R.id.et_ip))).getText().toString();
            //获取输入的远方服务器的port
            ctx.port=Integer.parseInt(
                    ((EditText)(ctx.findViewById(R.id.et_port))).getText().toString()
            );
            //向远方发起TCP连接
            try {
                s=new Socket(ctx.ip,ctx.port);
                //获取要发送的字符串
                String data=((EditText)(ctx.findViewById(R.id.et_text))).getText().toString();
                //byte[] data1=data.getBytes();
                byte[] info = data.getBytes();
                DataOutputStream dos=new DataOutputStream(s.getOutputStream());
                //将字符串按：UTF-8字节流方式传输。先传输长度，再传输字节内容。
                dos.write(info,0,info.length);
                dos.flush();
                dos.close();
                s.close();
                hd.post(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(MainActivity.this,"已发送",Toast.LENGTH_LONG).show();
                    }
                });
            } catch (Exception e1){
                // TODO Auto-generated catch block
                //e1.printStackTrace();
                final String errMsg=e1.getMessage();
                hd.post(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        Toast.makeText(ctx,"发送失败"+errMsg,Toast.LENGTH_LONG).show();
                    }
                });

            }//try

        }//run

    }//MyThread



}








