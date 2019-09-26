package su.arq.arqviewer.account

import android.accounts.Account
import android.os.Parcel

class ARQVAccount : Account {

    constructor(`in`: Parcel) : super(`in`)
    constructor(name: String) : super(name, TYPE)

    companion object {
        const val TYPE = "su.arq.arqviewer"
        const val TOKEN_FULL_ACCESS = "su.arq.arqviewer.TOKEN_FULL_ACCESS"
        const val KEY_PASSWORD = "su.arq.arqviewer.KEY_PASSWORD"
    }
}