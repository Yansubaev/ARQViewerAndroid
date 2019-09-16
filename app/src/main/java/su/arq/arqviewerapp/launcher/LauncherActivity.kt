package su.arq.arqviewerapp.launcher

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import su.arq.arqviewerapp.R
import su.arq.arqviewerapp.sign.SignActivity
import android.content.Intent as Intent1

const val CHOSEN_PAGE = "SIGN_PAGE"

class LauncherActivity : AppCompatActivity() {


    private var viewPager: ViewPager? = null
    private var cardModels: MutableList<LauncherCardModel>? = null
    private var cardAdapter: LauncherCardAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        setContentView(R.layout.activity_launcher)
        
        setWindowsFlags()

        cardModels = ArrayList()
        cardModels?.add(LauncherCardModel("ARQ Editor", "ARQ это ваш проводник в мир дополненной реальности." +
                " Просто загрузите 3D модель и дополните вашу реальность новыми объектами созданные вами." +
                " Подробности на нашем сайте."))

        cardAdapter = LauncherCardAdapter(cardModels, this)

        viewPager = findViewById(R.id.viewPager)
        viewPager?.adapter = cardAdapter
    }

    override fun onResume() {
        super.onResume()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)

        setWindowsFlags()
    }

    fun openSignActivity(view: View) {
        val intent = Intent1(applicationContext, SignActivity::class.java)
        startActivity(intent)
    }

    private fun setWindowsFlags() {
        this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

    }

}
