package me.echodev.resizer

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.net.Uri
import androidx.annotation.DrawableRes
import me.echodev.resizer.util.FileUtils.getOutputFilePath
import me.echodev.resizer.util.FileUtils.writeBitmapToFile
import me.echodev.resizer.util.ImageUtils.getScaledBitmap
import java.io.*
import java.util.*
import kotlin.math.max
import kotlin.math.min

/**
 * An image resizing library for Android, which allows you to scale an image file to a smaller or bigger one while keeping the aspect ratio.
 * Created by K.K. Ho on 1/9/2017.
 */
class BitmapResizer(private val context: Context) {

    private var compressFormat: CompressFormat
    private var drawableResID = 0
    private var outputDirPath: String
    private var outputFilename: String?
    private var sourceImage: File? = null
    private var targetLength = 1080
    private var quality = 80
    var uri: Uri? = null

    init {
        compressFormat = CompressFormat.JPEG
        outputDirPath = context
            .filesDir
            .absolutePath
        outputFilename = null
    }

    @get:Throws(FileNotFoundException::class)
    private val inputFileStream: InputStream?
        get() {
            var inStream: InputStream? = null
            uri?.let { uri ->
                inStream = context
                    .contentResolver
                    .openInputStream(uri)
            }
            sourceImage?.let {
                inStream = FileInputStream(sourceImage)
            }
            return inStream
        }

    /**
     * Get the resized image bitmap.
     *
     * @return The resized image bitmap.
     * @throws IOException on IO-Error
     */
    @get:Throws(IOException::class)
    val resizedBitmap: Bitmap?
        get() {
            inputFileStream?.let {
                return getScaledBitmap(targetLength, it)
            }

            if (drawableResID != 0) {
                return getScaledBitmap(context, targetLength, drawableResID)
            }
            throw IllegalArgumentException("All sourceFile, uri and DrawableRes are Invalid")
        }

    /**
     * Get the resized image file.
     *
     * @return The resized image file or null
     * @throws IOException on IO-Error
     */
    @get:Throws(IOException::class)
    val resizedFile: File
        get() {
            resizedBitmap?.let {
                val outputFilePath = getOutputFilePath(compressFormat, outputDirPath, outputFilename!!)
                val outFile = File(outputFilePath)
                writeBitmapToFile(it, compressFormat, quality, outFile)
                return outFile
            }
            throw java.lang.IllegalArgumentException("Something get wrong...")
        }

    fun setOutputDir(dir: File): BitmapResizer {
        outputDirPath = dir.absolutePath
        return this
    }

    /**
     * Set the output file name. If you don't set it, the output file will have the same name as the source file.
     *
     * @param filename The name of the output file, without file extension.
     * @return This Resizer instance, for chained settings.
     */
    fun setOutputFilename(filename: String): BitmapResizer {
        check(
            !(filename
                .lowercase(Locale.US)
                .endsWith(".jpg") || filename
                .lowercase(Locale.US)
                .endsWith(".jpeg") || filename
                .lowercase(Locale.US)
                .endsWith(".png") || filename
                .lowercase(Locale.US)
                .endsWith(".webp"))
        ) { "Filename should be provided without extension. See setOutputFormat(String)." }
        outputFilename = filename
        return this
    }

    /**
     * Set the image compression format by Bitmap.CompressFormat.
     *
     * @param compressFormat The compression format. The default format is JPEG.
     * @return This Resizer instance, for chained settings.
     */
    fun setOutputFormat(compressFormat: CompressFormat): BitmapResizer {
        this.compressFormat = compressFormat
        return this
    }

    /**
     * Set the image quality. The higher value, the better image quality but larger file size. PNG, which is a lossless format, will ignore the quality setting.
     *
     * @param quality The image quality value, ranges from 0 to 100. The default value is 80.
     * @return This Resizer instance, for chained settings.
     */
    fun setQuality(quality: Int): BitmapResizer {
        if (quality < 0) {
            this.quality = 0
        } else {
            this.quality = min(quality, 100)
        }
        return this
    }

    /**
     * Set the source image file.
     *
     * @param sourceImage The source image file to be resized.
     * @return This Resizer instance, for chained settings.
     */
    fun setSource(sourceImage: File): BitmapResizer {
        this.sourceImage = sourceImage
        return this
    }

    fun setSource(@DrawableRes resID: Int): BitmapResizer {
        drawableResID = resID
        return this
    }

    fun setSource(uri: Uri): BitmapResizer {
        this.uri = uri
        return this
    }

    /**
     * Set the target length of the image. You only need to specify the target length of the longer side (or either side if it's a square). Resizer will calculate the rest automatically.
     *
     * @param targetLength The target image length in pixel. The default value is 1080.
     * @return This Resizer instance, for chained settings.
     */
    fun setTargetLength(targetLength: Int): BitmapResizer {
        this.targetLength = max(targetLength, 0)
        return this
    }
}