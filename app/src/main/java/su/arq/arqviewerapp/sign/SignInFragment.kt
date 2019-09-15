package su.arq.arqviewerapp.sign

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import su.arq.arqviewerapp.R

class SignInFragment : SignFragment() {

    private var loginLay: ConstraintLayout? = null
    private var passwordLay:ConstraintLayout? = null
    private var loginOrNumTxt: TextView? = null
    private var passwordTxt:TextView? = null
    private var loginOrNumField: EditText? = null
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


        loginOrNumTxt = rootView.findViewById(R.id.sign_in_login_txt)
        loginOrNumField = rootView.findViewById(R.id.sign_in_login_field)

        passwordTxt = rootView.findViewById(R.id.sign_in_password_txt)
        passwordField = rootView.findViewById(R.id.sign_in_password_field)

        return rootView
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.sign_in_login_lay -> activateInput(loginOrNumTxt, loginOrNumField, v)
            R.id.sign_in_password_lay -> activateInput(passwordTxt, passwordField, v)
            R.id.sign_in_aye_image -> showAndHidePassword(passwordField)
        }
    }

}
