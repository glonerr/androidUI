#include "android_os_SystemProperties.h"
#include <stdio.h>
#include <string.h>
/*
 * Class:     android_os_SystemProperties
 * Method:    native_get
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_os_SystemProperties_native_1get__Ljava_lang_String_2
  (JNIEnv *env, jclass cls, jstring key){
	return key;
}

/*
 * Class:     android_os_SystemProperties
 * Method:    native_get
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_os_SystemProperties_native_1get__Ljava_lang_String_2Ljava_lang_String_2
  (JNIEnv *env, jclass cls, jstring key, jstring def){
	return def;
}

/*
 * Class:     android_os_SystemProperties
 * Method:    native_get_int
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_android_os_SystemProperties_native_1get_1int
  (JNIEnv *env, jclass cls, jstring key, jint def){
	return def;
}

/*
 * Class:     android_os_SystemProperties
 * Method:    native_get_long
 * Signature: (Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_android_os_SystemProperties_native_1get_1long
  (JNIEnv *env, jclass cls, jstring key, jlong def){
	return def;
}

/*
 * Class:     android_os_SystemProperties
 * Method:    native_get_boolean
 * Signature: (Ljava/lang/String;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_android_os_SystemProperties_native_1get_1boolean
  (JNIEnv *env, jclass cls, jstring key, jboolean def){
	return def;
}

/*
 * Class:     android_os_SystemProperties
 * Method:    native_set
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_android_os_SystemProperties_native_1set
  (JNIEnv *env, jclass cls, jstring key, jstring def){

}

/*
 * Class:     android_os_SystemProperties
 * Method:    native_add_change_callback
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_os_SystemProperties_native_1add_1change_1callback
  (JNIEnv *env, jclass cls){

}
