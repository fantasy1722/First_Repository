package com.skyworth.sernorvalue.method;


public class SyncfloatQueue{
	
	private int Maxlength = 0;
	private int length = 0;
	
	private float[] mQueue;
	
	public SyncfloatQueue(int length){
		this.Maxlength = length;
		mQueue = new float[length];
	}
	
	public void offer(float value){
		
	}
	
	public void put(float value){
		
	}
	
	public void poll(float value){
		
	}
	
	public void take(float value){
		
	}
	
	public boolean isEmpty(){
		return false;
		
	}
	
	public boolean isFull(){
		return false;
		
	}
	
	public int Length(){
		return length;
	}
	
	
}