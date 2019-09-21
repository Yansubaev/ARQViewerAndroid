package su.arq.arqviewer.sign.fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import su.arq.arqviewer.R


open class SignFragment : Fragment(), View.OnClickListener {

    protected var isPasswordHidden = true

    companion object{
        const val ALPHA_ANIMATION_DURATION: Long = 250
        const val TEXT_COLOR_ANIMATION_DURATION: Long = 250
        const val BACKGROUND_COLOR_ANIMATION_DURATION: Long = 250
    }

    override fun onClick(v: View) {

    }

    protected open fun activateInput(text: TextView?, field: EditText?, container: View?) {
        startBackgroundColorAnimation(
            container?.background,
            resources.getColor(R.color.colorWhite, resources.newTheme()),
            resources.getColor(R.color.colorAccent, resources.newTheme())
        )

        field?.visibility = View.VISIBLE

        startAlphaAnimation(text, 1.0f, 0.5f)
        startColorTextAnimation(
            text,
            resources.getColor(R.color.textDarkColor, resources.newTheme()),
            resources.getColor(R.color.colorWhite, resources.newTheme())
        )

        field?.requestFocus()
        field?.requestFocusFromTouch()

        val inputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT)

        field?.onFocusChangeListener = View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                if (field?.text.toString() == "") {
                    startBackgroundColorAnimation(
                        container?.background,
                        resources.getColor(R.color.colorAccent, resources.newTheme()),
                        resources.getColor(R.color.colorWhite, resources.newTheme())
                    )

                    field?.visibility = View.INVISIBLE

                    startColorTextAnimation(
                        text,
                        resources.getColor(R.color.colorWhite, resources.newTheme()),
                        resources.getColor(R.color.textDarkColor, resources.newTheme())
                    )
                    startAlphaAnimation(text, 0.5f, 1.0f)
                }
            }
        }
    }


    protected open fun startAlphaAnimation(view: View?, startValue: Float, endValue: Float) {
        val anim = ObjectAnimator.ofFloat(
            view,
            "alpha",
            startValue,
            endValue
        )
        anim.duration =
            ALPHA_ANIMATION_DURATION
        anim.start()

        anim.addUpdateListener { animation ->
            val value = animation.getAnimatedValue("alpha") as Float

            val params = view?.layoutParams as ConstraintLayout.LayoutParams
            params.verticalBias = 1.5f - value
            view.layoutParams = params
        }
    }

    protected open fun startColorTextAnimation(textView: TextView?, startValue: Int, endValue: Int) {
        val anim = ObjectAnimator.ofArgb(
            textView,
            "textColor",
            startValue,
            endValue
        )
        anim.duration =
            TEXT_COLOR_ANIMATION_DURATION
        anim.start()
    }

    protected open fun startBackgroundColorAnimation(background: Drawable?, startValue: Int, endValue: Int) {
        val anim = ObjectAnimator.ofArgb(
            background,
            "tint",
            startValue,
            endValue
        )
        anim.duration =
            BACKGROUND_COLOR_ANIMATION_DURATION
        anim.start()
    }

    protected open fun showAndHidePassword(field: EditText?) {
        if (isPasswordHidden) {
            field?.transformationMethod = null
            isPasswordHidden = false
        } else {
            field?.transformationMethod = PasswordTransformationMethod()
            isPasswordHidden = true
        }
    }

}
