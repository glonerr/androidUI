#include "android_content_res_AssetManager.h"
/*
 * Class:     android_content_res_AssetManager
 * Method:    list
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_android_content_res_AssetManager_list(
		JNIEnv *env, jobject obj, jstring path) {
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    addAssetPath
 * Signature: (Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_addAssetPath(
		JNIEnv *env, jobject obj, jstring path) {
	return 0;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    isUpToDate
 * Signature: ()Z
 */
JNIEXPORT jboolean JNICALL Java_android_content_res_AssetManager_isUpToDate(
		JNIEnv *env, jobject obj) {
	return false;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    setLocale
 * Signature: (Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_setLocale
(JNIEnv *env, jobject obj, jstring locale) {

}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getLocales
 * Signature: ()[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_android_content_res_AssetManager_getLocales(
		JNIEnv *env, jobject obj) {
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    setConfiguration
 * Signature: (IILjava/lang/String;IIIIIIIIIIIIII)V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_setConfiguration
(JNIEnv *env, jobject obj, jint mcc, jint mnc, jstring locale, jint orientation, jint touchscreen, jint density, jint keyboard, jint keyboardHidden, jint navigation, jint screenWidth, jint screenHeight, jint smallestScreenWidthDp, jint screenWidthDp, jint screenHeightDp, jint screenLayout, jint uiMode, jint majorVersion) {

}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getResourceIdentifier
 * Signature: (Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_getResourceIdentifier(
		JNIEnv *env, jobject obj, jstring type, jstring name, jstring defPackage){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getResourceName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_content_res_AssetManager_getResourceName(
		JNIEnv *env, jobject obj, jint resid){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getResourcePackageName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_content_res_AssetManager_getResourcePackageName(
		JNIEnv *env, jobject obj, jint resid){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getResourceTypeName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_content_res_AssetManager_getResourceTypeName(
		JNIEnv *env, jobject obj, jint resid){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getResourceEntryName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_content_res_AssetManager_getResourceEntryName(
		JNIEnv *env, jobject obj, jint resid){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    openAsset
 * Signature: (Ljava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_openAsset(JNIEnv *env,
		jobject obj, jstring fileName, jint accessMode){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    openAssetFd
 * Signature: (Ljava/lang/String;[J)Landroid/os/ParcelFileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_content_res_AssetManager_openAssetFd(
		JNIEnv *env, jobject obj, jstring fileName, jlongArray outOffsets){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    openNonAssetNative
 * Signature: (ILjava/lang/String;I)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_openNonAssetNative(
		JNIEnv *env, jobject obj, jint cookie, jstring fileName, jint accessMode){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    openNonAssetFdNative
 * Signature: (ILjava/lang/String;[J)Landroid/os/ParcelFileDescriptor;
 */
JNIEXPORT jobject JNICALL Java_android_content_res_AssetManager_openNonAssetFdNative(
		JNIEnv *env, jobject obj, jint cookie, jstring fileName, jlongArray outOffsets){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    destroyAsset
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_destroyAsset
(JNIEnv *env, jobject obj, jint asset){

}

/*
 * Class:     android_content_res_AssetManager
 * Method:    readAssetChar
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_readAssetChar(
		JNIEnv *env, jobject obj, jint asset){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    readAsset
 * Signature: (I[BII)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_readAsset(JNIEnv *env,
		jobject obj, jint asset, jbyteArray b, jint off, jint len){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    seekAsset
 * Signature: (IJI)J
 */
JNIEXPORT jlong JNICALL Java_android_content_res_AssetManager_seekAsset(
		JNIEnv *env, jobject obj, jint asset, jlong offset, jint whence){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getAssetLength
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_android_content_res_AssetManager_getAssetLength(
		JNIEnv *env, jobject obj, jint asset){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getAssetRemainingLength
 * Signature: (I)J
 */
JNIEXPORT jlong JNICALL Java_android_content_res_AssetManager_getAssetRemainingLength(
		JNIEnv *env, jobject obj, jint asset){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    loadResourceValue
 * Signature: (ISLandroid/util/TypedValue;Z)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_loadResourceValue(
		JNIEnv *env, jobject obj, jint ident, jshort density, jobject outValue, jboolean resolve){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    loadResourceBagValue
 * Signature: (IILandroid/util/TypedValue;Z)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_loadResourceBagValue(
		JNIEnv *env, jobject obj, jint ident, jint bagEntryId, jobject outValue, jboolean resolve){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    applyStyle
 * Signature: (IIII[I[I[I)Z
 */
JNIEXPORT jboolean JNICALL Java_android_content_res_AssetManager_applyStyle(
		JNIEnv *env, jclass cls, jint theme, jint defStyleAttr, jint defStyleRes, jint xmlParser, jintArray inAttrs, jintArray outValues,
		jintArray outIndices){
	return false;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    retrieveAttributes
 * Signature: (I[I[I[I)Z
 */
JNIEXPORT jboolean JNICALL Java_android_content_res_AssetManager_retrieveAttributes(
		JNIEnv *env, jobject obj, jintArray inAttrs, jintArray outValues, jintArray outIndices){
	return false;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getArraySize
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_getArraySize(
		JNIEnv *env, jobject obj, jint resource){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    retrieveArray
 * Signature: (I[I)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_retrieveArray(
		JNIEnv *env, jobject obj, jint resource, jintArray outValues){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getStringBlockCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_getStringBlockCount(
		JNIEnv *env, jobject obj){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getNativeStringBlock
 * Signature: (I)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_getNativeStringBlock(
		JNIEnv *env, jobject obj, jint block){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getCookieName
 * Signature: (I)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_content_res_AssetManager_getCookieName(
		JNIEnv *env, jobject obj, jint cookie){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getGlobalAssetCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_getGlobalAssetCount(
		JNIEnv *env, jclass cls){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getAssetAllocations
 * Signature: ()Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_android_content_res_AssetManager_getAssetAllocations(
		JNIEnv *env, jclass cls){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getGlobalAssetManagerCount
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_getGlobalAssetManagerCount(
		JNIEnv *env, jclass cls){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    newTheme
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_newTheme(JNIEnv *env,
		jobject obj){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    deleteTheme
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_deleteTheme
(JNIEnv *env, jobject obj, jint theme){
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    applyThemeStyle
 * Signature: (IIZ)V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_applyThemeStyle
(JNIEnv *env, jclass cls, jint theme, jint styleRes, jboolean force){

}

/*
 * Class:     android_content_res_AssetManager
 * Method:    copyTheme
 * Signature: (II)V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_copyTheme
(JNIEnv *env, jclass cls, jint dest, jint source){

}

/*
 * Class:     android_content_res_AssetManager
 * Method:    loadThemeAttributeValue
 * Signature: (IILandroid/util/TypedValue;Z)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_loadThemeAttributeValue(
		JNIEnv *env, jclass cls, jint theme, jint ident, jobject outValue, jboolean resolve){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    dumpTheme
 * Signature: (IILjava/lang/String;Ljava/lang/String;)V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_dumpTheme
(JNIEnv *env, jclass cls, jint theme, jint priority, jstring tag, jstring prefix){

}

/*
 * Class:     android_content_res_AssetManager
 * Method:    openXmlAssetNative
 * Signature: (ILjava/lang/String;)I
 */
JNIEXPORT jint JNICALL Java_android_content_res_AssetManager_openXmlAssetNative(
		JNIEnv *env, jobject obj, jint cookie, jstring fileName){
	return -1;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getArrayStringResource
 * Signature: (I)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_android_content_res_AssetManager_getArrayStringResource(
		JNIEnv *env, jobject obj, jint arrayRes){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getArrayStringInfo
 * Signature: (I)[I
 */
JNIEXPORT jintArray JNICALL Java_android_content_res_AssetManager_getArrayStringInfo(
		JNIEnv *env, jobject obj, jint arrayRes){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    getArrayIntResource
 * Signature: (I)[I
 */
JNIEXPORT jintArray JNICALL Java_android_content_res_AssetManager_getArrayIntResource(
		JNIEnv *env, jobject obj, jint arrayRes){
	return null;
}

/*
 * Class:     android_content_res_AssetManager
 * Method:    init
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_init
(JNIEnv *env, jobject obj){

}

/*
 * Class:     android_content_res_AssetManager
 * Method:    destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_android_content_res_AssetManager_destroy
(JNIEnv *env, jobject obj){

}
