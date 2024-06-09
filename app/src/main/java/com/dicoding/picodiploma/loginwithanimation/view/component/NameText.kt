package com.dicoding.picodiploma.loginwithanimation.view.component

import android.content.Context
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText

class NameText : AppCompatEditText, View.OnTouchListener {
    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }


    private fun init() {
        hint = "Masukkan Nama"
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        maxLines = 1
        inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME

        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank() && s.length < 2) {
                    error = "Minimum 2 Characters"
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        return false
    }

}