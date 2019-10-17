package su.arq.arqviewer.account

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ARQAuthenticatorService : Service(){

    private lateinit var authenticator: ARQAuthenticator

    override fun onCreate() {
        super.onCreate()
        authenticator =
            ARQAuthenticator(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder? = authenticator.iBinder

}