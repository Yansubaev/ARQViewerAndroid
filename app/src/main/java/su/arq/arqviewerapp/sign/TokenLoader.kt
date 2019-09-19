package su.arq.arqviewerapp.sign

import android.content.Context
import android.support.v4.content.AsyncTaskLoader

class TokenLoader(context: Context, login: String, password: String) : AsyncTaskLoader<String>(context){

    private var mObtainTokenUrl: String
    private var mLogin: String
    private var mPassword: String
    private lateinit var mAuthToken: String

    init {
        mObtainTokenUrl = context.getString(0)
        mLogin = login
        mPassword = password
    }

    override fun loadInBackground(): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object{
        @JvmStatic
        fun signIn(context: Context, login: String?, password: String): String? = null

    }
}