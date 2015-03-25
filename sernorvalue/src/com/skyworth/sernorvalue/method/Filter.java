package com.skyworth.sernorvalue.method;

import java.lang.Math;

public class Filter{
	
	private String name = null;
	private int maxLimit = 10;
	private float[] mFloat;
	
	private float linearvalue = 0;
	private boolean linearization = false;
	
	private int cursize = 0;//当前大小
	private int cursor = 0;//当前游标
	
	private String mControl = "Control";
	
	
	public Filter(String name,int maxLimit,boolean linearization){
		this.name = name;
		this.linearization = linearization;
		if(maxLimit > 0)
			this.maxLimit = maxLimit;
		mFloat = new float[this.maxLimit];
	}
	
	public void put(float value){
		synchronized (mControl) {
			if(linearization == true){
				value = (float) Math.acos(value/9.81);
				linearvalue = (float) Math.toDegrees(value);
			}else{
			if(cursor >= maxLimit){
				cursor = 0;
			}
			mFloat[cursor] = value;
			cursor++;
			if(cursize < maxLimit)
				cursize++;
			}
			mControl.notifyAll();
		}
	}
	
	public float AverageValue(){
		int i;
		float sum = 0;
		synchronized (mControl) {
		try {
			mControl.wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(linearization == true){
			return linearvalue;
		}else{
		for(i=0;i<cursize;i++){
			sum += mFloat[i];
		}
		if(cursize != 0)
			return sum/cursize;
		else
			return 0;
		}
		}
	}
}