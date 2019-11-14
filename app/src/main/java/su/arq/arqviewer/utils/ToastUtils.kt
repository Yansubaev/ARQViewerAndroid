package su.arq.arqviewer.utils

import android.content.res.Resources
import android.view.View
import com.google.android.material.snackbar.Snackbar
import su.arq.arqviewer.R

fun toastErrorMessage(rootView: View, message: String, resources: Resources){
    Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        .apply {
            view.setBackgroundColor(resources.getColor(
                R.color.colorError,
                resources.newTheme()
            )) }.show()
}

fun toastInternetUnavailable(rootView: View, resources: Resources){
    Snackbar.make(rootView, "Проверьте интернет соединение", Snackbar.LENGTH_LONG)
        .apply {
            view.setBackgroundColor(resources.getColor(
                R.color.colorError,
                resources.newTheme()
            )) }.show()
}