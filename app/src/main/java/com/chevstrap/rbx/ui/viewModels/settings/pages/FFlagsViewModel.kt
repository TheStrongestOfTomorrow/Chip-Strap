package com.chevstrap.rbx.ui.viewModels.settings.pages

import com.chevstrap.rbx.FastFlagsManager
import com.chevstrap.rbx.ShizukuHelper
import com.chevstrap.rbx.ui.viewModels.GlobalViewModel

class FFlagsViewModel : GlobalViewModel() {

    private val fastFlagsManager = FastFlagsManager.instance

    fun loadFlags() {
        fastFlagsManager.load()
    }

    fun getFlags(): Map<String, String> {
        return fastFlagsManager.getFlags()
    }

    fun setFlag(key: String, value: String) {
        fastFlagsManager.setFlag(key, value)
    }

    fun removeFlag(key: String) {
        fastFlagsManager.removeFlag(key)
    }

    fun saveFlags() {
        fastFlagsManager.save()
    }

    fun setUseShizuku(enabled: Boolean) {
        fastFlagsManager.useShizukuExport = enabled
    }

    fun isShizukuAvailable(): Boolean {
        return ShizukuHelper.isShizukuAvailable()
    }

    fun hasUnsavedChanges(): Boolean {
        return fastFlagsManager.hasUnsavedChanges()
    }
}
