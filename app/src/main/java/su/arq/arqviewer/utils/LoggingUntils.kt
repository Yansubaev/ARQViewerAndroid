package su.arq.arqviewer.utils

import android.util.Log

fun dlog(`object`: Any, message: String){
    Log.d(`object`.javaClass.simpleName, message)
}