package com.chevstrap.rbx

import android.os.Build
import java.io.File
import java.io.OutputStreamWriter

object ShizukuHelper {

    fun isShizukuAvailable(): Boolean {
        return try {
            val process = Runtime.getRuntime().exec("shizuku -v")
            process.waitFor() == 0
        } catch (_: Exception) {
            false
        }
    }

    fun writeTextFile(targetPath: String, content: String): Boolean {
        return try {
            val file = File(targetPath)
            file.parentFile?.mkdirs()
            file.writeText(content)
            true
        } catch (_: Exception) {
            try {
                val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "cat > '$targetPath'"))
                OutputStreamWriter(process.outputStream).use { writer ->
                    writer.write(content)
                }
                process.waitFor() == 0
            } catch (_: Exception) {
                false
            }
        }
    }
}
