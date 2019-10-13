package su.arq.arqviewer.splashscreen

import android.accounts.AccountManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import su.arq.arqviewer.R
import su.arq.arqviewer.projects.activity.ProjectsActivity
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.utils.EXTRA_ARQ_ACCOUNT_NAME
import su.arq.arqviewer.utils.EXTRA_TOKEN
import su.arq.arqviewer.walkthrough.WalkthroughActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_splash_screen)

        val intent: Intent

        val am = AccountManager.get(applicationContext)
        val al = am.getAccountsByType(ARQAccount.TYPE)

        intent = if(al.isNotEmpty()){
            Intent(
                applicationContext,
                ProjectsActivity::class.java
            ).putExtra(
                EXTRA_TOKEN,
                am.peekAuthToken(al.last(), al.last().type)
            ).putExtra(EXTRA_ARQ_ACCOUNT_NAME, al.last().name)
        }else{
            Log.d(this.javaClass.simpleName, "No ARQ Accounts")
            Intent(applicationContext, WalkthroughActivity::class.java)
        }

        startActivity(intent)
        finish()

    }
}
