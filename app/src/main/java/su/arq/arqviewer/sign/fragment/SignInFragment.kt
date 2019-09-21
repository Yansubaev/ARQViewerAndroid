package su.arq.arqviewer.sign.fragment

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.LoaderManager
import android.support.v4.content.Loader
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import su.arq.arqviewer.R
import su.arq.arqviewer.sign.activity.SignActivity
import su.arq.arqviewer.sign.account.ARQVAccount
import su.arq.arqviewer.sign.loader.ARQVTokenLoader



class SignInFragment : SignFragment(), LoaderManager.LoaderCallbacks<String> {

    private var loginLay: ConstraintLayout? = null
    private var passwordLay:ConstraintLayout? = null
    private var loginTxt: TextView? = null
    private var passwordTxt:TextView? = null
    private var loginField: EditText? = null
    private var passwordField:EditText? = null
    private var aye: ImageButton? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(
            R.layout.fragment_sign_in, container, false
        ) as ViewGroup

        loginLay = rootView.findViewById(R.id.sign_in_login_lay)
        passwordLay = rootView.findViewById(R.id.sign_in_password_lay)
        loginLay?.setOnClickListener(this@SignInFragment)
        passwordLay?.setOnClickListener(this@SignInFragment)

        aye = rootView.findViewById(R.id.sign_in_aye_image)
        aye?.setOnClickListener(this@SignInFragment)


        loginTxt = rootView.findViewById(R.id.sign_in_login_txt)
        loginField = rootView.findViewById(R.id.sign_in_login_field)

        passwordTxt = rootView.findViewById(R.id.sign_in_password_txt)
        passwordField = rootView.findViewById(R.id.sign_in_password_field)

        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_login_lay -> activateInput(loginTxt, loginField, v)
            R.id.sign_in_password_lay -> activateInput(passwordTxt, passwordField, v)
            R.id.sign_in_aye_image -> showAndHidePassword(passwordField)
        }
    }

    fun signIn(){
        Log.i(this.javaClass.simpleName, "signIn method in fragment")
        loaderManager.restartLoader(R.id.auth_token_loader, null, this)
/*
        when {
            TextUtils.isEmpty(loginField?.text) -> loginField?.error = getString(R.string.login)
            TextUtils.isEmpty(passwordField?.text) -> passwordField?.error = getString(R.string.password)
            else -> loaderManager.restartLoader(R.id.auth_token_loader, null, this)
        }
*/
    }

/*
    override fun onCreateLoader(p0: Int, p1: Bundle?): Loader<String> {
        if(p0 == R.id.auth_token_loader){
            return ARQVTokenLoader(
                activity!!.applicationContext,
                loginField?.text.toString(),
                passwordField?.text.toString()
                )
        }
        return
    }
*/

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<String> {
        return ARQVTokenLoader(
                activity!!.applicationContext,
                loginField?.text.toString(),
                passwordField?.text.toString()
            )
    }

    override fun onLoadFinished(loader: Loader<String>, token: String?) {
        if(loader.id == R.id.auth_token_loader && !TextUtils.isEmpty(token)){
            (activity as SignActivity).onTokenReceived(
                ARQVAccount(loginField?.text.toString()),
                passwordField?.text.toString(),
                token
            )
        }
    }

    override fun onLoaderReset(p0: Loader<String>) {

    }

}