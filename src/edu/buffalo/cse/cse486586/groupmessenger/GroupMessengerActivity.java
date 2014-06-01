package edu.buffalo.cse.cse486586.groupmessenger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import edu.buffalo.cse.cse486586.groupmessenger.Packet;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * GroupMessengerActivity is the main Activity for the assignment.
 * 
 * @author stevko
 *
 */
public class GroupMessengerActivity extends Activity {
    static final String TAG = GroupMessengerActivity.class.getSimpleName();
    static final String REMOTE_PORT0 = "11108";
    static final String REMOTE_PORT1 = "11112";
    static final String REMOTE_PORT2 = "11116";
    static final String REMOTE_PORT3 = "11120";
    static final String REMOTE_PORT4 = "11124";
    static final int SERVER_PORT = 10000;
    private ContentResolver cr;
    private int msgNumber;
    private String thisPort;
    int globalCount;
    int seq0=1,seq1=1,seq2=1,seq3=1,seq4=1;
    Map<Integer, String> buffer0=new HashMap<Integer, String>();
    Map<Integer, String> buffer1=new HashMap<Integer, String>();
    Map<Integer, String> buffer2=new HashMap<Integer, String>();
    Map<Integer, String> buffer3=new HashMap<Integer, String>();
    Map<Integer, String> buffer4=new HashMap<Integer, String>();
    Map<Integer, String> localBuffer=new HashMap<Integer, String>();

    Button SendB;

    private Uri buildUri(String scheme, String authority) {
        Uri.Builder uriBuilder = new Uri.Builder();
        uriBuilder.authority(authority);
        uriBuilder.scheme(scheme);
        return uriBuilder.build();
    }

    private ContentValues initTestValues() {
        ContentValues cv;
        cv = new ContentValues();
        cv.put("key", "key" + Integer.toString(2));
        cv.put("value", "val" + Integer.toString(2));
        return cv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_messenger);
        globalCount=1;
        msgNumber=1;

        /*
         * TODO: Use the TextView to display your messages. Though there is no grading component
         * on how you display the messages, if you implement it, it'll make your debugging easier.
         */
        final EditText editText = (EditText) findViewById(R.id.editText1);
        final TextView tv = (TextView) findViewById(R.id.textView1);
        SendB= (Button) findViewById(R.id.button4);
        cr=getContentResolver();
        tv.setMovementMethod(new ScrollingMovementMethod());

        // Test my port hack!! author: steveko
        TelephonyManager tel = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        String portStr = tel.getLine1Number().substring(tel.getLine1Number().length() - 4);
        final String myPort = String.valueOf((Integer.parseInt(portStr) * 2));
        thisPort=myPort;


        // Creating Server Sockets

        try {

            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            new ServerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, serverSocket);

        } catch (IOException e) {
            Log.e(TAG, "Can't create a ServerSocket");
            return;
        }


        // Server socket creation end

        findViewById(R.id.button1).setOnClickListener(
                new OnPTestClickListener(tv, getContentResolver()));

        //Implementing On-Click Listener for the Send Button:-

        SendB.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String msg = editText.getText().toString();
                editText.setText(""); 
                new ClientTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, msg, myPort);
            }
        });

    }

    private class ServerTask extends AsyncTask<ServerSocket, Packet, Void> {
        int count;
        @Override
        protected Void doInBackground(ServerSocket... sockets) {

            Packet Incoming,Broadcast;
            count=0;
            int localCount=1;

            try{
                ServerSocket serverSocket = sockets[0];
                while(true){
                    Socket accept= serverSocket.accept();
                    ObjectInputStream in = new ObjectInputStream(accept.getInputStream());
                    Incoming=(Packet) in.readObject();
                    if(thisPort.equals(REMOTE_PORT4)){

                        Broadcast=Incoming;

                        //Testing FIFO Ordering through the following switch statement.

                        switch(Incoming.sendersPort){
                            case 11108:

                                buffer0.put(Incoming.msgNo, Incoming.message);
                                Log.v("case 08", seq0+""+Incoming.msgNo);
                                while(buffer0.containsKey(seq0)){
                                    buffer0.remove(Broadcast.msgNo);
                                    Broadcast.msgNo=globalCount++;

                                    String remotePort[]={REMOTE_PORT1,REMOTE_PORT2,REMOTE_PORT3,REMOTE_PORT0};
                                    for(String s:remotePort)
                                    {

                                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                                Integer.parseInt(s));
                                        ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                                        out.writeObject(Broadcast);
                                        out.close();
                                        socket.close();
                                    }

                                    publishProgress(Broadcast);
                                    in.close();
                                    accept.close();
                                    seq0++;
                                } break; 
                            case 11112:

                                buffer1.put(Incoming.msgNo, Incoming.message);
                                Log.v("case 12", seq1+""+Incoming.msgNo);

                                while(buffer1.containsKey(seq1)){
                                    buffer1.remove(Incoming.msgNo);
                                    Broadcast.msgNo=globalCount++;
                                    String remotePort[]={REMOTE_PORT1,REMOTE_PORT2,REMOTE_PORT3,REMOTE_PORT0};
                                    for(String s:remotePort)
                                    {

                                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                                Integer.parseInt(s));
                                        ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                                        out.writeObject(Broadcast);
                                        out.close();
                                        socket.close();
                                    }
                                    publishProgress(Broadcast);
                                    in.close();
                                    accept.close();
                                    seq1++;
                                } break;

                            case 11116:

                                buffer2.put(Incoming.msgNo, Incoming.message);
                                Log.v("case 16", seq2+""+Incoming.msgNo);
                                while(buffer2.containsKey(seq2)){
                                    buffer2.remove(Incoming.msgNo);
                                    Broadcast.msgNo=globalCount++;
                                    String remotePort[]={REMOTE_PORT1,REMOTE_PORT2,REMOTE_PORT3,REMOTE_PORT0};
                                    for(String s:remotePort)
                                    {

                                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                                Integer.parseInt(s));
                                        ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                                        out.writeObject(Broadcast);
                                        out.close();
                                        socket.close();
                                    }
                                    publishProgress(Broadcast);
                                    in.close();
                                    accept.close();
                                    seq2++;
                                } break;

                            case 11120:

                                buffer3.put(Incoming.msgNo, Incoming.message);
                                Log.v("case 20", seq3+""+Broadcast.msgNo);
                                while(buffer3.containsKey(seq3)){
                                    buffer3.remove(Incoming.msgNo);
                                    Broadcast.msgNo=globalCount++;
                                    String remotePort[]={REMOTE_PORT1,REMOTE_PORT2,REMOTE_PORT3,REMOTE_PORT0};
                                    for(String s:remotePort)
                                    {

                                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                                Integer.parseInt(s));
                                        ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                                        out.writeObject(Broadcast);
                                        out.close();
                                        socket.close();
                                    }
                                    publishProgress(Broadcast);
                                    in.close();
                                    accept.close();
                                    seq3++;
                                }break;
                            case 11124:
                                buffer4.put(Incoming.msgNo, Incoming.message);
                                Log.v("case 24", seq4+""+Incoming.msgNo);
                                while(buffer4.containsKey(seq4)){
                                    buffer4.remove(Incoming.msgNo);
                                    Broadcast.msgNo=globalCount++;
                                    String remotePort[]={REMOTE_PORT1,REMOTE_PORT2,REMOTE_PORT3,REMOTE_PORT0};
                                    for(String s:remotePort)
                                    {

                                        Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                                                Integer.parseInt(s));
                                        ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                                        out.writeObject(Broadcast);
                                        out.close();
                                        socket.close();
                                    }
                                    publishProgress(Broadcast);
                                    in.close();
                                    accept.close();
                                    seq4++;
                                }break;

                            default:
                                Log.e("Error at ","Default");


                        }
                    }
                    else{	
                        //Testing Total Ordering through the following Buffer and LocalCount.
                        localBuffer.put(Incoming.msgNo, Incoming.message);
                        while(localBuffer.containsKey(localCount)){
                            publishProgress(new Packet(0,localBuffer.get(localCount),localCount));
                            localCount++;
                            in.close();
                            accept.close();
                        }

                    }

                }

                /*
                 * TODO: Fill in your server code that receives messages and passes them
                 * to onProgressUpdate().
                 */

            } 
            catch(Exception e){
                Log.e(TAG, "Problem while recieving");
                Log.e(TAG, e.toString());
            }


            return null;
        }

        protected void onProgressUpdate(Packet...strings) {

            Uri uri = buildUri("content", "edu.buffalo.cse.cse486586.groupmessenger.provider");
            ContentValues cv=initTestValues();

            Packet strReceived = strings[0];
            String message=strReceived.message;
            int test=strReceived.msgNo;

            cv.put("key",count+"");
            cv.put("value",message);
            TextView tv = (TextView) findViewById(R.id.textView1);
            tv.append(test+": "+message+"\n");
            try{
                cr.insert(uri,cv);
            }
            catch(Exception e)
            {
                tv.append(e.getLocalizedMessage());
            }
            ++count;

        }


    }
    private class ClientTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... msgs) {
            try {
                int myport=Integer.parseInt(msgs[1]);

                String remotePort[]={REMOTE_PORT4};

                for(String s:remotePort)
                {
                    Socket socket = new Socket(InetAddress.getByAddress(new byte[]{10, 0, 2, 2}),
                            Integer.parseInt(s));
                    String msgToSend = msgs[0];//+myport+" : "
                    Packet p=new Packet(myport,msgToSend,msgNumber++);
                    ObjectOutputStream out=new ObjectOutputStream(socket.getOutputStream());
                    out.writeObject(p);
                    out.close();
                    socket.close();
                }
            } 
            catch (UnknownHostException e) {
                Log.e(TAG, "ClientTask UnknownHostException");
            } catch (IOException e) {
                Log.e(TAG, "ClientTask socket IOException");
            }

            return null;
        }
    }

}



