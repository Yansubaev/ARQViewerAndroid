package su.arq.arqviewer.webcomunication.response

import android.util.Log

interface AuthenticationData {
    val token: String
    val name: String
    val email: String

    fun printData(){
        Log.d(this.javaClass.simpleName, "name = $name;    email = $email")
    }
}