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
import androidx.annotation.RequiresApi
import java.io.File
import java.lang.Exception
import java.net.URI

class ThumbnailGenerator {

    companion object {

        fun createVideoThumbnail(imageUri: Uri?): Bitmap? {

            return ThumbnailUtils.createVideoThumbnail(
                File(imageUri?.path).absolutePath, MediaStore.Video.Thumbnails.MICRO_KIND
            )

        }

    }

}