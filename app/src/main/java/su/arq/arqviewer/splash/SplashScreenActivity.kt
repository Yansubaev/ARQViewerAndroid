package su.arq.arqviewer.splash

import android.accounts.AccountManager
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import su.arq.arqviewer.R
import su.arq.arqviewer.projects.ProjectsActivity
import su.arq.arqviewer.account.ARQVAccount
import su.arq.arqviewer.walkthrough.WalkthroughActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_splash_screen)

        val intent: Intent

        val am = AccountManager.get(applicationContext)
        val al = am.getAccountsByType(ARQVAccount.TYPE)

        intent = if(al.isNotEmpty()){
            for(ac in al)
                Log.d(this.javaClass.simpleName, ac.name)
            Intent(
                applicationContext,
                ProjectsActivity::class.java
            ).putExtra("EXTRA_TOKEN", am.peekAuthToken(al.last(), al.last().type))
        }else{
            Log.d(this.javaClass.simpleName, "No ARQV Accounts")
            Intent(applicationContext, WalkthroughActivity::class.java)
        }



        startActivity(intent)
        finish()

    }
}
