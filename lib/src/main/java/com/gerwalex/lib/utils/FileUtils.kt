package me.echodev.resizer.util

import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*

/**
 * Created by K.K. Ho on 3/9/2017.
 */
object FileUtils {

    @JvmStatic
    fun getOutputFilePath(
        compressFormat: CompressFormat, outputDirPath: String,
        outFilename: String,
    ): String {
        val targetFileName: String
        val targetFileExtension = "." + compressFormat
            .name
            .lowercase(Locale.US)
            .replace("jpeg", "jpg")
        targetFileName = outFilename + targetFileExtension
        return outputDirPath + File.separator + targetFileName
    }

    @JvmStatic
    @Throws(IOException::class)
    fun writeBitmapToFile(bitmap: Bitmap, compressFormat: CompressFormat?, quality: Int, file: File) {
        val directory = file.parentFile
        if (directory != null && !directory.exists()) {
            directory.mkdirs()
        }
        var fileOutputStream: FileOutputStream? = null
        try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(compressFormat, quality, fileOutputStream)
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush()
                fileOutputStream.close()
            }
        }
    }
}