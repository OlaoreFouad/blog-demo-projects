package dev.iamfoodie.filedescriptiordemo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import android.widget.ImageView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import java.io.File
import java.lang.Exception
import java.net.URI

class ThumbnailGenerator {

    companion object {

        fun createVideoThumbnail(context: Context, imageUri: Uri?, imageView: ImageView): Bitmap? {

            val bitmap: Bitmap? = null

            Glide.with(context)
                .asBitmap()
                .load(imageUri)
                .into(imageView)

            return bitmap

        }

        fun createAudioThumbnail(context: Context, fileUri: Uri): Bitmap? {
            var bitmap: Bitmap? = null

            val mediaMetadataRetriever = MediaMetadataRetriever()
            val rawArt: ByteArray
            val options = BitmapFactory.Options()

            mediaMetadataRetriever.setDataSource(context, fileUri)
            rawArt = mediaMetadataRetriever.embeddedPicture

            if (null != rawArt) {
                bitmap = BitmapFactory.decodeByteArray(rawArt, 0, rawArt.size, options)
            }

            return bitmap
        }

    }

}