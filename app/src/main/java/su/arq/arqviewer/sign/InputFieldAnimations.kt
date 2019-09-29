package su.arq.arqviewer.sign

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.support.constraint.ConstraintLayout
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import su.arq.arqviewer.R
import su.arq.arqviewer.sign.fragment.SignFragment

open class InputFieldAnimations(
    protected var context: Context,
    protected var caption: TextView?,
    protected var inputText: EditText?,
    protected var container: View?
) : View.OnClickListener {
    protected val resources: Resources
        get() = context.resources

    protected var isPasswordHidden = true

    private val whiteColor: Int = resources.getColor(R.color.colorWhite, resources.newTheme())
    private val darkColor: Int = resources.getColor(R.color.textDarkColor, resources.newTheme())
    private val accentColor: Int = resources.getColor(R.color.colorAccent, resources.newTheme())
    private val errorColor: Int = resources.getColor(R.color.colorError, resources.newTheme())

    private var cursorStart: Int? = null
    private var cursorEnd: Int? = null

    private var currentBackgroundColor: Int? = null
    private var currentTextColor: Int? = null

    companion object{
        const val ALPHA_ANIMATION_DURATION: Long = 250
        const val TEXT_COLOR_ANIMATION_DURATION: Long = 250
        const val BACKGROUND_COLOR_ANIMATION_DURATION: Long = 250
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.sign_in_aye_image){
            showAndHidePassword()
        }else{
            activateInput()
        }
    }

    protected open fun activateInput() {
        if(currentBackgroundColor == null){
            currentBackgroundColor = whiteColor
        }
        if(currentTextColor == null){
            currentTextColor = darkColor
        }

        startBackgroundColorAnimation(
            container?.background,
            currentBackgroundColor ?: whiteColor,
            accentColor
        )

        //inputText?.visibility = View.VISIBLE

        startAlphaAnimation(caption, 1.0f, 0.5f)
        startColorTextAnimation(
            caption,
            currentTextColor ?: darkColor,
            whiteColor
        )

        inputText?.requestFocus()
        inputText?.requestFocusFromTouch()

        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(inputText, InputMethodManager.SHOW_IMPLICIT)

        inputText?.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (inputText?.text.toString() == "") {
                    startBackgroundColorAnimation(
                        container?.background,
                        currentBackgroundColor ?: accentColor,
                        whiteColor
                    )

                    //inputText?.visibility = View.INVISIBLE

                    startColorTextAnimation(
                        caption,
                        currentTextColor ?: whiteColor,
                        darkColor
                    )
                    startAlphaAnimation(caption, 0.5f, 1.0f)
                }
            }
        }
    }

    protected open fun activateInputFail(){
        startBackgroundColorAnimation(
            container?.background,
            currentBackgroundColor ?: accentColor,
            errorColor
        )

        startColorTextAnimation(
            caption,
            currentTextColor ?: whiteColor,
            whiteColor
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
            SignFragment.ALPHA_ANIMATION_DURATION
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
        anim.duration = SignFragment.TEXT_COLOR_ANIMATION_DURATION
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
        anim.duration = SignFragment.BACKGROUND_COLOR_ANIMATION_DURATION
        anim.start()

        currentBackgroundColor = endValue
    }

    protected open fun showAndHidePassword() {
        cursorEnd = inputText?.selectionEnd
        cursorStart = inputText?.selectionStart

        if (isPasswordHidden) {
            inputText?.transformationMethod = null
            isPasswordHidden = false
        } else {
            inputText?.transformationMethod = PasswordTransformationMethod()
            isPasswordHidden = true
        }
        inputText?.setSelection(cursorStart ?: 0, cursorEnd ?: 0)
    }

}