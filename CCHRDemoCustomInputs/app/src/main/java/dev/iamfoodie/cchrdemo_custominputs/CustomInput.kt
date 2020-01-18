package dev.iamfoodie.cchrdemo_custominputs

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.setPadding
import kotlinx.android.synthetic.main.custom_input.view.*

const val DATETIME = 1
const val NUMBER = 2
const val EMAIL = 3
const val TEXT = 4
const val PASSWORD = 5

@RequiresApi(Build.VERSION_CODES.O)
class CustomInput @JvmOverloads
    constructor(val ctx: Context, val attributeSet: AttributeSet? = null, val defStyleAttr: Int = 0)
    : ConstraintLayout(ctx, attributeSet, defStyleAttr){

    var type: Int = TEXT
    var min = 0
    var max = 0
    var hint: String = "Enter text"
    var textOnError = "Invalid Input"
    var length: Int = 0

    private var inputIsDirty = false
    private var isValid = false

    init {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.custom_input, this)

        val typedArray = ctx.obtainStyledAttributes(attributeSet, R.styleable.CustomInput)

        min = typedArray.getInt(R.styleable.CustomInput_min, min)
        max = typedArray.getInt(R.styleable.CustomInput_max, max)
        hint = typedArray.getString(R.styleable.CustomInput_hint) ?: when(type) {
            PASSWORD -> "Enter Password"
            NUMBER -> "Enter Number"
            EMAIL -> "Enter Email"
            else -> "Enter Text"
        }
        type = typedArray.getText(R.styleable.CustomInput_types_enum)?.toString()?.toInt() ?: type
        textOnError = typedArray.getString(R.styleable.CustomInput_textOnError) ?: ValidationErrors.getError(type)
        length = typedArray.getInt(R.styleable.CustomInput_length, length)

        setInputType()
        custom_input.hint = hint
        if (type == PASSWORD) {
            custom_input.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_view, 0)
            if (min != 0) {
                textOnError = typedArray.getString(R.styleable.CustomInput_textOnError) ?: ValidationErrors.getError(type, min)
            }
        }
        typedArray.recycle()

        validation_text.text = textOnError
        validation_text.setTextColor(Color.RED)
        validation_text.visibility = View.INVISIBLE
        custom_input.isFocusedByDefault = false

        custom_input.setOnFocusChangeListener { _, focused ->
            var drawableVisibility: Pair<Int, Boolean> = if (focused) {
                (if (checkForValidity()) R.drawable.state_focused else R.drawable.state_error) to checkForValidity()
            } else {
                if (checkForValidity()) {
                    R.drawable.state_neutral to false
                } else {
                    R.drawable.state_error to true
                }
            }
            custom_input.setBackgroundResource(drawableVisibility.first)
            validation_text.visibility = if (drawableVisibility.second) View.VISIBLE else View.INVISIBLE
        }

        custom_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                inputIsDirty = true
                val valid = checkForValidity()
                if (valid) {
                    validation_text.visibility = View.INVISIBLE
                    custom_input.setBackgroundResource(R.drawable.state_focused)
                } else {
                    validation_text.visibility = View.VISIBLE
                    custom_input.setBackgroundResource(R.drawable.state_error)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }
        })

        setPadding(48)
    }

    private fun checkForValidity(): Boolean {
        var valid = false
        when(type) {
            PASSWORD, TEXT -> {
                if (length != 0) {
                    val text = custom_input.text.toString()
                    valid = text.length == length
                }
            }
            NUMBER -> {
                if (min != 0 || max != 0) {
                    val n = custom_input.text.toString()
                    if (!n.isEmpty()) {
                        valid = (min <= n.toInt()) && (max >= n.toInt())
                    }
                }
            }
            EMAIL -> {
                val regex = Regex("(^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$)")
                valid = regex.matches(custom_input.text.toString())
            }
        }

        return valid && inputIsDirty
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

    private fun configureInputTypes() {

    }

}