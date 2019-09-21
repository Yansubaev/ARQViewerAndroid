package su.arq.arqviewer.utils

import android.util.Log

import java.io.Closeable
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

/**
 * @author Daniel Serdyukov
 */
object IOUtils {

    const val EOF = -1

    const val BUFFER_SIZE = 64 * 1024

    fun closeQuietly(closeable: Closeable) {
        try {
            closeable.close()
        } catch (e: IOException) {
            Log.wtf(IOUtils::class.java.simpleName, e)
        }

    }

    @Throws(IOException::class)
    fun toString(`is`: InputStream): String {
        val result = StringBuilder()
        val reader = InputStreamReader(`is`, Charset.defaultCharset())
        val buffer = CharArray(BUFFER_SIZE)
        try {
            val bytes: Int = reader.read(buffer)
            while (bytes != EOF) {
                result.append(buffer, 0, bytes)
            }
        } finally {
            closeQuietly(reader)
        }
        return result.toString()
    }

    fun toStringQuietly(`is`: InputStream): String {
        return try {
            toString(`is`)
        } catch (e: IOException) {
            ""
        }
    }

}