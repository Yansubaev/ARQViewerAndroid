package su.arq.arqviewer.sign

import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.accounts.AccountManager
import android.content.Context
import android.os.Bundle
import su.arq.arqviewer.R
import su.arq.arqviewer.sign.activity.AccountRegistrator

class AccountRegistratorService(
    private val registrator: AccountRegistrator
) {
    private var accountAuthenticatorResponse: AccountAuthenticatorResponse?
    private var context: Context
    private var resultBundle: Bundle? = null
    private var accountManager: AccountManager

    init {
        accountAuthenticatorResponse = registrator.accountAuthenticatorResponse
        context = registrator.context
        if(accountAuthenticatorResponse != null){
            accountAuthenticatorResponse?.onRequestContinued()
        }
        accountManager = AccountManager.get(context)

        registrator.setOnTokenReceivedListener { account, password, token ->
            onTokenReceived(account, password, token)
        }
    }

    private fun onTokenReceived(account: Account, password: String?, token: String?) {
        val result = Bundle()
        this.javaClass.simpleName + " signTrace"
        if (accountManager.addAccountExplicitly(account, password, Bundle())) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, token)
            accountManager.setAuthToken(account, account.type, token)

        } else {
            result.putString(
                AccountManager.KEY_ERROR_MESSAGE,
                context.getString(R.string.account_already_exists)
            )
        }
        setAccountAuthenticatorResult(result)
        registrator.onAccountRegistered(account)

        finish()
    }

    private fun setAccountAuthenticatorResult(result: Bundle){
        resultBundle = result
    }

    private fun finish(){
        if(accountAuthenticatorResponse != null){
            if(resultBundle != null){
                accountAuthenticatorResponse?.onResult(resultBundle)
            }else{
                accountAuthenticatorResponse?.onError(
                    AccountManager.ERROR_CODE_CANCELED,
                    "canceled"
                )
            }
            accountAuthenticatorResponse = null
        }
    }

}