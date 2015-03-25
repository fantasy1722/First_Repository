package com.skyworth.sernorvalue.method;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BtMouseProtocl{
		
    static {
        try
        {
        	System.loadLibrary("BtMouseProtocol");
        }catch(UnsatisfiedLinkError e){
        	e.printStackTrace();
        }
    }
	
    public BtMouseProtocl(){
    	
    }
    
    public native byte[] TraIntToByte(int i,int j,int k);
    
    public native int[] TraByteToInt(byte[] l);
    
}