package com.shubham.ble_application;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.shubham.ble_application.entity.CustomAdapter;
import com.shubham.ble_application.entity.ScannedInformation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements BleWrapperUiCallbacks {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.shubham.ble_application/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.shubham.ble_application/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    private class Msg_to_Alfred {
        private byte MsgType;
        private String Msg;
    }

    private static final int BOND_NONE = 10;        //10 = Remote device is not BONDED
    private Msg_to_Alfred erase_configuration = new Msg_to_Alfred();
    private Msg_to_Alfred Dfu_mode = new Msg_to_Alfred();
    private Msg_to_Alfred Write_Id = new Msg_to_Alfred();
    private Msg_to_Alfred Write_Config_Complete = new Msg_to_Alfred();
    private TextView tV1;
    private Add abc;
    private Button BScan;
    private Button BStopScan;
    private Button BErase;
    private Button BDfu;
    private Button BAssignId;
    private Button BConfigComplete;
    private RadioGroup Version_group;
    private RadioButton Version;
    private EditText newID;
    private EditText newNtId;
    private BleWrapper myBleWrapper = null;
    final static private UUID ALFRED_SERVICE = UUID.fromString("00009001-1212-efde-1523-785feabcd123");
    final static private UUID ALFRED_SERVICEa = UUID.fromString("0000900a-1212-efde-1523-785feabcd123");
    final static private UUID ALFRED_SERVICEb = UUID.fromString("0000900b-1212-efde-1523-785feabcd123");
    ArrayList<ScannedInformation> DeviceList = new ArrayList<>();
    private BluetoothGatt connected_gatt = null;
    private BluetoothDevice connected_device = null;
    private List<BluetoothGattService> connected_services = null;
    private ListView LVDevices;
    CustomAdapter adapter;
    private BluetoothGattCharacteristic characteristic;
    private Handler handler = new Handler();
    BluetoothGattCharacteristic write_char;

    private Handler MainThreadHandler = new Handler(Looper.getMainLooper());

    public void stop_scan() {
        myBleWrapper.stopScanning();
        BScan.setEnabled(true);
        BStopScan.setEnabled(false);
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            stop_scan();
            BScan.setEnabled(true);
            BStopScan.setEnabled(false);
        }
    };

    private void start_scan() {
        tV1.setText("");
        Set<BluetoothDevice> pairedDevicesSet = myBleWrapper.getAdapter().getBondedDevices();
        Object[] pairedDevices = pairedDevicesSet.toArray();
        for (int i = 0; i < pairedDevicesSet.size(); i++) {
            try {
                Method method = pairedDevices[i].getClass().getMethod("removeBond", (Class[]) null);
                method.invoke(pairedDevices[i], (Object[]) null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        DeviceList.clear();
        adapter = new CustomAdapter(this, R.layout.list_item, DeviceList);
        LVDevices.setAdapter(adapter);
        myBleWrapper.startScanning();
        BScan.setEnabled(false);
        BStopScan.setEnabled(true);
        Version_group.clearCheck();
        handler.postDelayed(runnable, 20000);
    }

    private void DisableRadioGroup() {
        for (int i = 0; i < Version_group.getChildCount(); i++) {
            Version_group.getChildAt(i).setEnabled(false);
        }
    }

    private void EnableRadioGroup() {
        for (int i = 0; i < Version_group.getChildCount(); i++) {
            Version_group.getChildAt(i).setEnabled(true);
        }
    }

    private void DisableFunctionalities(){
        BErase.setEnabled(false);
        BDfu.setEnabled(false);
        BAssignId.setEnabled(false);
        BConfigComplete.setEnabled(false);
        newID.setText("");
        newNtId.setText("");
        newID.setEnabled(false);
        newNtId.setEnabled(false);
        DisableRadioGroup();
    }

    private void EnableFunctionalities(){
        BErase.setEnabled(true);
        BDfu.setEnabled(true);
        newID.setEnabled(true);
        newNtId.setEnabled(true);
        BAssignId.setEnabled(true);
        BConfigComplete.setEnabled(true);
        EnableRadioGroup();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        erase_configuration.MsgType = 99;
        erase_configuration.Msg = "ERASE SWITCH UNIT CONFIGURATION";
        Dfu_mode.MsgType = 16;
        Dfu_mode.Msg = "OTA DFU MODE";
        Write_Id.MsgType = 3;
        Write_Id.Msg = "ASSIGN ID";
        Write_Config_Complete.MsgType = 10;
        Write_Config_Complete.Msg = "WRITE CONFIG COMPLETE";

        tV1 = (TextView) findViewById(R.id.tV1);
        newID = (EditText)findViewById(R.id.ETid);
        newNtId = (EditText)findViewById(R.id.ETnt_id);
        Version_group = (RadioGroup) findViewById(R.id.version);
        BScan = (Button) findViewById(R.id.BEnableBle);
        LVDevices = (ListView) findViewById(R.id.LVDevices);
        BStopScan = (Button) findViewById(R.id.BStopScan);
        BErase = (Button) findViewById(R.id.BErase);
        BDfu = (Button) findViewById(R.id.BDfu);
        BAssignId = (Button)findViewById(R.id.AssignId);
        BConfigComplete = (Button)findViewById(R.id.ConfigComplete);
        DisableFunctionalities();
        Version_group.clearCheck();
        DisableRadioGroup();

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        myBleWrapper = new BleWrapper(this, this);

        if (myBleWrapper.checkBleHardwareAvailable() == true) {
            if (myBleWrapper.isBtEnabled() == false) {
                tV1.setTextColor(Color.parseColor("#ff0000"));
                tV1.setText("Enable BLE");
            } else {
                tV1.setTextColor(Color.parseColor("#000000"));
                tV1.setText("BLE is on");
            }
            myBleWrapper.initialize();
        }

        LVDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                myBleWrapper.diconnect();       //currently single device can only connect
                if (!(myBleWrapper.isConnected())) {
                    boolean isConnected = myBleWrapper.connect(DeviceList.get(position).getDevice().getAddress());
                    BluetoothDevice ConnectedDevice = DeviceList.get(position).getDevice();
                }
            }
        });

        tV1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Bluetooth is not enabled. Request to user to turn it on
                BluetoothAdapter myBleAdapter = BluetoothAdapter.getDefaultAdapter();
                myBleAdapter.enable();

                if (myBleWrapper.isBtEnabled() == true) {
                    tV1.setTextColor(Color.parseColor("#000000"));
                    tV1.setText("Yo ble is on");
                }
//                        else
//                        {
//                            tV1.setText("ble is off");
//                        }
            }
        });
//        abc = new Add();
        BStopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myBleWrapper.isBtEnabled() == true) {
                    stop_scan();
                    tV1.setTextColor(Color.parseColor("#000000"));
                } else {
                    tV1.setTextColor(Color.parseColor("#ff0000"));
                    tV1.setText("Enable BLE");
                }
            }
        });

        BScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (myBleWrapper.isBtEnabled() == true) {
                    DeviceList = new ArrayList<>();
                    myBleWrapper.initialize();
                    if (connected_device != null) {
                        try {
                            Method method = connected_device.getClass().getMethod("removeBond", (Class[]) null);
                            method.invoke(connected_device, (Object[]) null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DisableFunctionalities();
                    }
                    start_scan();
                    tV1.setTextColor(Color.parseColor("#000000"));
                } else {
                    tV1.setTextColor(Color.parseColor("#ff0000"));
                    tV1.setText("Enable BLE");
                }
            }
        });

        BErase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] arr = new byte[3];
                if (Version_group.getCheckedRadioButtonId() == -1) {  //means default version
                    arr[0] = erase_configuration.MsgType;
                } else {               //currently version 10
                    arr[0] = 0;
                    arr[1] = 1;
                    arr[2] = erase_configuration.MsgType;
                }
                characteristic.setValue(arr);
                connected_gatt.writeCharacteristic(characteristic);
            }
        });

        BDfu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] arr = new byte[3];
                if (Version_group.getCheckedRadioButtonId() == -1) {  //means default version
                    arr[0] = Dfu_mode.MsgType;
                } else {               //currently version 10
                    arr[0] = 0;
                    arr[1] = 1;
                    arr[2] = Dfu_mode.MsgType;
                }
                characteristic.setValue(arr);
                connected_gatt.writeCharacteristic(characteristic);
            }
        });

        BAssignId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = 0xFF, network_id = 0xFF;
                if(newID.getText().length()>0)
                    id = (Integer.parseInt(newID.getText().toString()) & 0xFF);
                if(newNtId.getText().length()>0)
                    network_id = (Integer.parseInt(newNtId.getText().toString()) & 0xFF);
                if(id>8 || id<1) {
                    Toast.makeText(MainActivity.this, "Invalid ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(network_id>255) {
                    Toast.makeText(MainActivity.this, "Invalid NetworK ID", Toast.LENGTH_SHORT).show();
                    return;
                }
                byte[] arr = new byte[5];
                if (Version_group.getCheckedRadioButtonId() == -1) {  //means default version
                    arr[0] = Write_Id.MsgType;
                    arr[1] = (byte)id;
                    arr[2] = (byte)network_id;
                } else {               //currently version 10
                    arr[0] = 0;
                    arr[1] = 1;
                    arr[2] = Write_Id.MsgType;
                    arr[3] = (byte)id;
                    arr[4] = (byte)network_id;
                }
                characteristic.setValue(arr);
                connected_gatt.writeCharacteristic(characteristic);
            }
        });

        BConfigComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] arr = new byte[3];
                if (Version_group.getCheckedRadioButtonId() == -1) {  //means default version
                    arr[0] = Write_Config_Complete.MsgType;
                } else {               //currently version 10
                    arr[0] = 0;
                    arr[1] = 1;
                    arr[2] = Write_Config_Complete.MsgType;
                }
                characteristic.setValue(arr);
                connected_gatt.writeCharacteristic(characteristic);
            }
        });

        adapter = new CustomAdapter(this, R.layout.list_item, DeviceList);
        LVDevices.setAdapter(adapter);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void uiDeviceFound(BluetoothDevice device, int rssi, byte[] record) {
//        tV1.setText(device.getName());
        ScannedInformation ScannedDevice = new ScannedInformation();
        boolean found = false;
        ScannedDevice.setDevice(device);
        ScannedDevice.setRecord(record);
        ScannedDevice.setRssi(rssi);
        int len = adapter.getCount();
        for (int i = 0; i < len; i++) {
            if (device.getAddress().equals(DeviceList.get(i).getDevice().getAddress())) {
                found = true;
                DeviceList.get(i).setRssi(rssi);
                adapter.notifyDataSetChanged();
            }
        }
        if (found == false) {
            DeviceList.add(ScannedDevice);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void uiDeviceConnected(final BluetoothGatt gatt, final BluetoothDevice device) {

        MainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(MainActivity.this, "Device_connected", Toast.LENGTH_SHORT).show();
                tV1.setText("Connected  " + device.getName().toString() + "     "+ device.getAddress());
                if(device.getBondState() == BOND_NONE)
                {
                    if (device.createBond()) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        gatt.getServices();
                    } else {
                        tV1.setText("Connected, but unable to Bond");
                        myBleWrapper.diconnect();
                    }
                }
            }
        });
    }

    @Override
    public void uiDeviceDisconnected(BluetoothGatt gatt, BluetoothDevice device) {
        MainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(MainActivity.this, "Device_Disconnected", Toast.LENGTH_SHORT).show();
                connected_services = null;
                connected_device = null;
                connected_gatt = null;
                DisableFunctionalities();
                if (connected_device != null) {
                    try {
                        Method method = connected_device.getClass().getMethod("removeBond", (Class[]) null);
                        method.invoke(connected_device, (Object[]) null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tV1.setText("Dis-Connected");
                }
            }
        });
    }

    @Override
    public void uiAvailableServices(final BluetoothGatt gatt, final BluetoothDevice device, final List<BluetoothGattService> services) {
        connected_services = services;
        connected_device = device;
        connected_gatt = gatt;

        MainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                if (myBleWrapper.isConnected()) {
                    EnableFunctionalities();
                    Toast.makeText(MainActivity.this, "Enabled", Toast.LENGTH_SHORT).show();
                }
                //main code, commented because of buttons Erase and DFU

                for (BluetoothGattService service : connected_services) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();
                    for (BluetoothGattCharacteristic charecs : characteristics) {
                        if (charecs.getUuid().toString().contains("0000900a")) {
                            characteristic = charecs;
                        }
                    }

                }
            }
        });
    }

    @Override
    public void uiCharacteristicForService(BluetoothGatt gatt, BluetoothDevice device, final BluetoothGattService service, List<BluetoothGattCharacteristic> chars) {

    }

    @Override
    public void uiCharacteristicsDetails(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void uiNewValueForCharacteristic(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String strValue, int intValue, byte[] rawValue, String timestamp) {

    }

    @Override
    public void uiGotNotification(BluetoothGatt gatt, BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic characteristic) {

    }

    @Override
    public void uiSuccessfulWrite(BluetoothGatt gatt, final BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String description) {
        MainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "written", Toast.LENGTH_SHORT).show();
                try {
                    Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Bond Removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void uiFailedWrite(BluetoothGatt gatt, final BluetoothDevice device, BluetoothGattService service, BluetoothGattCharacteristic ch, String description) {
        MainThreadHandler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "failed", Toast.LENGTH_SHORT).show();
                try {
                    Method method = device.getClass().getMethod("removeBond", (Class[]) null);
                    method.invoke(device, (Object[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(MainActivity.this, "Bond Removed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void uiNewRssiAvailable(BluetoothGatt gatt, BluetoothDevice device, int rssi) {

    }
}
