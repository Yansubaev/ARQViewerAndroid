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
import su.arq.arqviewer.sign.InputFieldAnimations
import su.arq.arqviewer.webcomunication.loaders.ARQVAuthDataLoader
import su.arq.arqviewer.webcomunication.response.AuthDataResponse

class SignInFragment :
    Fragment(),
    LoaderManager.LoaderCallbacks<AuthDataResponse>,
    Loader.OnLoadCanceledListener<AuthDataResponse>
{

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

        loginField.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                passwordField.requestFocus()
                passwordInput.activateInput()
            }
        }
        passwordField.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                signIn()
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

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<AuthDataResponse> {
        val loader = ARQVAuthDataLoader(
                activity!!.applicationContext,
                loginField.text.toString(),
                passwordField.text.toString()
            )
        loader.registerOnLoadCanceledListener(this)
        return loader
    }

    override fun onLoadFinished(loader: Loader<AuthDataResponse>, data: AuthDataResponse?) {
        if(loader.id == R.id.auth_data_loader && !TextUtils.isEmpty(data?.token)){
            (activity as SignActivity).onTokenReceived(
                ARQAccount(loginField.text.toString()),
                passwordField.text.toString(),
                data?.token
            )
        }
    }

    override fun onLoadCanceled(loader: Loader<AuthDataResponse>) {
        passwordInput.activateInputFail()
        loginInput.activateInputFail()
    }


    override fun onLoaderReset(loader: Loader<AuthDataResponse>) {

    }
}
