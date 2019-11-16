package su.arq.arqviewer.activities.sign

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import su.arq.arqviewer.activities.projects.ProjectsActivity
import java.util.ArrayList
import android.accounts.AccountManager
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import su.arq.arqviewer.R
import su.arq.arqviewer.activities.sign.page.SignPageModel
import su.arq.arqviewer.activities.sign.page.SignPagerAdapter
import su.arq.arqviewer.activities.sign.fragment.SignInFragment
import su.arq.arqviewer.activities.sign.registrator.AccountRegistrator
import su.arq.arqviewer.activities.sign.registrator.AccountRegistratorService
import su.arq.arqviewer.utils.EXTRA_ARQ_ACCOUNT

class SignActivity : FragmentActivity(), AccountRegistrator {

    override val context: Context
        get() = applicationContext
    override var accountAuthenticatorResponse: AccountAuthenticatorResponse? = null
    override var onTokenReceiver: ((account: Account, password: String?, token: String?) -> Unit)? = null

    private var models: MutableList<SignPageModel>? = null
    private var pagerAdapter: PagerAdapter? = null

    private lateinit var viewPager: ViewPager
    private lateinit var auth: TextView
    private lateinit var signButton: ImageButton
    private lateinit var nextArrow: ImageView
    private lateinit var progressCircle: ProgressBar
    private lateinit var signInFragment: SignInFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountAuthenticatorResponse =
            intent.getParcelableExtra(
                AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE
            )
        AccountRegistratorService(this)
        setContentView(R.layout.activity_sign)
        setWindowsFlags()

        signInFragment = SignInFragment()
        models = ArrayList()
        models?.add(SignPageModel(signInFragment))

        viewPager = findViewById(R.id.viewPagerSign)
        auth = findViewById(R.id.authorization)
        signButton = findViewById(R.id.next_btn)
        nextArrow = findViewById(R.id.sign_next_btn_arrow)
        progressCircle = findViewById(R.id.sign_progress_bar)

        pagerAdapter = SignPagerAdapter(
            supportFragmentManager,
            models as ArrayList<SignPageModel>
        )
        viewPager.adapter = pagerAdapter

        auth.apply {
            val ttf = Typeface.createFromAsset(applicationContext.assets, "fonts/ttnorms_bold.ttf")
            typeface = ttf
        }
    }

    fun signIn(view: View){
        signButton.isClickable = false
        signInFragment.signIn()
        //startLoadingList()
    }

    override fun signFailed(){
        signButton.isClickable = true
        cancelLoading()
    }

    fun backButton(view: View) {
        super.onBackPressed()
    }

    fun selectPage(view: View) {
        if (view.id == R.id.authorization) {
            viewPager.setCurrentItem(1, true)
        }
    }

    override fun onTokenReceived(account: Account, password: String?, token: String?) {
        Log.d(this.javaClass.simpleName + " signTrace", "onTokenReceived")
        onTokenReceiver?.invoke(account, password, token)
    }

    override fun onAccountRegistered(account: Account) {
        openProjectsActivity(account)
    }

    override fun onResume() {
        super.onResume()
        setWindowsFlags()
    }

    private fun setWindowsFlags() {
        this.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                )
    }

    private fun openProjectsActivity(account: Account){
        setResult(Activity.RESULT_OK)

        val intent = Intent(applicationContext, ProjectsActivity::class.java)
        intent.putExtra(EXTRA_ARQ_ACCOUNT, account)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        finish()
    }

    fun startLoading(){
        nextButtonAnimations(0.5f, 1f)
    }

    fun cancelLoading(){
        nextButtonAnimations(1f, 0.5f)
    }

    private fun nextButtonAnimations(start: Float, end: Float){
        val animScaleProgr = ValueAnimator.ofFloat(start, end)
        animScaleProgr.addUpdateListener {
            val v = it.animatedValue as Float
            progressCircle.scaleX = v
            progressCircle.scaleY = v
            progressCircle.alpha = v*2 - 1f
        }
        val animScaleArrow = ValueAnimator.ofFloat(end, start)
        animScaleArrow.addUpdateListener {
            val v = it.animatedValue as Float
            nextArrow.scaleX = v
            nextArrow.scaleY = v
            nextArrow.alpha = v*2 - 1f
        }

        animScaleProgr.duration = 150L
        animScaleArrow.duration = 150L

        animScaleArrow.start()
        animScaleProgr.startDelay = 150L
        animScaleProgr.start()
    }
}
