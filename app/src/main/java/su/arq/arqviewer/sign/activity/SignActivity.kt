package su.arq.arqviewer.sign.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import su.arq.arqviewer.projects.activity.ProjectsActivity
import java.util.ArrayList
import android.accounts.AccountManager
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.app.Activity
import android.widget.ImageButton
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import su.arq.arqviewer.R
import su.arq.arqviewer.sign.page.model.SignPageModel
import su.arq.arqviewer.sign.page.adapter.SignPagerAdapter
import su.arq.arqviewer.sign.fragment.SignInFragment
import su.arq.arqviewer.utils.EXTRA_ARQ_ACCOUNT

class SignActivity : FragmentActivity() {

    private var models: MutableList<SignPageModel>? = null
    private var pagerAdapter: PagerAdapter? = null

    private lateinit var viewPager: ViewPager
    private lateinit var auth: TextView
    private lateinit var signButton: ImageButton

    private var accountAuthenticatorResponse: AccountAuthenticatorResponse? = null
    private var mResultBundle: Bundle? = null

    private var signInFragment: SignInFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        accountAuthenticatorResponse =
            intent.getParcelableExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE)

        if(accountAuthenticatorResponse != null){
            accountAuthenticatorResponse?.onRequestContinued()
        }

        val from = intent.getStringExtra("FROM_ACTIVITY")
        if(from == "Projects"){
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left)
        } else {
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        setContentView(R.layout.activity_sign)
        setWindowsFlags()

        signInFragment = SignInFragment()

        models = ArrayList()
        models?.add(SignPageModel(signInFragment!!))

        viewPager = findViewById(R.id.viewPagerSign)
        auth = findViewById(R.id.authorization)
        signButton = findViewById(R.id.next_btn)

        pagerAdapter = SignPagerAdapter(
            supportFragmentManager,
            models as ArrayList<SignPageModel>
        )
        viewPager.adapter = pagerAdapter
    }

    fun onTokenReceived(account: Account, password: String?, token: String?) {
        val am = AccountManager.get(this)
        val result = Bundle()

        if (am.addAccountExplicitly(account, password, Bundle())) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, token)
            am.setAuthToken(account, account.type, token)

        } else {
            result.putString(
                AccountManager.KEY_ERROR_MESSAGE,
                getString(R.string.account_already_exists)
            )
        }

        setAccountAuthenticatorResult(result)
        setResult(Activity.RESULT_OK)

        val intent = Intent(applicationContext, ProjectsActivity::class.java)
        intent.putExtra(EXTRA_ARQ_ACCOUNT, account)

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(intent)
        finish()
    }

    fun signIn(view: View){
        signButton.isClickable = false
        signInFragment?.signIn()
    }

    fun signFailed(){
        signButton.isClickable = true
    }

    fun backButton(view: View) {
        super.onBackPressed()
    }

    fun selectPage(view: View) {
        if (view.id == R.id.authorization) {
            viewPager.setCurrentItem(1, true)
        }
    }

    override fun finish(){
        if(accountAuthenticatorResponse != null){
            if(mResultBundle != null){
                accountAuthenticatorResponse?.onResult(mResultBundle)
            }else{
                accountAuthenticatorResponse?.onError(AccountManager.ERROR_CODE_CANCELED, "canceled")
            }
            accountAuthenticatorResponse = null
        }

        super.finish()
    }


    override fun onResume() {
        super.onResume()

        setWindowsFlags()
    }

    private fun setAccountAuthenticatorResult(result: Bundle){
        mResultBundle = result
    }

    private fun setWindowsFlags() {
        this.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                )
    }

}
