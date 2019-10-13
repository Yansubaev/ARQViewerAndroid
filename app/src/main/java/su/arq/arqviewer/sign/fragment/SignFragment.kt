package su.arq.arqviewer.sign.fragment

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.drawable.Drawable
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import su.arq.arqviewer.R


open class SignFragment : Fragment(), View.OnClickListener {

    protected var isPasswordHidden = true
    private var cursorStart: Int? = null
    private var cursorEnd: Int? = null

    private var currentBackgroundColor: Int? = null
    private var currentTextColor: Int? = null

    companion object{
        const val ALPHA_ANIMATION_DURATION: Long = 250
        const val TEXT_COLOR_ANIMATION_DURATION: Long = 250
        const val BACKGROUND_COLOR_ANIMATION_DURATION: Long = 250
    }

    override fun onClick(v: View) {

    }

    protected open fun activateInput(text: TextView?, field: EditText?, container: View?) {
        if(currentBackgroundColor == null){
            currentBackgroundColor = resources.getColor(R.color.colorWhite, resources.newTheme())
        }
        if(currentTextColor == null){
            currentTextColor = resources.getColor(R.color.textDarkColor, resources.newTheme())
        }

        startBackgroundColorAnimation(
            container?.background,
            currentBackgroundColor ?: resources.getColor(R.color.colorWhite, resources.newTheme()),
            resources.getColor(R.color.colorAccent, resources.newTheme())
        )

        //field?.visibility = View.VISIBLE

        startAlphaAnimation(text, 1.0f, 0.5f)
        startColorTextAnimation(
            text,
            currentTextColor ?: resources.getColor(R.color.textDarkColor, resources.newTheme()),
            resources.getColor(R.color.colorWhite, resources.newTheme())
        )

        field?.requestFocus()
        field?.requestFocusFromTouch()

        val inputMethodManager = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(field, InputMethodManager.SHOW_IMPLICIT)

        field?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (field?.text.toString() == "") {
                    startBackgroundColorAnimation(
                        container?.background,
                        currentBackgroundColor ?: resources.getColor(R.color.colorAccent, resources.newTheme()),
                        resources.getColor(R.color.colorWhite, resources.newTheme())
                    )

                    //field?.visibility = View.INVISIBLE

                    startColorTextAnimation(
                        text,
                        currentTextColor ?: resources.getColor(R.color.colorWhite, resources.newTheme()),
                        resources.getColor(R.color.textDarkColor, resources.newTheme())
                    )
                    startAlphaAnimation(text, 0.5f, 1.0f)
                }
            }
        }
    }

    protected open fun activateInputFail(text: TextView?, field: EditText?, container: View?){
        startBackgroundColorAnimation(
            container?.background,
            currentBackgroundColor ?: resources.getColor(R.color.colorAccent, resources.newTheme()),
            resources.getColor(R.color.colorError, resources.newTheme())
        )

        startColorTextAnimation(
            text,
            currentTextColor ?: resources.getColor(R.color.colorWhite, resources.newTheme()),
            resources.getColor(R.color.colorWhite, resources.newTheme())
        )
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
        anim.duration = TEXT_COLOR_ANIMATION_DURATION
        anim.start()

        currentTextColor = endValue
    }

    protected open fun startBackgroundColorAnimation(background: Drawable?, startValue: Int, endValue: Int) {
        val anim = ObjectAnimator.ofArgb(
            background,
            "tint",
            startValue,
            endValue
        )
        anim.duration = BACKGROUND_COLOR_ANIMATION_DURATION
        anim.start()

        currentBackgroundColor = endValue
    }

    protected open fun showAndHidePassword(field: EditText?) {
        cursorEnd = field?.selectionEnd
        cursorStart = field?.selectionStart

        if (isPasswordHidden) {
            field?.transformationMethod = null
            isPasswordHidden = false
        } else {
            field?.transformationMethod = PasswordTransformationMethod()
            isPasswordHidden = true
        }
        field?.setSelection(cursorStart ?: 0, cursorEnd ?: 0)
    }

}
