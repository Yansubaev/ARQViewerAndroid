package su.arq.arqviewer.account

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ARQAuthenticatorService : Service(){

    private lateinit var mAuthenticator: ARQAuthenticator

    override fun onCreate() {
        super.onCreate()
        mAuthenticator =
            ARQAuthenticator(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder? = mAuthenticator.iBinder

}