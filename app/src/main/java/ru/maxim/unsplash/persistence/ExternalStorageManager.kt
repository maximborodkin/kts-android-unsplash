package ru.maxim.unsplash.persistence

import android.content.Context
import android.content.res.Configuration
import android.os.Environment
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import ru.maxim.unsplash.R
import ru.maxim.unsplash.util.toFileName
import ru.maxim.unsplash.util.toast
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

class ExternalStorageManager(private val context: Context) {

    // Using english version of application name for folders
    private val applicationFolderName by lazy {
        val localeContext =
            Configuration(context.resources.configuration).apply { setLocale(Locale.US) }
        context.createConfigurationContext(localeContext).getString(R.string.app_name)
    }

    private val externalImagesFolder =
        context.getExternalFilesDir(Environment.DIRECTORY_DCIM)?.absolutePath +
                File.separator +
                applicationFolderName


    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun saveFile(file: ResponseBody) = withContext(IO) {
        var filename = externalImagesFolder + File.separator + Date().toFileName() + ".jpg"
        var index = 0
        while (File(filename).exists()) {
            filename = """$filename${if (index == 0) "" else "_${index++}"}"""
        }
        File(filename).createNewFile()
        Timber.tag("File_DOWNLOAD").d("Start downloading file to $filename")

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
                withContext(Main) { context.toast(R.string.downloaded) }
                Timber.tag("File_DOWNLOAD").d("File saved to $filename")
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