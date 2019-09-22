package su.arq.arqviewer.launcher

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import su.arq.arqviewer.R
import su.arq.arqviewer.walkthrough.WalkthroughActivity

class LauncherActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher)

    }

    override fun onResume() {
        super.onResume()

        val intent = Intent(applicationContext, WalkthroughActivity::class.java)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        var t = 0
        for(i in 0 until 1000000){
            t+=i
        }

        startActivity(intent)

        finish()
    }
}
