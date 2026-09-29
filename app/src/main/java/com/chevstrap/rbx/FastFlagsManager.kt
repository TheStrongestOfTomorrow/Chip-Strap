package com.chevstrap.rbx

import chevstrap.extensions.FileTool
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import java.io.File

class FastFlagsManager private constructor() {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val flagsMap = mutableMapOf<String, String>()
    private val originalFlagsMap = mutableMapOf<String, String>()

    var useShizukuExport: Boolean = false

    val fileLocation: File
        get() {
            val dir = File(Paths.localAppData, "ClientSettings")
            if (!dir.exists()) {
                dir.mkdirs()
            }
            return File(dir, "ClientAppSettings.json")
        }

    val robloxShizukuPath: String
        get() = "/data/data/com.roblox.client/files/ClientSettings/ClientAppSettings.json"

    fun load() {
        flagsMap.clear()
        originalFlagsMap.clear()

        val file = fileLocation
        if (!file.exists()) {
            return
        }

        try {
            val content = FileTool.safeRead(file)
            if (content.isBlank()) return

            val parsed = json.parseToJsonElement(content).jsonObject
            for ((key, value) in parsed) {
                val strVal = when {
                    value is JsonPrimitive -> value.content
                    else -> value.toString()
                }
                flagsMap[key] = strVal
                originalFlagsMap[key] = strVal
            }
        } catch (e: Exception) {
            App.logger.writeLine("FastFlagsManager::load", "Error loading fast flags: ${e.message}")
        }
    }

    fun save() {
        val file = fileLocation
        try {
            val mapToSave = mutableMapOf<String, JsonElement>()
            for ((k, v) in flagsMap) {
                val jsonElem = when {
                    v.equals("true", ignoreCase = true) -> JsonPrimitive(true)
                    v.equals("false", ignoreCase = true) -> JsonPrimitive(false)
                    v.toLongOrNull() != null -> JsonPrimitive(v.toLong())
                    v.toDoubleOrNull() != null -> JsonPrimitive(v.toDouble())
                    else -> JsonPrimitive(v)
                }
                mapToSave[k] = jsonElem
            }
            val jsonObject = JsonObject(mapToSave)
            val jsonString = json.encodeToString(JsonObject.serializer(), jsonObject)
            FileTool.safeWrite(file, jsonString)

            if (useShizukuExport) {
                ShizukuHelper.writeTextFile(robloxShizukuPath, jsonString)
            }

            originalFlagsMap.clear()
            originalFlagsMap.putAll(flagsMap)
        } catch (e: Exception) {
            App.logger.writeLine("FastFlagsManager::save", "Error saving fast flags: ${e.message}")
        }
    }

    fun getFlags(): Map<String, String> = flagsMap.toMap()

    fun setFlag(name: String, value: String) {
        if (name.isBlank()) return
        flagsMap[name.trim()] = value.trim()
    }

    fun removeFlag(name: String) {
        flagsMap.remove(name)
    }

    fun clearAll() {
        flagsMap.clear()
    }

    fun hasUnsavedChanges(): Boolean {
        return flagsMap != originalFlagsMap
    }

    companion object {
        @Volatile
        private var instanceDelegate: FastFlagsManager? = null

        @JvmStatic
        val instance: FastFlagsManager
            get() {
                return instanceDelegate ?: synchronized(this) {
                    instanceDelegate ?: FastFlagsManager().also {
                        instanceDelegate = it
                    }
                }
            }
    }
}
