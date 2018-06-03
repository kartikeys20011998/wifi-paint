package com.example.kartikey.wifipaint_project;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.kartikey.wifipaint_project.R.id.txt;

public class Wifi_Connectivity extends AppCompatActivity {
    WifiP2pManager mManager;
    WifiP2pManager.Channel channel;
    WifiDirectBroadCastReciever reciever;
    IntentFilter intentFilter;
    TextView txt,txt2;
    List<WifiP2pDevice> peers= new ArrayList<WifiP2pDevice>();
    ListView lv;
    Intent datadisplay;
    ArrayAdapter wifiadapter;
    int pos;
    EditText et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_wifi__connectivity);

       // Toast.makeText(getApplicationContext(), "on Create() CALLED", Toast.LENGTH_LONG).show();
        //txt = (TextView) findViewById(R.id.txt);
        txt2= (TextView)findViewById(R.id.txt2);
        mManager = (WifiP2pManager)getSystemService(WIFI_P2P_SERVICE);
        channel = mManager.initialize(this,getMainLooper(),null);
        reciever = new WifiDirectBroadCastReciever(mManager,channel,this);
       //   et = (EditText) findViewById(R.id.et);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        registerReceiver(reciever, intentFilter);

        wifiadapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1);
        lv = (ListView) findViewById(R.id.lv);
       // Toast.makeText(getApplicationContext(), "Adapter set ", Toast.LENGTH_LONG).show();
        datadisplay=new Intent(this,MainActivity.class);

        //STARTING THE DISCOVERY OF PEERS

        mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "DISCOVERY PROCESS INTIALISED  ", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), " DISCOVERY PROCESS NOT INITIALISED", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(getApplicationContext(), "on RESUME CALLED ", Toast.LENGTH_SHORT).show();
        registerReceiver(reciever, intentFilter);
        //PEER DISCOVERY STARTED
        mManager.discoverPeers(channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getApplicationContext(), "DISCOVERY PROCESS INTIALISED  ", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(getApplicationContext(), " DISCOVERY PROCESS NOT INITIALISED", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(reciever);
    }

      public void play(InetAddress hostaddress, boolean host)
      {
        datadisplay.putExtra("HostAddress",hostaddress.getHostAddress());
        datadisplay.putExtra("Host",host);
        datadisplay.putExtra("Connected",true);
          startActivity(datadisplay);
      }


    //INNER BROADCAST RECIEVER CLASS
    public class WifiDirectBroadCastReciever extends BroadcastReceiver {
        private WifiP2pManager mManager;
        private WifiP2pManager.Channel mChannel;
        private Wifi_Connectivity mainActivity;

        public WifiDirectBroadCastReciever(WifiP2pManager mManager, WifiP2pManager.Channel mChannel, Wifi_Connectivity mainActivity) {
            this.mManager = mManager;
            this.mChannel = mChannel;
            this.mainActivity = mainActivity;
        }

        // FECTHING THE LIST OF PEERS
        //LISTENERS
        WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peerList) {
                Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();

                if (!refreshedPeers.equals(peers)) {
                    Toast.makeText(getApplicationContext(), " Non-zero PEERS ", Toast.LENGTH_SHORT).show();
                    peers.clear();
                    peers.addAll(refreshedPeers);
                    wifiadapter.clear();
                    wifiadapter.addAll(peers);
                }
                if (peers.size() == 0)
                    Toast.makeText(getApplicationContext(), " NO PEERS FOUND ", Toast.LENGTH_SHORT).show();
            }
        };
// CALLED WHEN THE DEVICES ARE CONNECTED
        WifiP2pManager.ConnectionInfoListener connectionInfoListener = new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                // InetAddress from the WIfiP2pInfo Struct
                InetAddress inetAddress = info.groupOwnerAddress;
                if (info.groupFormed && info.isGroupOwner) {
                    //TASKS FOR THE SPECIFIC GROUP OWNER
                    //CREATING GROUP OWNER THREAD AND ACCEPTING INCOMING CONNECTIONS
                    txt2.setText("HOST");
                    mainActivity.play(inetAddress,true);

                } else if (info.groupFormed) {
                    //The  other device acts as the client
                    // Create a peer(client) thread that connects to the group owner
                    txt2.setText("CLIENT");
                    mainActivity.play(inetAddress,false);
                }

            }
        };

  //METHOD CALLED TO CONNECT TWO DEVICES

        public void onConnect(int position) {
            try {
                Toast.makeText(getApplicationContext(),"onConnect ENTERED", Toast.LENGTH_SHORT).show();
                WifiP2pDevice device = peers.get(position);
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = device.deviceAddress;
                config.wps.setup = WpsInfo.PBC;
                mManager.connect(channel, config, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                     //   Toast.makeText(getApplicationContext(), "CONNECTION SUCCESSFULL ", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                       // Toast.makeText(getApplicationContext(), "CONNECTION FAILED.RETRY", Toast.LENGTH_LONG).show();
                    }
                });
                /* mManager.requestGroupInfo(channel, new WifiP2pManager.GroupInfoListener() {
                @Override
                public void onGroupInfoAvailable(WifiP2pGroup group) {
                    String groupPwd=group.getPassphrase();

                }
            });*/
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "onConnect() EXCEPTION OCCURRED :" + e, Toast.LENGTH_LONG).show();
                System.out.println("onConnect() EXCEPTION OCCURRED :" + e);
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                //Check to see if wifi is enabled or disabled on the device
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    Toast.makeText(context, "WIFI P2p SERVICE ACTIVE ", Toast.LENGTH_SHORT).show();
                    System.out.println("WIFI P2P ENABLED ");
                } else {
                    System.out.println("WIFI P2P NOT ENABLED ");
                    Toast.makeText(context, " WIFI P2p Service INACTIVE ", Toast.LENGTH_SHORT).show();
                }
            }

            else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                //Call WifiP2pManager.requestPeers() to get the list of current Peers
                Toast.makeText(context, "Peers CHANGING ", Toast.LENGTH_SHORT).show();
                if (mManager != null)
                    mManager.requestPeers(channel, peerListListener);
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pos = position;
                        reciever.onConnect(pos);
                    }
                });

            }

            else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                //Respond to a new connection or disconnection
                if (mManager == null)
                    return;
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
                if (networkInfo.isConnected()) {
                    //We are connected with the other device ,request Connection
                    //info to find group owner ip
                    mManager.requestConnectionInfo(channel,connectionInfoListener);
                    Toast.makeText(getApplicationContext(), intent.getAction(), Toast.LENGTH_LONG).show();
                    //Toast.makeText(getApplicationContext(), "CONNECTION ESTABLISHED", Toast.LENGTH_LONG).show();
                }
            }

            else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                //Respond to the device's wifi state changing

            }
            else {

                Toast.makeText(getApplicationContext(), " ELSE EXECUTED ", Toast.LENGTH_LONG).show();
                wifiadapter.add(intent.getAction());

            }
            lv.setAdapter(wifiadapter);
           // txt.setText(action);

        }
    }


}