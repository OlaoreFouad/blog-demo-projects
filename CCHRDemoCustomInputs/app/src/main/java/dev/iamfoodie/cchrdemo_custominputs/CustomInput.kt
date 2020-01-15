package dev.iamfoodie.cchrdemo_custominputs

import android.content.Context
import android.content.res.TypedArray
import android.text.InputType
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import kotlinx.android.synthetic.main.custom_input.view.*

const val DATETIME = 1
const val NUMBER = 2
const val EMAIL = 3
const val TEXT = 4
const val PASSWORD = 5

class CustomInput @JvmOverloads
    constructor(val ctx: Context, val attributeSet: AttributeSet? = null, val defStyleAttr: Int = 0)
    : ConstraintLayout(ctx, attributeSet, defStyleAttr){

    var type: Int = 4
    var min = 0
    var max = 0
    var hint: String = "Enter text"
    var textOnError = "Invalid Input"

    private var inputIsDirty = false

    init {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_input, this)

        val typedArray = ctx.obtainStyledAttributes(attributeSet, R.styleable.CustomInput)

        min = typedArray.getInt(R.styleable.CustomInput_min, 0)
        max = typedArray.getInt(R.styleable.CustomInput_max, 0)
        hint = typedArray.getString(R.styleable.CustomInput_hint) ?: hint
        textOnError = typedArray.getString(R.styleable.CustomInput_textOnError) ?: textOnError
        type = typedArray.getText(R.styleable.CustomInput_types_enum).toString().toInt()

        typedArray.recycle()

        setInputType()
        if (type != PASSWORD) {
            custom_input.right
        }

        setPadding(16)
    }

    private fun setInputType() {
        custom_input.inputType = when(type) {
            DATETIME -> InputType.TYPE_CLASS_DATETIME
            NUMBER -> InputType.TYPE_CLASS_NUMBER
            EMAIL -> InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            PASSWORD -> InputType.TYPE_TEXT_VARIATION_PASSWORD
            else -> InputType.TYPE_CLASS_TEXT
        }
    }

}