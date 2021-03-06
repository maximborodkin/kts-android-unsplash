package ru.maxim.unsplash.persistence

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import ru.maxim.unsplash.R
import ru.maxim.unsplash.util.longToast
import ru.maxim.unsplash.util.toFileName
import ru.maxim.unsplash.util.toast
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class ExternalStorageManager(private val context: Context) {

    private val externalImagesFolder =
        context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun saveFile(file: ResponseBody) = withContext(IO) {
        var filename = externalImagesFolder + File.separator + Date().toFileName() + ".jpg"
        var index = 0
        while (File(filename).exists()) {
            filename = """$filename${if (index == 0) "" else "_${index++}"}"""
        }
        File(filename).createNewFile()

        var fileInputStream: InputStream? = null
        var fileOutputStream: OutputStream? = null
        try {
            fileInputStream = file.byteStream()
            fileOutputStream = FileOutputStream(filename)
            fileOutputStream.use {
                val buffer = ByteArray(1024 * 4)
                var size: Int
                while (fileInputStream.read(buffer).also { size = it } != -1) {
                    fileOutputStream.write(buffer, 0, size)
                }
                fileOutputStream.flush()
                withContext(Main) {
                    context.longToast(
                        context.getString(R.string.downloaded_placeholder, externalImagesFolder)
                    )
                }
            }
        } catch (e: Exception) {
            withContext(Main) { context.toast(R.string.download_error) }
            throw e
        } finally {
            fileInputStream?.close()
            fileOutputStream?.close()
        }
    }
}