package su.arq.arqviewer.sign

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import org.w3c.dom.Text
import su.arq.arqviewer.R
import java.security.Key

open class InputFieldModel(
    protected var context: Context,
    protected var caption: TextView?,
    protected var inputText: EditText?,
    protected var container: View?
) : View.OnClickListener {

    protected val resources: Resources
        get() = context.resources

    protected var isPasswordHidden = true
    protected var stateFailed: Boolean = false

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

    init {
        inputText?.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                reactivateInput()
                stateFailed = false
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {}

        })
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.sign_in_aye_image){
            showAndHidePassword()
        }else{
            activateInput()
        }
    }

    open fun reactivateInput(){
        if(stateFailed) {
            stateFailed = false

            if(currentBackgroundColor == null){
                currentBackgroundColor = whiteColor
            }

            startBackgroundColorAnimation(
                container?.background,
                currentBackgroundColor ?: whiteColor,
                accentColor
            )
        }
    }

    open fun activateInput() {
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

        inputText?.visibility = View.VISIBLE

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

                    inputText?.visibility = View.INVISIBLE

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

    open fun activateInputFail(){
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

        stateFailed = true
    }

    protected open fun startAlphaAnimation(view: View?, startValue: Float, endValue: Float) {
        val anim = ObjectAnimator.ofFloat(
            view,
            "alpha",
            startValue,
            endValue
        )
        anim.duration = ALPHA_ANIMATION_DURATION
        anim.start()

        anim.addUpdateListener { animation ->
            val alphaValue = animation.getAnimatedValue("alpha") as Float

            val params = view?.layoutParams as ConstraintLayout.LayoutParams
            params.verticalBias = 1.5f - alphaValue
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