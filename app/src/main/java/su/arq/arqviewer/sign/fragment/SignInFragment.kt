package su.arq.arqviewer.sign.fragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import su.arq.arqviewer.R
import su.arq.arqviewer.sign.activity.SignActivity
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.sign.InputFieldAnimations
import su.arq.arqviewer.webcomunication.callbacks.error.WebAPIErrorCallbackListener
import su.arq.arqviewer.webcomunication.loaders.ARQVAuthDataLoader

class SignInFragment : Fragment(), LoaderManager.LoaderCallbacks<String>, WebAPIErrorCallbackListener {
    private var aye: ImageButton? = null
    private var mLoaderManager: LoaderManager? = null

    private lateinit var loginField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginInput: InputFieldAnimations
    private lateinit var passwordInput: InputFieldAnimations

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_sign_in, container, false
        ) as ViewGroup

        val loginTxt = rootView.findViewById<TextView>(R.id.sign_in_login_txt)
        loginField = rootView.findViewById(R.id.sign_in_login_field)
        val loginLay = rootView.findViewById<ConstraintLayout>(R.id.sign_in_login_lay)
        loginInput = InputFieldAnimations(context!!, loginTxt, loginField, loginLay)
        loginLay.setOnClickListener(loginInput)

        val passwordTxt = rootView.findViewById<TextView>(R.id.sign_in_password_txt)
        passwordField = rootView.findViewById(R.id.sign_in_password_field)
        val passwordLay = rootView.findViewById<ConstraintLayout>(R.id.sign_in_password_lay)
        passwordInput = InputFieldAnimations(context!!, passwordTxt, passwordField, passwordLay)
        passwordLay.setOnClickListener(passwordInput)

        aye = rootView.findViewById(R.id.sign_in_aye_image)
        aye?.setOnClickListener(passwordInput)

        mLoaderManager = LoaderManager.getInstance(this@SignInFragment)
        return rootView
    }

    fun signIn(){
        when {
            TextUtils.isEmpty(loginField.text) -> loginField.error = getString(R.string.login)
            TextUtils.isEmpty(passwordField.text) -> passwordField.error = getString(R.string.password)
            else -> mLoaderManager?.restartLoader(R.id.auth_data_loader, null, this)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<String> {
        return ARQVAuthDataLoader(
                activity!!.applicationContext,
                loginField.text.toString(),
                passwordField.text.toString()
            ).addAuthErrorCallbackListeners(this)
    }

    override fun onLoadFinished(loader: Loader<String>, token: String?) {
        if(loader.id == R.id.auth_data_loader && !TextUtils.isEmpty(token)){
            (activity as SignActivity).onTokenReceived(
                ARQAccount(loginField.text.toString()),
                passwordField.text.toString(),
                token
            )
        }
    }

    override fun onLoaderReset(p0: Loader<String>) {

    }

    override fun error(message: String?, httpResponseCode: Int?) {

    }
}
