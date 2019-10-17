package su.arq.arqviewer.activities.walkthrough

import android.os.Bundle
import android.view.View
import su.arq.arqviewer.R
import su.arq.arqviewer.activities.sign.SignActivity
import android.content.Intent
import android.net.Uri
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import su.arq.arqviewer.activities.walkthrough.card.WalkthroughCardAdapter
import su.arq.arqviewer.activities.walkthrough.card.WalkthroughCardModel
import kotlin.math.roundToInt

class WalkthroughActivity : AppCompatActivity() {

    private var viewPager: ViewPager? = null
    private var cardModels: MutableList<WalkthroughCardModel>? = null
    private var cardAdapter: WalkthroughCardAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        setContentView(R.layout.activity_walkthrough)

        setWindowsFlags()

        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        val logicalDensity = metrics.density
        val itemWidth = (metrics.widthPixels - 114*logicalDensity).roundToInt()

        cardModels = ArrayList()
        cardModels?.add(
            WalkthroughCardModel(
                "ARQ Editor", "ARQ это ваш проводник в мир дополненной реальности." +
                        " Просто загрузите 3D модель и дополните вашу реальность новыми объектами, созданными вами." +
                        " Подробности на нашем сайте."
            )
        )

        cardAdapter = WalkthroughCardAdapter(
            cardModels,
            this,
            itemWidth
        )

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
            val intent = Intent(applicationContext, SignActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }else if (view.id == R.id.goto_website_btn){
            val browserIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(applicationContext.getString(R.string.arq_website))
            )
            startActivity(browserIntent)
            finish()
        }
    }

    private fun setWindowsFlags() {
        this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

    }

}
