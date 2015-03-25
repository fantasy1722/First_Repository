package com.skyworth.sernorvalue.method;


public class BasicTread implements ThreadControlImpl{

	private String mName;
	private Thread mCurrent = null;
	private ThreadControlImpl runmethod = null;
	private boolean mCycle = false;
	
	public BasicTread(String name,ThreadControlImpl run,boolean cycle){
		mCycle = cycle;
		mName = name;
		runmethod = run;
		mCurrent = new ControlThread();
	}
	
	
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
        if(mCurrent != null){
        	runmethod.cancel();
        	Thread temp = mCurrent;
        	mCurrent = null;
        	temp.interrupt();
        }
	}



	@Override
	public void start() {
		// TODO Auto-generated method stub
		mCurrent.start();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public class ControlThread extends Thread {
		 	
		public void run() {
			Thread thisThread = Thread.currentThread();
			Thread.currentThread().setName(mName);
			do{
				BasicTread.this.runmethod.run();
			}while((mCurrent == thisThread) && mCycle);
		
	    }
	}
}