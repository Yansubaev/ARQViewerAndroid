package su.arq.arqviewer.sign.activity

import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.Context

interface AccountRegistrator {
    val context: Context
    var accountAuthenticatorResponse: AccountAuthenticatorResponse?

    var onTokenReceiver: ((account: Account, password: String?, token: String?) -> Unit)?
    fun setOnTokenReceivedListener(m: ((account: Account, password: String?, token: String?) -> Unit)){
        onTokenReceiver = m
    }

    fun onTokenReceived(account: Account, password: String?, token: String?)
    fun onAccountRegistered(account: Account)
}