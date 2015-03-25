package com.skyworth.sernorvalue.method;

import android.util.Log;

public class PositionTransform{
	
	private String TAG = "PositionTransform";
	
	private volatile int mValue;
		
	private volatile float preValue;
	
	private volatile float accError = 0;
	
	private int coefficient = 1;
	
	public void init(float preValue,int coefficient){
		this.preValue = preValue;
		this.accError = 0;
		this.mValue = 0;
		if(coefficient > 0)
			this.coefficient = coefficient;
		
		Log.d(TAG, "preValue is: " + preValue + "coefficient is: " + coefficient);
	}
	
	public int CalculateValue(float value){
		float a;
		int b;
		float err;

		if(((preValue - value) > 20) || ((preValue - value) < -20)){
				preValue = value;
		}
		
		a = (value - preValue) * coefficient;

		b = (int)((value - preValue) * coefficient);

		err = (float)(a - b);
		accError += err;
		if(accError > 1){
			accError = accError - 1;
			b =  b + 1;
		}
		mValue = b;
		preValue = value;
		return mValue;
	}    	
}