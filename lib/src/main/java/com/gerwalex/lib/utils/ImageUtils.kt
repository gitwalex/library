package me.echodev.resizer.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.InputStream
import kotlin.math.roundToInt

/**
 * Created by K.K. Ho on 3/9/2017.
 */
object ImageUtils {
    @JvmStatic

    @Throws(FileNotFoundException::class)
    fun getScaledBitmap(targetLength: Int, sourceImage: File): Bitmap? {
        return getScaledBitmap(targetLength, FileInputStream(sourceImage))
    }

    @JvmStatic

    fun getScaledBitmap(targetLength: Int, sourceImage: InputStream): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeStream(sourceImage, null, options)
        bitmap?.let { return getScaledBitmap(targetLength, bitmap, options) }
        return null

    }

    @JvmStatic

    fun getScaledBitmap(context: Context, targetLength: Int, @DrawableRes resID: Int): Bitmap {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeResource(context.resources, resID, options)
        return getScaledBitmap(targetLength, bitmap, options)
    }

    private fun getScaledBitmap(targetLength: Int, bitmap: Bitmap, options: BitmapFactory.Options): Bitmap {
        var size: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bitmap.allocationByteCount
        } else {
            bitmap.byteCount
        }
        Log.d("gerwalex", "Resizer - SourceBitmapSize: $size")
        // Get the dimensions of the original bitmap
        val originalWidth = options.outWidth
        val originalHeight = options.outHeight
        var aspectRatio = originalWidth.toFloat() / originalHeight
        // Calculate the target dimensions
        val targetWidth: Int
        val targetHeight: Int
        if (originalWidth > originalHeight) {
            targetWidth = targetLength
            targetHeight = (targetWidth / aspectRatio).roundToInt()
        } else {
            aspectRatio = 1 / aspectRatio
            targetHeight = targetLength
            targetWidth = (targetHeight / aspectRatio).roundToInt()
        }
        val bmp = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
        size = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bitmap.allocationByteCount
        } else {
            bitmap.byteCount
        }
        Log.d("gerwalex", "Resizer - ResizedBitmapSize: $size")
        return bmp
    }
}