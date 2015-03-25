package com.skyworth.sernorvalue.method;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ValueAssemblage{
	
	private BlockingQueue<Float> X;
	private BlockingQueue<Float> Y;
	private BlockingQueue<Float> Z;
	
	private Filter xfilter;
	private Filter yfilter;
	private Filter zfilter;
	
	private int defaultSize = 100;
	private int defaultfilteSize = 20;
	
	private float x;
	private float y;
	private float z;
	
	
	public ValueAssemblage(int size,int filtersize,boolean linearization){
		if(size > 0)
			defaultSize = size;
		if(filtersize > 0)
			defaultfilteSize = filtersize;
		X = new LinkedBlockingQueue<Float>(defaultSize);
		Y = new LinkedBlockingQueue<Float>(defaultSize);
		Z = new LinkedBlockingQueue<Float>(defaultSize);
		
		xfilter = new Filter("X",defaultfilteSize,false);
		yfilter = new Filter("Y",defaultfilteSize,linearization);
		zfilter = new Filter("Z",defaultfilteSize,false);
	}
	
	public boolean offerx(Float obj){
		return X.offer(obj);
	}
	
	public boolean offery(Float obj){
		return Y.offer(obj);
	}
	
	public boolean offerz(Float obj){
		return Z.offer(obj);
	}
	
	public int takex(){
		Float a = null;
		int value = 0;
		if((a = X.poll()) != null)
			value = (int)a.floatValue();
		else
			value = 0;
		return value;
	}
	
	public int takey(){
		Float a = null;
		int value = 0;
		if((a = Y.poll()) != null)
			value = (int)a.floatValue();
		else
			value = 0;
		return value;
	}
	
	public int takez(){
		Float a = null;
		int value = 0;
		if((a = Z.poll()) != null)
			value = (int)a.floatValue();
		else
			value = 0;
		return value;
	}
	
	public boolean takexTofilter(){
	    Float obj = null;
	    float value = 0;
		try {
			obj = X.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		if(obj != null){
			value = obj.floatValue();
			xfilter.put(value);
			return true;
		}else
			return false;
	}
	
	public boolean takeyTofilter(){
	    Float obj = null;
	    float value = 0;
		try {
			obj = Y.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		if(obj != null){
			value = obj.floatValue();
			yfilter.put(value);
			return true;
		}else
			return false;
	}
	
	public boolean takezTofilter(){
	    Float obj = null;
	    float value = 0;
		try {
			obj = Z.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(obj != null){
			value = obj.floatValue();
			zfilter.put(value);
			return true;
		}else
			return false;
	}
	
	public float getValuex(){
		
		return xfilter.AverageValue();	
	}
	
	public float getValuey(){
		
		return yfilter.AverageValue();	
	}
	
	public float getValuez(){

		return zfilter.AverageValue();	
	}
	
}