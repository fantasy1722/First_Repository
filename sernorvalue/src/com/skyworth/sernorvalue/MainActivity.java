package com.skyworth.sernorvalue;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends Activity{
	private String TAG="MainActivity_sensor";
	private boolean Debug = true;
	
	private ListView list;
	private ArrayList<String> Viewlist = new ArrayList<String>();
	
	private BluetoothAdapter mBluetoothAdapter = null;
	private ArrayAdapter<String> mPairedDevicesArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_main);
			
			//final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
			//mBluetoothAdapter = bluetoothManager.getAdapter();
			
			mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			if(mBluetoothAdapter == null){
				Dialog builder = new AlertDialog.Builder(MainActivity.this).setTitle("温馨提示")
						.setMessage("检测到设备没有蓝牙，应用运行需要蓝牙支持！").setPositiveButton("确定", new OnClickListener(){
				
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								finish();
							}
						}).create();
						builder.show();
			}
	}

	@Override
	public void onStart() {
	     super.onStart();
	     if(Debug) Log.d(TAG, "++ ON START ++");
	     if(mBluetoothAdapter != null){
	    	if(!mBluetoothAdapter.isEnabled()){
				Dialog builder = new AlertDialog.Builder(MainActivity.this).setTitle("温馨提示")
						.setMessage("设备蓝牙未打开！").setPositiveButton("前往蓝牙设置", new OnClickListener(){
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							 Intent mIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
							 startActivity(mIntent);
							}
				}).create();
				builder.show();
	    	 }else{
				mPairedDevicesArrayAdapter = new ArrayAdapter<String>(this, R.layout.device_name);
				ListView pairedListView = (ListView) findViewById(R.id.pairdev);
				pairedListView.setAdapter(mPairedDevicesArrayAdapter);
				pairedListView.setOnItemClickListener(pairedmDeviceClickListener);
	        
				Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
	        
				if (pairedDevices.size() > 0) {
	            
					for (BluetoothDevice device : pairedDevices) {
	            		mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
	            	}
				} else {
					String noDevices = getResources().getText(R.string.none_paired).toString();
					mPairedDevicesArrayAdapter.add(noDevices);
				}
				Button goto_Bluetooth = (Button) findViewById(R.id.goto_bluetooth); 
				goto_Bluetooth.setOnClickListener(new android.view.View.OnClickListener(){
		            @Override
		            public void onClick(View v)
		            {
		                // TODO Auto-generated method stub
						 Intent mIntent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
						 startActivity(mIntent);
		            }
		        });
	    	 }
	     }
	}

	@Override
	public synchronized void onResume() {
	     super.onResume();
	     if(Debug) Log.d(TAG, "+ ON RESUME +");
	}

	@Override
	public synchronized void onPause() {
	     super.onPause();
	     if(Debug) Log.d(TAG, "- ON PAUSE -");    
	}

	@Override
	public void onStop() {
	     super.onStop();
	     if(Debug) Log.d(TAG, "-- ON STOP --");
	}

	@Override
	public void onDestroy() {
	     super.onDestroy();
	     if(Debug) Log.d(TAG, "--- ON DESTROY ---");
	     finish();
	     System.exit(0);
	}	
	
    private OnItemClickListener pairedmDeviceClickListener = new OnItemClickListener() {
   	 public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
	            if (mBluetoothAdapter.isDiscovering())
	            {
	            	mBluetoothAdapter.cancelDiscovery();
	            }
	            String info = ((TextView) v).getText().toString();
	            String address = info.substring(info.length() - 17);
	            BluetoothDevice mDevice = mBluetoothAdapter.getRemoteDevice(address);
	            if(mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
	            	Bundle bundle = new Bundle();
	            	bundle.putString("BluetoothDevice", address);
	            	Intent mIntent = new Intent(MainActivity.this, OperateTVActivity.class);
	            	mIntent.putExtras(bundle);
	            	startActivity(mIntent);
           	}
   	 }
   };
}
