package dev.iamfoodie.filedescriptiordemo.views

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import dev.iamfoodie.filedescriptiordemo.R
import kotlinx.android.synthetic.main.file_descriptor.view.*

class FileDescriptor @JvmOverloads
    constructor(private val ctx: Context, val attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    init {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.file_descriptor, this)

        background = resources.getDrawable(R.drawable.round_file_descriptor_background)
        elevation = 20F

        val typedArray = ctx.obtainStyledAttributes(attributeSet, R.styleable.FileDescriptor)
        val showInfoByDefault = typedArray.getBoolean(R.styleable.FileDescriptor_showInfo, false)
        file_info.visibility = View.GONE

        if (showInfoByDefault) {
            file_info.visibility = View.VISIBLE
        }
    }

}