/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class android_os_SystemProperties */

#ifndef _Included_android_os_SystemProperties
#define _Included_android_os_SystemProperties
#ifdef __cplusplus
extern "C" {
#endif
#undef android_os_SystemProperties_PROP_NAME_MAX
#define android_os_SystemProperties_PROP_NAME_MAX 31L
#undef android_os_SystemProperties_PROP_VALUE_MAX
#define android_os_SystemProperties_PROP_VALUE_MAX 91L
/*
 * Class:     android_os_SystemProperties
 * Method:    native_get
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_os_SystemProperties_native_1get__Ljava_lang_String_2
  (JNIEnv *, jclass, jstring);

/*
 * Class:     android_os_SystemProperties
 * Method:    native_get
 * Signature: (Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_os_SystemProperties_native_1get__Ljava_lang_String_2Ljava_lang_String_2
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     android_os_SystemProperties
 * Method:    native_get_int
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_android_os_SystemProperties_native_1get_1int
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     android_os_SystemProperties
 * Method:    native_get_long
 * Signature: (Ljava/lang/String;J)J
 */
JNIEXPORT jlong JNICALL Java_android_os_SystemProperties_native_1get_1long
  (JNIEnv *, jclass, jstring, jlong);

/*
 * Class:     android_os_SystemProperties
 * Method:    native_get_boolean
 * Signature: (Ljava/lang/String;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_android_os_SystemProperties_native_1get_1boolean
  (JNIEnv *, jclass, jstring, jboolean);

/*
 * Class:     android_os_SystemProperties
 * Method:    native_set
 * Signature: (Ljava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_android_os_SystemProperties_native_1set
  (JNIEnv *, jclass, jstring, jstring);

/*
 * Class:     android_os_SystemProperties
 * Method:    native_add_change_callback
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_os_SystemProperties_native_1add_1change_1callback
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif
