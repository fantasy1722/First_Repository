package com.skyworth.sernorvalue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.skyworth.sernorvalue.method.BasicTread;
import com.skyworth.sernorvalue.method.Filter;
import com.skyworth.sernorvalue.method.ThreadControlImpl;
import com.skyworth.sernorvalue.method.ValueAssemblage;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

public class MainService extends Service{
		
	private String TAG = "MainService_sensor";
	private SensorManager mSensorManager;	
	private Sensor gSensor;
	private Sensor oSensor;
	private Sensor gySensor;
	
	private SensorEventListenerManage SensorEventListenerManage_G,SensorEventListenerManage_O,SensorEventListenerManage_GY;
	
	public static ValueAssemblage ValueAssemblage_h,ValueAssemblage_v;
	
	private BasicTread mTakeValueThread;
	
	////////////////////////////////////////////////////////
	
	private int aacsensormode = 2;
	int anveragelen = 5;
	float[] grovalu = new float[this.anveragelen];
	float[] grovalu_y = new float[this.anveragelen];
	float[] grovalu_z = new float[this.anveragelen];
	private float mGX = 0.0F;
	private float mGY = 0.0F;
	private float mGZ = 0.0F;
	private float radixT = 1.0F;
	
	
	
	
	
	@Override
    public void onCreate() {
		
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		
		gSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if(gSensor == null){
			Log.d(TAG,"the G-Sersor isn't support!");
		}else{
			Log.d(TAG,"the G-Sersor is support!");
		}

		oSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
		if(oSensor == null){
			Log.d(TAG,"the O-Sersor isn't support!");
		}else{
			Log.d(TAG,"the O-Sersor is support!");
		}
		
		gySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		if(gySensor == null){
			Log.d(TAG,"the gy-Sersor isn't support!");
		}else{
			Log.d(TAG,"the gy-Sersor is support!");
		}
		
		//SensorEventListenerManage_G = new SensorEventListenerManage(gSensor,SensorManager.SENSOR_DELAY_NORMAL);
		SensorEventListenerManage_O = new SensorEventListenerManage(oSensor,SensorManager.SENSOR_DELAY_GAME);
		//SensorEventListenerManage_GY = new SensorEventListenerManage(gySensor,SensorManager.SENSOR_DELAY_GAME);
		
		//SensorEventListenerManage_G.registerListener();
		SensorEventListenerManage_O.registerListener();
		//SensorEventListenerManage_GY.registerListener();
		
		ValueAssemblage_h = new ValueAssemblage(50,8,false);
		//ValueAssemblage_v = new ValueAssemblage(50,1,false);
		
		
		mTakeValueThread = new BasicTread("Value_Take",new ValueTransferThread(ValueAssemblage_h),true);
		mTakeValueThread.start();

	}
	
    @Override  
    public void onDestroy() {  
        // TODO Auto-generated method stub    
        super.onDestroy(); 
        Log.d(TAG, "server onDestroy~~~");  
		SensorEventListenerManage_O.unregisterListener();
		mTakeValueThread.cancel();
		
    }  


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
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
	
	private class SensorEventListenerManage implements SensorEventListener{
		
		private Sensor mSen;
		private int Freq = SensorManager.SENSOR_DELAY_NORMAL;
		private float gravity[]={0,0,0};
		
		public SensorEventListenerManage(Sensor s,int freq){
			mSen = s;
			Freq = freq;
		}
		
		public void registerListener(){
			mSensorManager.registerListener(this, mSen, Freq);
		}
		
		public void unregisterListener(){
			mSensorManager.unregisterListener(this);
			
		}
		
		@Override
	    public void onAccuracyChanged(Sensor sensor, int accuracy) {
			
	    }

		@Override
	    public void onSensorChanged(SensorEvent event) {
			
		    if(event.sensor.getType() == Sensor.TYPE_ORIENTATION){
			    Float axisX = new Float(event.values[0]);    
			    Float axisY = new Float(event.values[1]);    
			    Float axisZ = new Float(event.values[2]);
		    	if(!(ValueAssemblage_h.offerx(axisX)))
		    		Log.e(TAG,"ValueAssemblage_h BlockingQueue is out");
		    	
		    	if(!(ValueAssemblage_h.offery(axisY)))
		    		Log.e(TAG,"ValueAssemblage_h BlockingQueue is out");
		    	
		    	if(!(ValueAssemblage_h.offerz(axisZ)))
		    		Log.e(TAG,"ValueAssemblage_h BlockingQueue is out");
		  	
		    }else if(event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
		    	
		        mGX = event.values[2];
		        mGY = event.values[0];
		        mGZ = event.values[1];
		        float f4 = movevalue(mGX, grovalu);
		        float f5 = movevalue(mGY, grovalu_y);
		        if (Math.abs(f4) < 0.05D)
		            f4 = 0.0F;
		          if (Math.abs(f5) < 0.05D)
		            f5 = 0.0F;
		          if (((Math.abs(f4) > 0.05D) || (Math.abs(f5) > 0.05D))){
		        	  Log.d(TAG,"Sensor.TYPE_GYROSCOPE is run");
		        	  Float X = new Float(28.0F * -f4 * radixT);
		        	  Float Y = new Float(14.0F * -f5 * radixT);
		        	  if(!(ValueAssemblage_v.offerz(X)))
				    		Log.e(TAG,"ValueAssemblage_v BlockingQueue is out");
		        	  if(!(ValueAssemblage_v.offerz(Y)))
				    		Log.e(TAG,"ValueAssemblage_v BlockingQueue is out");
		          }
		   
		    }
	    }	
	}
	
	private class ValueTransferThread implements ThreadControlImpl{
		
		private ValueAssemblage mData;
		
		public ValueTransferThread(ValueAssemblage data){
			
			this.mData =data;
		}
		 
		@Override
		public void run() {
			// TODO Auto-generated method stub
				if(!mData.takexTofilter())
					Log.d(TAG," get x arg value is fail!");
				if(!mData.takeyTofilter())
					Log.d(TAG," get y arg value is fail!");
				if(!mData.takezTofilter())
					Log.d(TAG," get z arg value is fail!");
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
}