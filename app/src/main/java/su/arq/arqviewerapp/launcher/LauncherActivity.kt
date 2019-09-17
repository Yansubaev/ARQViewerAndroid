package su.arq.arqviewerapp.launcher

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.View
import su.arq.arqviewerapp.R
import su.arq.arqviewerapp.sign.SignActivity
import android.content.Intent as Intent1
import android.content.Intent
import android.net.Uri

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

    fun bottomSignButtonsClick(view: View) {
        if(view.id == R.id.sign_up_btn){
            val intent = Intent1(applicationContext, SignActivity::class.java)
            startActivity(intent)
        }else if (view.id == R.id.goto_website_btn){
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://arq.su/"))
            startActivity(browserIntent)
        }
    }

    private fun setWindowsFlags() {
        this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

    }

}
