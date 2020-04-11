package dev.iamfoodie.filedescriptiordemo.views

import android.content.Context
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
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
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
        retrieveFileThumbnail()
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

    private fun retrieveFileThumbnail() {

        try {

            when(fileType) {
                FileType.IMAGE -> file_preview_image.setImageBitmap(MediaStore.Images.Media.getBitmap(ctx.contentResolver, fileUri))
                FileType.MP4 -> ThumbnailGenerator.createVideoThumbnail(ctx, fileUri, file_preview_image)
                FileType.TEXT -> file_preview_image.setImageResource(R.drawable.txt)
                FileType.PDF -> file_preview_image.setImageResource(R.drawable.pdf)
                FileType.MP3 -> ThumbnailGenerator.createAudioThumbnail(ctx, fileUri!!)
                FileType.DOCX -> file_preview_image.setImageResource(R.drawable.docx)
                FileType.UNKNOWN -> file_preview_image.setImageResource(R.drawable.no_file_selected)
                else -> file_preview_image.setImageResource(R.drawable.no_file_selected)
            }

            file_preview_image.visibility = View.VISIBLE

        } catch (e: Exception) {
            Log.d("MainActivity", "Error occurred: ${ e.message }")
        }
    }

    private fun setFile() {
        val columns = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = ctx.contentResolver.query(
            fileUri!!, columns, null, null, null
        )
        cursor?.let {
            it.moveToFirst()
            val dataColumnIndex = cursor.getColumnIndex(columns[0])
            val filePath = cursor.getString(dataColumnIndex)
            it.close()
            filePath?.let {path ->
                file = File(path)
            }
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