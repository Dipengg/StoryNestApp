package com.dicoding.storyapp.utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dicoding.storyapp.R

class PasswordTextField @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs), View.OnTouchListener {

    private var showPasswordImage: Drawable? = null
    private var hidePasswordImage: Drawable? = null

    private var isPasswordVisible = false

    init {

        showPasswordImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_24)
        hidePasswordImage = ContextCompat.getDrawable(context, R.drawable.ic_baseline_visibility_off_24)

        inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

        setCompoundDrawablesWithIntrinsicBounds(null, null, hidePasswordImage, null)
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().length < 8) {
                    setError("Password tidak boleh kurang dari 8 karakter", null)
                } else {
                    error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val drawableRight = compoundDrawables[2]
            drawableRight?.let {
                if (event.rawX >= (right - drawableRight.bounds.width())) {
                    togglePasswordVisibility()
                    return true
                }
            }
        }
        return false
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible

        inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }

        val visibilityDrawable = if (isPasswordVisible) hidePasswordImage else showPasswordImage
        setCompoundDrawablesWithIntrinsicBounds(null, null, visibilityDrawable, null)

        setSelection(text?.length ?: 0)
    }

}