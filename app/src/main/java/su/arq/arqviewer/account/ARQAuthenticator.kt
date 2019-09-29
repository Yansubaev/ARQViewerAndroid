package su.arq.arqviewer.account

import android.accounts.AbstractAccountAuthenticator
import android.accounts.Account
import android.accounts.AccountAuthenticatorResponse
import android.content.Context
import android.os.Bundle
import android.accounts.AccountManager
import android.content.Intent
import android.text.TextUtils
import su.arq.arqviewer.sign.activity.SignActivity
import su.arq.arqviewer.webcomunication.loaders.ARQVAuthDataLoader
import su.arq.arqviewer.utils.EXTRA_TOKEN_TYPE

class ARQAuthenticator(var mContext: Context) : AbstractAccountAuthenticator(mContext) {
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        options: Bundle?
    ): Bundle? {
        return null
    }

    override fun updateCredentials(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle? {
        return null
    }

    override fun getAuthToken(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        authTokenType: String?,
        options: Bundle?
    ): Bundle {
        val result = Bundle()
        val am = AccountManager.get(mContext.applicationContext)
        var authToken = am.peekAuthToken(account, authTokenType)
        if (TextUtils.isEmpty(authToken)) {
            val password = am.getPassword(account)
            if (!TextUtils.isEmpty(password)) {
                authToken = ARQVAuthDataLoader.signIn(
                    mContext,
                    account?.name,
                    password
                )
            }
        }
        if (!TextUtils.isEmpty(authToken)) {
            result.putString(AccountManager.KEY_ACCOUNT_NAME, account?.name)
            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account?.type)
            result.putString(AccountManager.KEY_AUTHTOKEN, authToken)
        } else {
            val intent = Intent(mContext, SignActivity::class.java)
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            intent.putExtra(EXTRA_TOKEN_TYPE, authTokenType)
            val bundle = Bundle()
            bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        }
        return result
    }

    override fun hasFeatures(
        response: AccountAuthenticatorResponse?,
        account: Account?,
        features: Array<out String>?
    ): Bundle? {
        return null
    }

    override fun editProperties(
        response: AccountAuthenticatorResponse?,
        accountType: String?
    ): Bundle? {
        return null
    }

    override fun addAccount(
        response: AccountAuthenticatorResponse?,
        accountType: String?,
        authTokenType: String?,
        requiredFeatures: Array<out String>?,
        options: Bundle?
    ): Bundle {
        val intent = Intent(mContext, SignActivity::class.java)
        intent.putExtra(EXTRA_TOKEN_TYPE, accountType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = Bundle()
        if (options != null) {
            bundle.putAll(options)
        }
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        return bundle
    }

    override fun getAuthTokenLabel(authTokenType: String?): String? {
        return null
    }

}