package dev.iamfoodie.filedescriptiordemo.views

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import dev.iamfoodie.filedescriptiordemo.R
import dev.iamfoodie.filedescriptiordemo.utils.ThumbnailGenerator
import kotlinx.android.synthetic.main.file_descriptor.view.*
import java.lang.Exception

class FileDescriptor @JvmOverloads
    constructor(private val ctx: Context, val attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    var fileUri: Uri? = null
        set(value) {
            field = value
            setUpFileDescriptor()
        }

    private var fileType: FileType? = null

    init {
        val inflater = ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        inflater.inflate(R.layout.file_descriptor, this)

        background = resources.getDrawable(R.drawable.round_file_descriptor_background)
        elevation = 20F

        val typedArray = ctx.obtainStyledAttributes(attributeSet, R.styleable.FileDescriptor)
        val showInfoByDefault = typedArray.getBoolean(R.styleable.FileDescriptor_showInfo, false)
        file_info.visibility = View.GONE
        file_preview_image.visibility = View.INVISIBLE

        if (showInfoByDefault) {
            file_info.visibility = View.VISIBLE
        }
    }

    fun setUpFileDescriptor() {
        setUpFileType()
        val thumb = retrieveFileThumbnail()
        if (thumb == null) {
            Log.d("MainActivity", "thumbnail is null!")
        }
        file_preview_image.visibility = View.VISIBLE
        file_preview_image.setImageBitmap(thumb)
    }

    private fun setUpFileTypeImage() {
        file_type_image.setImageResource(when(fileType) {
            FileType.DOCX -> R.drawable.docx
            FileType.IMAGE -> R.drawable.image
            FileType.MP3 -> R.drawable.mp3
            FileType.MP4 -> R.drawable.video
            FileType.PDF -> R.drawable.pdf
            FileType.TEXT -> R.drawable.txt
            FileType.UNKNOWN -> R.drawable.no_file_selected
            else -> R.drawable.no_file_selected
        })
    }

    private fun setUpFileType() {
        val resolver = ctx.contentResolver
        val type = resolver.getType(fileUri!!)!!

        fileType = if (type.contains("image")) {
            FileType.IMAGE
        } else if (type.contains("video")) {
            FileType.MP4
        } else if (type.contains("application/msword") || type.contains("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
            FileType.DOCX
        } else if (type.contains("audio")) {
            FileType.MP3
        } else if (type.contains("application/pdf")) {
            FileType.PDF
        } else if (type.contains("text/plain")) {
            FileType.TEXT
        } else {
            FileType.UNKNOWN
        }

        setUpFileTypeImage()
    }

    private fun retrieveFileThumbnail(): Bitmap? {

        var thumbnailBitmap: Bitmap? = null

        try {

            thumbnailBitmap = when(fileType) {
                FileType.IMAGE -> MediaStore.Images.Media.getBitmap(ctx.contentResolver, fileUri)
                FileType.MP4 -> ThumbnailGenerator.createVideoThumbnail(fileUri)
                else -> null
            }

        } catch (e: Exception) {
            Log.d("MainActivity", "Error occurred: ${ e.message }")
        }

        return thumbnailBitmap
    }

}

enum class FileType {
    IMAGE, TEXT, PDF, DOCX, MP4, MP3, UNKNOWN
}