package su.arq.arqviewer.activities.sign.fragment

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
import su.arq.arqviewer.activities.sign.SignActivity
import su.arq.arqviewer.account.ARQAccount
import su.arq.arqviewer.activities.sign.AnimatedInputField
import su.arq.arqviewer.activities.sign.registrator.AccountRegistrator
import su.arq.arqviewer.webcomunication.loaders.ARQVAuthDataLoader
import su.arq.arqviewer.webcomunication.response.AuthenticationData

class SignInFragment :
    Fragment(),
    LoaderManager.LoaderCallbacks<AuthenticationData>,
    Loader.OnLoadCanceledListener<AuthenticationData>
{
    private var aye: ImageButton? = null
    private var mLoaderManager: LoaderManager? = null

    private lateinit var loginField: EditText
    private lateinit var passwordField: EditText
    private lateinit var loginInput: AnimatedInputField
    private lateinit var passwordInput: AnimatedInputField

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
        loginInput = AnimatedInputField(context!!, loginTxt, loginField, loginLay)
        loginLay.setOnClickListener(loginInput)

        val passwordTxt = rootView.findViewById<TextView>(R.id.sign_in_password_txt)
        passwordField = rootView.findViewById(R.id.sign_in_password_field)
        val passwordLay = rootView.findViewById<ConstraintLayout>(R.id.sign_in_password_lay)
        passwordInput = AnimatedInputField(context!!, passwordTxt, passwordField, passwordLay)
        passwordLay.setOnClickListener(passwordInput)

        passwordField.setOnEditorActionListener { v, _, event ->
            if(event != null){
                false
            }else{
                (activity as SignActivity).signIn(v)
                true
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
                loginField.error = "Логин не должен быть пустым"
            TextUtils.isEmpty(passwordField.text) ->
                passwordField.error = "Пароль не должен быть пустым"
            else ->
                mLoaderManager?.restartLoader(R.id.auth_data_loader, null, this)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<AuthenticationData> {
        val loader = ARQVAuthDataLoader(
                activity!!.applicationContext,
                loginField.text.toString(),
                passwordField.text.toString()
            )
        loader.registerOnLoadCanceledListener(this)
        return loader
    }

    override fun onLoadFinished(loader: Loader<AuthenticationData>, data: AuthenticationData) {
        if(loader.id == R.id.auth_data_loader && !TextUtils.isEmpty(data.token)){
            Log.d(this.javaClass.simpleName + " signTrace", "onLoadFinished, ")
            data.printData()
            (activity as AccountRegistrator).onTokenReceived(
                ARQAccount(data.email),
                passwordField.text.toString(),
                data.token
            )
        }
    }

    override fun onLoadCanceled(loader: Loader<AuthenticationData>) {
        passwordInput.activateInputFail()
        loginInput.activateInputFail()
        (activity as SignActivity).signFailed()
    }

    override fun onLoaderReset(loader: Loader<AuthenticationData>) {  }
}
