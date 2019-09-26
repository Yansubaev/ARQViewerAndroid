package su.arq.arqviewer.account

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ARQVAuthenticatorService : Service(){

    private lateinit var mAuthenticator: ARQVAuthenticator

    override fun onCreate() {
        super.onCreate()
        mAuthenticator =
            ARQVAuthenticator(applicationContext)
    }

    override fun onBind(intent: Intent?): IBinder? = mAuthenticator.iBinder

}