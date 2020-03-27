package dev.iamfoodie.filedescriptiordemo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.provider.MediaStore
import java.io.File
import java.net.URI

class ThumbnailGenerator {

    companion object {

        fun createImageThumbnail(imageUri: Uri, ctx: Context): Bitmap {
            val imageFile = File(imageUri.path)
            return ThumbnailUtils.extractThumbnail(
                BitmapFactory.decodeFile(imageFile.absolutePath),
                128,
                128)
        }

    }

}