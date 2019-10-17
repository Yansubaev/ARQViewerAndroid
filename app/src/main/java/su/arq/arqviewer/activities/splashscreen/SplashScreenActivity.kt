package su.arq.arqviewer.activities.splashscreen

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import su.arq.arqviewer.R
import su.arq.arqviewer.activities.projects.ProjectsActivity
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.utils.EXTRA_ARQ_ACCOUNT
import su.arq.arqviewer.activities.walkthrough.WalkthroughActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_splash_screen)

        val intent: Intent
        val am = AccountManager.get(applicationContext)
        val al = am.getAccountsByType(ARQAccount.TYPE)

        intent = if(al.isNotEmpty()){
            Intent(applicationContext, ProjectsActivity::class.java)
        }else{
            Log.i(this.javaClass.simpleName, "No ARQ account found")
            Intent(applicationContext, WalkthroughActivity::class.java)
        }
        startActivity(intent)
        finish()

    }
}
