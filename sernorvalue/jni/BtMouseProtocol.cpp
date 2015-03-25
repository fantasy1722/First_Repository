#include <string.h>
#include <jni.h>
#include <android/log.h>
#include "com_skyworth_sernorvalue_method_BtMouseProtocl.h"


struct DataTransmit{
     jint Command;
     jint Xoffset;
     jint Yoffset;
     jint Sum;
};


JNIEXPORT jbyteArray JNICALL Java_com_skyworth_sernorvalue_method_BtMouseProtocl_TraIntToByte
  (JNIEnv *env, jobject obj, jint i, jint j, jint k){
	jbyteArray mbyte;
	DataTransmit mdata;
	jbyte pData[sizeof(DataTransmit)];
	//__android_log_print(ANDROID_LOG_INFO, "BtMouseProtocl", "JNI the array transform.");
	mdata.Command = i;
	mdata.Xoffset = j;
	mdata.Yoffset = k;
	mdata.Sum = i + j + k;
	memcpy(pData,(jbyte*)&mdata,sizeof(mdata));
	mbyte = env->NewByteArray(sizeof(DataTransmit));
	env->SetByteArrayRegion(mbyte,0,sizeof(pData),pData);
	return mbyte;
}


JNIEXPORT jintArray JNICALL Java_com_skyworth_sernorvalue_method_BtMouseProtocl_TraByteToInt
  (JNIEnv *env, jobject obj, jbyteArray byte){
	jintArray mint;
	DataTransmit mdata;
	jbyte *pData;
	jint pInt[4];
	jint Sum;
	jboolean isCopy;
	pData = env->GetByteArrayElements(byte,&isCopy);
	if(pData != NULL){
		memcpy(&mdata,pData,sizeof(DataTransmit));
		env->ReleaseByteArrayElements(byte,pData,JNI_ABORT);
	}else{
		__android_log_print(ANDROID_LOG_ERROR, "BtMouseProtocl", "JNI The array is not copy rigth.");
		return NULL;
	}
	pInt[0] = mdata.Command;
	pInt[1] = mdata.Xoffset;
	pInt[2] = mdata.Yoffset;
	Sum = mdata.Sum;
	if(Sum == (mdata.Command + mdata.Xoffset + mdata.Yoffset))
			pInt[3] = 1;
	else
			pInt[3] = 0;
	mint = env->NewIntArray(sizeof(pInt));
	env->SetIntArrayRegion(mint,0,sizeof(pInt),pInt);
	return mint;
}
