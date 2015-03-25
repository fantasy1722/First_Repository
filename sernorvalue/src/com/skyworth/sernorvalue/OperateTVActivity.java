package com.skyworth.sernorvalue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.skyworth.sernorvalue.method.BasicTread;
import com.skyworth.sernorvalue.method.BtMouseProtocl;
import com.skyworth.sernorvalue.method.PositionTransform;
import com.skyworth.sernorvalue.method.ThreadControlImpl;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;



public class OperateTVActivity extends Activity implements OnGestureListener {
	
	private String TAG="OperateTVActivity";
	private boolean Debug = true;
	private Context mContext;
	
	private BluetoothAdapter mAdapter;
	private BluetoothDevice mDevice;
	
	private ConnectThread mSecureConnectThread;
	private ConnectedThread mConnectedThread;
	
	
	private BasicTread mContectThread;
	private BasicTread mSendThread;
	private BasicTread mProcessThread;
	
    private static final UUID MY_UUID_SECURE =
            UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
	
	private static PositionTransform positionTransformv = new PositionTransform();
	private static PositionTransform positionTransformh = new PositionTransform();
	
	private ProgressDialog mDialog;
	
	//status
	private static final int CONNECTSUCCES = 0;
	private static final int CONNECTFAIL = 1;
	private static final int SERVICEFAIL = 2;
	
	
	//button value
	private static final int NOTHING = 0;
	private static final int HOME = 1;
	private static final int MENU = 2;
	private static final int UP = 3;
	private static final int DOWN = 4;
	private static final int LEFT = 5;
	private static final int RIGHT = 6;
	private static final int ENTER = 7;
	private static final int RETURN = 8;
	private static final int UPTODOWN = 9;
	private static final int DOWNTOUP = 10;
	private static final int POWER = 11;
	private static final int SOURCE = 12;
	private static final int VOLUME_UP = 13;
	private static final int VOLUME_DOWN = 14;
	private static final int VOLUME_MUTE = 15;
	private static final int MOUSE_OK = 16;
	
	private volatile int mCommand = 0;
	
	private boolean MouseEnable = true;
	
	private int width;		// 屏幕宽度（像素）
	private int height;     // 屏幕高度（像素）
	private float density;  // 屏幕密度（0.75 / 1.0 / 1.5）
	private int densityDpi; // 屏幕密度DPI（120 / 160 / 240）
	
	private Vibrator mVibrator; 
	
	private BtMouseProtocl mTran = new BtMouseProtocl();
	private GestureDetector gestureDetector = null; 
	
	
	///////////////////////////////////////////////
	
	private Sensor gySensor;
	private SensorManager mSensorManager;
	private int aacsensormode = 2;
	int anveragelen = 8;
	float[] grovalu = new float[this.anveragelen];
	float[] grovalu_y = new float[this.anveragelen];
	float[] grovalu_z = new float[this.anveragelen];
	private float mGX = 0.0F;
	private float mGY = 0.0F;
	private float mGZ = 0.0F;
	private float radixT = 1.0F;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mContext = this;
		Bundle bundle = getIntent().getExtras();
		String address = bundle.getString("BluetoothDevice");
		Log.d(TAG,"BluetoothDevice address is:  " + address);
		//final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		//mAdapter = bluetoothManager.getAdapter();
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		if(mAdapter != null)
			mDevice = mAdapter.getRemoteDevice(address);
		else{
			Log.e(TAG,"something error occur!");
			this.finish();
		}
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("正在连接...");
		mDialog.show();
		gestureDetector = new GestureDetector(this);    // 声明检测手势事件
    	mSecureConnectThread = new ConnectThread(mDevice,true);	
    	mContectThread = new BasicTread("Contect_Thread",mSecureConnectThread,false);
    	mContectThread.start();
    	  
    	DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        width  = metric.widthPixels;     // 屏幕宽度（像素）
        height = metric.heightPixels;   // 屏幕高度（像素）
        density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        
        mVibrator = (Vibrator)getApplication().getSystemService(Service.VIBRATOR_SERVICE);
        
        
        
        ////////////////////////////////////////////////////////////////////
        /*mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		gySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		mSensorManager.registerListener(new SensorEventListener(){

			@Override
			public void onAccuracyChanged(Sensor sensor, int accuracy) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSensorChanged(SensorEvent event) {
				// TODO Auto-generated method stub
				    mGX = event.values[2];
			        mGY = event.values[0];
			        mGZ = event.values[1];
			        float f4 = movevalue(mGX, grovalu);
			        float f5 = movevalue(mGY, grovalu_y);
			        if (Math.abs(f4) < 0.05D)
			            f4 = 0.0F;
			          if (Math.abs(f5) < 0.05D)
			            f5 = 0.0F;
			          if (((Math.abs(f4) > 0.05D) || (Math.abs(f5) > 0.05D)) && (mConnectedThread != null) && MouseEnable){
			        	  Log.d(TAG,"Sensor.TYPE_GYROSCOPE is run");
			        	  byte[] send;
			        	  send = mTran.TraIntToByte(0,(int)(32.0F * -f4 * radixT),(int)(25.0F * -f5 * radixT));
			        	  mConnectedThread.setSendData(send,false);
			          }
			}	
		},gySensor,SensorManager.SENSOR_DELAY_GAME);*/
	}

	   @Override
	    public void onStart() {
	        super.onStart();
	        if(Debug) Log.d(TAG, "++ ON START ++");
			Intent serviceIntent = new Intent();
	    	serviceIntent.setClass(this, MainService.class);
	    	this.startService(serviceIntent);
	    }

	    @Override
	    public synchronized void onResume() {
	        super.onResume();
	        if(Debug) Log.d(TAG, "+ ON RESUME +");
	        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); 
	    }

	    @Override
	    public synchronized void onPause() {
	        super.onPause();
	        if(Debug) Log.d(TAG, "- ON PAUSE -");
	        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    }

	    @Override
	    public void onStop() {
	        super.onStop();
	        if(Debug) Log.d(TAG, "-- ON STOP --");
			Intent serviceIntent = new Intent();
	    	serviceIntent.setClass(this, MainService.class);
	    	this.stopService(serviceIntent);
	    }

	    @Override
	    public void onDestroy() {
	        super.onDestroy();
	        if(Debug) Log.d(TAG, "--- ON DESTROY ---");
	        mSecureConnectThread = null;
	        mConnectedThread = null;
	        
	    	if(mContectThread != null){
	    		mContectThread.cancel();
	    		mContectThread = null;
	    	}
    		if(mSendThread != null){
    			
    			mSendThread.cancel();
    			mSendThread = null;
    		}
    		if(mProcessThread != null){    			
    			mProcessThread.cancel();
    			mProcessThread = null;
    		}
    		mVibrator.cancel();
			Intent serviceIntent = new Intent();
	    	serviceIntent.setClass(this, MainService.class);
	    	this.stopService(serviceIntent);
	    }	
	    
		public float movevalue(float paramFloat, float[] paramArrayOfFloat)
		{
		    float f = 0.0F;
		    for (int i = -1 + anveragelen; ; i--)
		    {
		      if (i <= 0)
		      {
		        paramArrayOfFloat[0] = paramFloat;
		        return (f + paramArrayOfFloat[0]) / anveragelen;
		      }
		      paramArrayOfFloat[i] = paramArrayOfFloat[(i - 1)];
		      f += paramArrayOfFloat[i];
		    }
		}
	    
	    private void initView(){
	    	if(mDialog.isShowing()) mDialog.dismiss();
	    	setContentView(R.layout.main);
	    	Switch switchbutton = (Switch)this.findViewById(R.id.switchbutton);
	    	switchbutton.setChecked(true);
	    	switchbutton.setOnCheckedChangeListener(new OnCheckedChangeListener(){
				@Override	
				public void onCheckedChanged(CompoundButton buttonView,  
					                       	boolean isChecked) { 
					 if (isChecked) {
						 MouseEnable = true;
				      } else {
				    	 MouseEnable = false;
				      }  
				}
			});	
	    	setButtonClickListener(findViewById(R.id.home),HOME);
	    	setButtonClickListener(findViewById(R.id.menu),MENU);
	    	setButtonClickListener(findViewById(R.id.back),RETURN);
	    	setButtonClickListener(findViewById(R.id.up),UP);
	    	setButtonClickListener(findViewById(R.id.down),DOWN);
	    	setButtonClickListener(findViewById(R.id.left),LEFT);
	    	setButtonClickListener(findViewById(R.id.right),RIGHT);
	    	setButtonClickListener(findViewById(R.id.source),SOURCE);
	    	setButtonClickListener(findViewById(R.id.volume_up),VOLUME_UP);
	    	setButtonClickListener(findViewById(R.id.volume_down),VOLUME_DOWN);
	    }
		
	    private class ConnectThread implements ThreadControlImpl {
	        private final BluetoothSocket mmSocket;
	        private final BluetoothDevice mmDevice;
	        private String mSocketType;
	        
	        public ConnectThread(BluetoothDevice device, boolean secure) {
	            mmDevice = device;
	            BluetoothSocket tmp = null;
	            mSocketType = secure ? "Secure" : "Insecure";

	            // Get a BluetoothSocket for a connection with the
	            // given BluetoothDevice
	            try {
	                    tmp = device.createRfcommSocketToServiceRecord(
	                            MY_UUID_SECURE);
	            } catch (IOException e) {
	                	Log.e(TAG, "Socket Type: " + mSocketType + " create() failed", e);
	                	connectionFailed();
	            }
	            mmSocket = tmp;
	        }

	        @Override
	        public void run() {
	            Log.i(TAG, "BEGIN mConnectThread SocketType:" + mSocketType);
	            // Always cancel discovery because it will slow down a connection
	            mAdapter.cancelDiscovery();
	            // Make a connection to the BluetoothSocket
	            try {
	                // This is a blocking call and will only return on a
	                // successful connection or an exception
	                mmSocket.connect();
	            } catch (IOException e) {
	                // Close the socket
	                try {
	                    mmSocket.close();
	                } catch (IOException e2) {
	                    Log.e(TAG, "unable to close() " + mSocketType +
	                            " socket during connection failure", e2);
	                }
	                connectionFailed();
	                return;
	            }
	            // Start the connected thread
	            connected(mmSocket, mmDevice, mSocketType);
	        }
	        
	        public void cancel() {
	            try {
	                mmSocket.close();
	            } catch (IOException e) {
	                Log.e(TAG, "close() of connect " + mSocketType + " socket failed", e);
	            }
	        }

			@Override
			public void start() {
				// TODO Auto-generated method stub
				
			}
	    }
	
	    private class ConnectedThread implements ThreadControlImpl {
	        private final BluetoothSocket mmSocket;
	        private final OutputStream mmOutStream;
	        private byte[] sendData;
	        private String control = "Thread_Control"; 
	        private Boolean suspend = true;

	        public ConnectedThread(BluetoothSocket socket, String socketType) {
	            Log.d(TAG, "create ConnectedThread: " + socketType);
	            mmSocket = socket;
	            OutputStream tmpOut = null;
	            // Get the BluetoothSocket input and output streams
	            try {
	                tmpOut = socket.getOutputStream();
	            } catch (IOException e) {
	                Log.e(TAG, "temp sockets not created", e);
	            }
	            mmOutStream = tmpOut;
	        }

	        
	        public void setSendData(byte[] data,boolean suspend){
	            if (!suspend) {  
	            	synchronized (control) {
	            		 this.suspend = suspend;	
	            		 sendData = data;
	            		 control.notifyAll();  
	                }  
	            }  
	        }
	        
	        @Override
	        public void run() {
	            	synchronized(control){
	            		if (suspend) { 
	            			try {
	            				control.wait();
	            			} catch (InterruptedException e) {
	            				// TODO Auto-generated catch block
	            				e.printStackTrace();
	            			}
	            		}
            			try {
            					mmOutStream.write(sendData);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							connectionLost();
							e.printStackTrace();
						}
            			suspend = true;
	            	}
	        }


			@Override
			public void start() {
				// TODO Auto-generated method stub
				
			}


			@Override
			public void cancel() {
				// TODO Auto-generated method stub
	            try {
	                mmSocket.close();
	            } catch (IOException e) {
	                Log.e(TAG, "close() of connect socket failed", e);
	            }
				
			}
	    }
	
	    private class ProcessThread implements ThreadControlImpl {
	    	int positionx = 0;
	    	int positiony = 0;
	    	
	    	public ProcessThread(int h,int v) {
	    		positionTransformh.init(MainService.ValueAssemblage_h.getValuex(), h);
	    		positionTransformv.init(MainService.ValueAssemblage_h.getValuey(), v);
	    	}
	    	  
	        @Override  
	        public void run() {
	            	if((MainService.ValueAssemblage_h != null) || (MainService.ValueAssemblage_v != null)){
            			positionx = positionTransformh.CalculateValue(MainService.ValueAssemblage_h.getValuex());
            			positiony = positionTransformv.CalculateValue(MainService.ValueAssemblage_h.getValuey());
	            		Log.d(TAG,"is run here!");
	            		//positionx = MainService.ValueAssemblage_v.takex();
	            		//positiony = MainService.ValueAssemblage_v.takey();
	            		Log.d(TAG,"2is run here!");
	            		byte[] send;
	                	int command = 0;
	                	if(mCommand != NOTHING){
	                		command = mCommand;
	                		positionx = 0;
	                		positiony = 0;
	                		mCommand = NOTHING;
	                	}else{
	                		command = 0;
	                		if(!MouseEnable){
		                		positionx = 0;
		                		positiony = 0;
	                		}
	                	}
	               
	            		send = mTran.TraIntToByte(command, positionx, positiony);
	            		Log.d(TAG, "positionx : " + positionx + " positiony : " + positiony + " command : " + command);
	        	        if(mConnectedThread != null && send != null){
	        	        	if(!((positionx == 0) && (positiony == 0) && (command == 0)))
	        	        		mConnectedThread.setSendData(send,false);
	        	        	//else
	        	        	//	Log.d(TAG, "mConnectedThread.setSendData == null");
	        	        }else{
	        	        	Log.e(TAG,"mConnectedThread is null!!");
	        	        }
	            	}else{
	            		Log.d(TAG, "MainService.ValueAssemblage_h == null");
	            	}
	        }

			@Override
			public void start() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void cancel() {
				// TODO Auto-generated method stub
				
			}
		 }
	    
	    
	    public synchronized void connected(BluetoothSocket socket, BluetoothDevice
	            device, final String socketType) {
	    	
            Message message = null;
            message = mUIHandler.obtainMessage(CONNECTSUCCES);
            mUIHandler.sendMessage(message);
	        mConnectedThread = new ConnectedThread(socket, socketType);
	        mSendThread = new BasicTread("Translate_Thread",mConnectedThread,true);
	        mSendThread.start();
	        mProcessThread = new BasicTread("Process_Thread",new ProcessThread(40,30),true);
	        mProcessThread.start();  
	    }

	    private void connectionFailed() {
	    	Log.e(TAG, "close() of connect socket failed");
	    	if(mSecureConnectThread != null){
	    		if(mContectThread != null)
	    			mContectThread.cancel();
	    		Message message = null;
	    		message = mUIHandler.obtainMessage(CONNECTFAIL);
	    		mUIHandler.sendMessage(message);
	    	}
	    }
	    
	    private void connectionLost() {
	    	Log.e(TAG, "close() of connect socket Lost");
	    	if(mConnectedThread != null){
	    		if(mSendThread != null)
	    			mSendThread.cancel();
	    		Message message = null;
	    		message = mUIHandler.obtainMessage(CONNECTFAIL);
	    		mUIHandler.sendMessage(message);
	        }
	    }
	    
	    public void showInfo(int id){
	    	if(mDialog.isShowing()) mDialog.dismiss();
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle("温馨提示");
	    	switch(id){
	    	case CONNECTFAIL:
	    		builder.setMessage("未能连接电视，请确保电视蓝牙打开").setPositiveButton("返回", new OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								OperateTVActivity.this.finish();
							}
						});
	    		builder.show();
	    	    break;
	    	case SERVICEFAIL:
	    		builder.setMessage("手机没有指定传感器，空鼠标功能将无效").setPositiveButton("确定", new OnClickListener(){
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub

							}
						});
	    		builder.show();
	    	    break;
	    	default:
	    		break;
	    	}
	    }
	       
		private Handler mHandler = new Handler() {
			 
			 public void handleMessage(Message msg) {
				 int command;
				 byte[] send;
				 command = msg.what;
				 Log.d(TAG,"command is " + command);
				 mCommand = command;
	        	// send = mTran.TraIntToByte(command,0,0);
	        	 //mConnectedThread.setSendData(send,false);
			 }			 
		};
		 
		private Handler mUIHandler = new Handler() {
			 
			 public void handleMessage(Message msg) {
				 Log.d(TAG, "mUIHandler handleMessage : " + msg.what);
				 switch(msg.what){
				 case CONNECTSUCCES:
					 initView();
					 break;
				 case CONNECTFAIL:
					 showInfo(CONNECTFAIL);
					 break;
				 case SERVICEFAIL:
					 break;
				 default:
					 break;
				 }
			 }			 
		};
		 
		public void setButtonClickListener(View view,final int type){
			
			view.setOnClickListener(new android.view.View.OnClickListener()
	        {
	            @Override
	            public void onClick(View v)
	            {
	                // TODO Auto-generated method stub
	            	Message message = null;
	            	message = mHandler.obtainMessage(type);
	            	mHandler.sendMessage(message);
	            	mVibrator.vibrate( new long[]{0,60,10,10},-1);
	            }
	        });
		} 
		 
	    @Override  
	    public boolean onTouchEvent(MotionEvent event) {
	    	   Log.d(TAG,"onTouchEvent ?????"); 
	           return gestureDetector.onTouchEvent(event);         // 注册手势事件  
	    }
	       
	    @Override 
	    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
	    	Log.d(TAG,"startx : " + e1.getX() + " endx : " + e2.getX() + " starty : " + e1.getY() + " endy : " + e1.getY());
	        if (e2.getY() - e1.getY() > 80) {            // 从左向右滑动（左进右出）  
	        	 Log.d(TAG,"touch from up to down!!!!");
	             Message message = null;
	             message = mHandler.obtainMessage(UPTODOWN);
	             mHandler.sendMessage(message);
	    	     return true;  
	    	} else if (e2.getY() - e1.getY() < -80) {        // 从右向左滑动（右进左出）  
	    		 Log.d(TAG,"touch from down to up!!!!"); 
	             Message message = null;
	             message = mHandler.obtainMessage(DOWNTOUP);
	             mHandler.sendMessage(message);
	    	     return true;  
	    	}  
	    	 return true;  
	    }  
		 
		@Override
		public boolean onDown(MotionEvent arg0) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
				float arg3) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent arg0) {
			// TODO Auto-generated method stub
			Log.d(TAG,"width : " + width + " height : " + height + " density : " + density + " densityDpi : " + densityDpi);
			Log.d(TAG,"position_x : " + arg0.getX() + " position_y : " + arg0.getY());
            if((height/arg0.getY() > 1.4) && (height/arg0.getY() < 4) && (width/arg0.getX() > 1.3)){
            	Log.d(TAG,"singlepress!"); 
            	Message message = null;
            	if(!MouseEnable)
            		message = mHandler.obtainMessage(ENTER);
            	else
            		message = mHandler.obtainMessage(MOUSE_OK);
            	mHandler.sendMessage(message);
            	mVibrator.vibrate( new long[]{0,60,10,10},-1);
            }
			return true;
		}		
}
