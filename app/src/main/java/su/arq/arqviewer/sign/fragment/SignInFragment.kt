package su.arq.arqviewer.sign.fragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import su.arq.arqviewer.R
import su.arq.arqviewer.sign.activity.SignActivity
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.sign.InputFieldModel
import su.arq.arqviewer.sign.activity.AccountRegistrator
import su.arq.arqviewer.webcomunication.loaders.ARQVAuthDataLoader
import su.arq.arqviewer.webcomunication.response.AuthDataResponse
import su.arq.arqviewer.webcomunication.response.AuthenticationDataProvider

class SignInFragment :
    Fragment(),
    LoaderManager.LoaderCallbacks<AuthenticationDataProvider>,
    Loader.OnLoadCanceledListener<AuthenticationDataProvider>
{
    private var aye: ImageButton? = null
    private var mLoaderManager: LoaderManager? = null

    private lateinit var loginField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginInput: InputFieldModel
    private lateinit var passwordInput: InputFieldModel

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
        loginInput = InputFieldModel(context!!, loginTxt, loginField, loginLay)
        loginLay.setOnClickListener(loginInput)

        val passwordTxt = rootView.findViewById<TextView>(R.id.sign_in_password_txt)
        passwordField = rootView.findViewById(R.id.sign_in_password_field)
        val passwordLay = rootView.findViewById<ConstraintLayout>(R.id.sign_in_password_lay)
        passwordInput = InputFieldModel(context!!, passwordTxt, passwordField, passwordLay)
        passwordLay.setOnClickListener(passwordInput)

        passwordField.setOnEditorActionListener { v, _, event ->
            if(event != null){
                true
            }else{
                (activity as SignActivity).signIn(v)
                false
            }
        }

        aye = rootView.findViewById(R.id.sign_in_aye_image)
        aye?.setOnClickListener(passwordInput)

        mLoaderManager = LoaderManager.getInstance(this@SignInFragment)
        return rootView
    }

    fun signIn(){
        when {
            TextUtils.isEmpty(loginField.text) ->
                loginField.error = getString(R.string.login)
            TextUtils.isEmpty(passwordField.text) ->
                passwordField.error = getString(R.string.password)
            else ->
                mLoaderManager?.restartLoader(R.id.auth_data_loader, null, this)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<AuthenticationDataProvider> {
        val loader = ARQVAuthDataLoader(
                activity!!.applicationContext,
                loginField.text.toString(),
                passwordField.text.toString()
            )
        loader.registerOnLoadCanceledListener(this)
        return loader
    }

    override fun onLoadFinished(
        loader: Loader<AuthenticationDataProvider>,
        data: AuthenticationDataProvider
    ) {
        if(loader.id == R.id.auth_data_loader && !TextUtils.isEmpty(data.token)){
            (activity as AccountRegistrator).onTokenReceived(
                ARQAccount(data.email),
                passwordField.text.toString(),
                data.token
            )
        }
    }

    override fun onLoadCanceled(loader: Loader<AuthenticationDataProvider>) {
        passwordInput.activateInputFail()
        loginInput.activateInputFail()
        (activity as SignActivity).signFailed()
    }

    override fun onLoaderReset(loader: Loader<AuthenticationDataProvider>) {  }
}
