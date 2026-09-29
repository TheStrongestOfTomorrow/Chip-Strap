package com.chevstrap.rbx.ui.views.settings.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chevstrap.rbx.ui.viewModels.MethodAction
import com.chevstrap.rbx.ui.viewModels.settings.pages.FFlagsViewModel

class FFlagsFragment : PageFragment() {

    private val viewModel = FFlagsViewModel()
    private var isShizukuEnabled = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = super.onCreateView(inflater, container, savedInstanceState)
        populateUI()
        return root
    }

    private fun populateUI() {
        viewModel.loadFlags()
        val parent = parentLayout ?: return
        parent.removeAllViews()

        addSection(parent, "FastFlags (FFlags) Editor")

        val shizukuStatus = if (viewModel.isShizukuAvailable()) "Shizuku Mode: Active" else "Shizuku Mode: Standby / System"
        addToggle(
            "Use Shizuku / Direct Sync",
            shizukuStatus,
            isShizukuEnabled,
            { enabled ->
                isShizukuEnabled = enabled
                viewModel.setUseShizuku(enabled)
            },
            1
        )

        var keyInput = ""
        var valueInput = ""

        addTextbox(
            "Flag Name",
            "e.g. FFlagDebugGraphicsDisableDirect3D11",
            "",
            { value -> keyInput = value },
            1
        )

        addTextbox(
            "Flag Value",
            "e.g. True / False / 60",
            "",
            { value -> valueInput = value },
            1
        )

        addButton(
            "Add / Update FastFlag",
            "Save or update key-value pair in ClientAppSettings.json",
            MethodAction {
                if (keyInput.isNotBlank()) {
                    viewModel.setFlag(keyInput, valueInput)
                    viewModel.saveFlags()
                    populateUI()
                }
            },
            1
        )

        addSection(parent, "Active Flags")

        val flags = viewModel.getFlags()
        if (flags.isEmpty()) {
            addButton(
                "No FastFlags set",
                "Add a flag using the inputs above",
                MethodAction { },
                1
            )
        } else {
            for ((key, value) in flags) {
                addButton(
                    key,
                    "Value: $value (Tap to remove)",
                    MethodAction {
                        viewModel.removeFlag(key)
                        viewModel.saveFlags()
                        populateUI()
                    },
                    1
                )
            }
        }
    }
}
