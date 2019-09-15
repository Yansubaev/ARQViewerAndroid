package su.arq.arqviewerapp.sign

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.TextView
import su.arq.arqviewerapp.R
import su.arq.arqviewerapp.launcher.CHOSEN_PAGE
import su.arq.arqviewerapp.launcher.LauncherActivity
import su.arq.arqviewerapp.projects.ProjectsActivity
import java.util.ArrayList

class SignActivity : FragmentActivity() {

    private var viewPager: ViewPager? = null
    private var models: MutableList<SignPageModel>? = null
    private var pagerAdapter: PagerAdapter? = null
    private var regis: TextView? = null
    private var auth:TextView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        setContentView(R.layout.activity_sign)


        setWindowsFlags()

        models = ArrayList()
        models?.add(SignPageModel(SignInFragment()))

        viewPager = findViewById(R.id.viewPagerSign)
        auth = findViewById(R.id.authorization)

        pagerAdapter = SignPagerAdapter(supportFragmentManager, models as ArrayList<SignPageModel>)
        viewPager?.adapter = pagerAdapter
    }

    override fun onResume() {
        super.onResume()

        setWindowsFlags()
    }

    fun backButton(view: View) {
        super.onBackPressed()
    }

    fun selectPage(view: View) {
        if (view.id == R.id.authorization) {
            viewPager?.setCurrentItem(1, true)
        }
    }

    private fun setWindowsFlags() {
        this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)

    }

    fun openProjects(view: View){
        val intent = Intent(applicationContext, ProjectsActivity::class.java)
        startActivity(intent)

    }
}
