package com.example.sample_flutter_fitpro;



import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;

import com.legend.bluetooth.fitprolib.application.FitProSDK;
import com.legend.bluetooth.fitprolib.bluetooth.BleManager;
import com.legend.bluetooth.fitprolib.bluetooth.BluetoothLeService;
import com.legend.bluetooth.fitprolib.bluetooth.Profile;
import com.legend.bluetooth.fitprolib.bluetooth.SDKCmdMannager;
import com.legend.bluetooth.fitprolib.model.MeasureDetailsModel;
import com.legend.bluetooth.fitprolib.receiver.LeReceiver;
import com.legend.bluetooth.fitprolib.utils.FitProSpUtils;
import com.legend.bluetooth.fitprolib.utils.SDKTools;
import static com.legend.bluetooth.fitprolib.application.FitProSDK.Logdebug;
import static com.legend.bluetooth.fitprolib.bluetooth.OtaManager.mBLE;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.getBrightScreenValue;
import static com.legend.bluetooth.fitprolib.bluetooth.SendData.setSendBeforeValue;
import static com.legend.bluetooth.fitprolib.utils.SDKTools.mHandler;

import com.legend.bluetooth.fitprolib.utils.SaveKeyValues;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.Serializable;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;






public class FitProWrapper extends SDKWrapper implements ISDKWrapper {


    private LeReceiver leReceiver;
    //private final BleManager<BluetoothDevice> mBle;
    public MainActivity mainActivity;
    private static final String TAG = "FitProWrapper";
    private String dataForMethods;
    protected static String mac;
    protected static String name;
    private String date;

    private enum selectedCommandTable {
        Heart,
        Pressure,
        Oxygen,
    }

    private static selectedCommandTable selectedCommand;
    static boolean serviceRegistered = false;
    public FitProWrapper(MainActivity mainActivity) {

        this.mainActivity = mainActivity;

        System.out.println("checking if BLE is enabled and supported");


        System.out.println("getFitProSDK");

        //if(!SDKCmdMannager.isConnected())


        System.out.println("start intent and bind service");

        SaveKeyValues.putStringValues("bluetooth_address", "");
        String savedMAC1 = SaveKeyValues.getStringValues("bluetooth_address", "");
        System.out.println("saved mac after init on create " + savedMAC1);


        if (!serviceRegistered) {
            //SaveKeyValues.putStringValues("bluetooth_address", mac);
            //SaveKeyValues.putStringValues("bluetooth_address", "");
            String SavedMacBeforeInit = SaveKeyValues.getStringValues("bluetooth_address", "");
            System.out.println("saved mac before init is ------" + SavedMacBeforeInit);

            FitProSDK.getFitProSDK().init(mainActivity.getApplication());

            String savedMAC = SaveKeyValues.getStringValues("bluetooth_address", "");
            System.out.println("saved mac after init " + savedMAC);

            Intent intent = new Intent(mainActivity, BluetoothLeService.class);
            boolean res = mainActivity.bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
            System.out.println(res);
            leReceiver = new LeReceiver(mainActivity, handler);

            leReceiver.registerLeReceiver();
            //mainActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
            serviceRegistered = true;
            //bleManager.registerBleStateReceiver(true);
        }

    }


/*
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("find");
        intentFilter.addAction("state");
        intentFilter.addAction("charac_write");
        intentFilter.addAction("charac_read");
        intentFilter.addAction("charac_changed");
        intentFilter.addAction("descriptor");
        intentFilter.addAction("find_phone");

        return intentFilter;
    }



    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {


        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("we are here --------------");
            Message var3 = new Message();
            Map var4 = (Map)intent.getExtras().getSerializable("Datas");
            final String intent1;
            if ((intent1 = intent.getAction()).equals(var4.get("action").toString())) {
                Bundle context1;
                (context1 = new Bundle()).putSerializable("Datas", (Serializable)var4);
                Integer intent2 = (Integer)var4.get("what");
                var3.setData(context1);
                var3.what = intent2;
                handler.sendMessage(var3);
            }
        }

    };

 */


    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBLE = ((BluetoothLeService.LocalBinder) service).getService();
            Logdebug(TAG, "in onServiceConnected!!!--------------");
            if (!mBLE.initialize()) {
                Logdebug(TAG, "Unable to initialize Bluetooth---------------");

            }
            //connect();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBLE = null;
        }
    };

    /*
    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("on finalize called ---------!!!!!!!!");
        leReceiver.unregisterLeReceiver();
        //mBle.getBleService().close();
    }
*/




    private Handler handler = new Handler(msg -> {
        System.out.println("this is the handler message ! ----" + msg.getData().toString());
        final Map<String, Object> map = (Map<String, Object>) msg.getData().getSerializable("Datas");//接受msg传递过来的参数
        System.out.println(map);

        switch (msg.what) {
            case Profile.MsgWhat.what2:
                System.out.println("connection status received ------");

                break;
            case Profile.MsgWhat.what4:
                System.out.println("battery level-------" + map.get("battery"));
                int level = Integer.parseInt(Objects.requireNonNull(map.get("battery")).toString());
                System.out.println(level);

                break;
            case Profile.MsgWhat.what60://心率测量返回
                MeasureDetailsModel data = (MeasureDetailsModel) map.get("measuredata");
                SDKTools.hearting = false;
                setData(data);

                break;
            case Profile.MsgWhat.what61://心率测量停止(手环主动发起)
                SDKTools.hearting = false;

                break;
            case Profile.MsgWhat.what64://心率测量开始/停止(APP发起)
                System.out.println("check here----");
                System.out.println(map.toString());
                if (SDKTools.hearting && map.get("is_ok") != null && map.get("is_ok").equals("0")) {
                    System.out.println("here!!!");
                    SDKTools.hearting = false;//停止测量

                }
                break;
            default:
                break;
        }
        return false;
    });


    private void setData(MeasureDetailsModel data) {
        int hBlood = data.getHblood();
        int lBlood = data.getLblood();
        int heart = data.getHeart();
        int spo = data.getSpo();

        System.out.println("heart rate is ------------------"+heart);
        System.out.println("blood pressure is ------------"+hBlood+"/"+lBlood);
        System.out.println("oxygen is -------------"+spo);


    }

    @Override
    public ISDKWrapper setMacAddress(String macAddress) {
        System.out.println("set mac called --------------------------");
        this.mac = macAddress;
        return this;
    }

    @Override
    public ISDKWrapper setName(String name) {
        System.out.println("the saved name is -----------"+this.name);
        this.name=name;
        System.out.println("name saved after set is -----------"+this.name);
        return this;
    }

    @Override
    public ISDKWrapper setDateForData(String date) {
        this.date = date;
        return null;
    }

    @Override
    public void connectDevice() {

        System.out.println("connectDevice Called ");
        connect();
    }

    @Override
    public void connect() {
        System.out.println("inside the wrapper , connect methode called and the mac is "+mac);
        SDKTools.mService.connect2(mac);
    }

    @Override
    public void startHeart() {
        System.out.println("startHeart started");
        SDKTools.hearting = !SDKTools.hearting;
        SDKCmdMannager.startMeasureHeatRate();
    }

}