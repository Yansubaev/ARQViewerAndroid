package su.arq.arqviewer.launcher

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import su.arq.arqviewer.R
import su.arq.arqviewer.walkthrough.WalkthroughActivity

class LaunchingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launching)

        val intent = Intent(applicationContext, WalkthroughActivity::class.java)
        startActivity(intent)
        finish()
    }
}
