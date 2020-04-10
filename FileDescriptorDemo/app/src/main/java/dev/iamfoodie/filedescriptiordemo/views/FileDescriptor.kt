package dev.iamfoodie.filedescriptiordemo.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.AttributeSet
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dev.iamfoodie.filedescriptiordemo.R
import dev.iamfoodie.filedescriptiordemo.utils.ThumbnailGenerator
import kotlinx.android.synthetic.main.file_descriptor.view.*
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.absoluteValue

const val TAG: String = "FileDescriptor"

class FileDescriptor @JvmOverloads
    constructor(private val ctx: Context, val attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : ConstraintLayout(ctx, attributeSet, defStyleAttr) {

    var fileUri: Uri? = null
        set(value) {
            field = value
            setUpFileDescriptor()
        }

    var file: File? = null

    var infoIsShown: Boolean

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

        infoIsShown = showInfoByDefault

        if (this.infoIsShown) {
            file_info.visibility = View.VISIBLE
        }

        info_file_image.setOnClickListener {
            this.infoIsShown = !this.infoIsShown
            file_info.visibility = if (this.infoIsShown) View.VISIBLE else View.GONE
        }

    }

    fun setUpFileDescriptor() {
        setFile()
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
        file?.let {
            Log.d(TAG,  "${ it.length() } ${ it.exists() }")
            file_name.text = it.name
            file_info.text = """
                File Name: ${ it.name }
                Path to File: ${ it.path }
                Last Modified: ${ it.lastModified().format("MMM dd, YYYY HH:mm") }
                Size: ${ it.length().valueInKb() } KB
        """.trimIndent()
        }
    }

    private fun retrieveFileThumbnail(): Bitmap? {

        var thumbnailBitmap: Bitmap? = null

        try {

            thumbnailBitmap = when(fileType) {
                FileType.IMAGE -> MediaStore.Images.Media.getBitmap(ctx.contentResolver, fileUri)
                FileType.MP4 -> {
                    ThumbnailUtils.createVideoThumbnail(file?.absolutePath, MediaStore.Images.Thumbnails.MINI_KIND)
                }
                else -> null
            }

        } catch (e: Exception) {
            Log.d("MainActivity", "Error occurred: ${ e.message }")
        }

        return thumbnailBitmap
    }

    private fun setFile() {
        val columns = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = ctx.contentResolver.query(
            fileUri!!, columns, null, null, null
        )
        cursor?.let {
            it.moveToFirst()
            val columnIndex = cursor.getColumnIndex(columns[0])
            val filePath = cursor.getString(columnIndex)
            it.close()
            file = File(filePath)
        }

    }

}

enum class FileType {
    IMAGE, TEXT, PDF, DOCX, MP4, MP3, UNKNOWN
}

fun Long.format(format: String): String {
    return SimpleDateFormat(format).format(this.absoluteValue)
}

fun Long.valueInKb(): Double {
    val kb: Double = this.div(1024).toDouble()
    return kb
}