package com.github.joechung2008.diagnostics.toolwindows

import com.github.joechung2008.diagnostics.controllers.ExtensionsPanelController
import com.github.joechung2008.diagnostics.ui.ExtensionsToolWindowPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class ExtensionsToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = ExtensionsToolWindowPanel()
        ExtensionsPanelController(panel.envCombo, panel.extCombo, panel.browser, CoroutineScope(Dispatchers.Main))
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, null, false)
        toolWindow.contentManager.addContent(content)
    }
}
