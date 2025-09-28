package com.github.joechung2008.diagnostics.actions

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.wm.ToolWindowManager

class ShowExtensionsAction : AnAction() {
    private val logger = Logger.getInstance(ShowExtensionsAction::class.java)

    /**
     * Choose the thread used for update()/actionPerformed() decisions.
     * UI changes and presentation updates should be run on the EDT.
     */
    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.EDT

    // Enable the action only when a Project is available
    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = e.project != null
    }

    /**
     * Log the invocation and show the Extensions tool window.
     */
    override fun actionPerformed(e: AnActionEvent) {
        logger.info("ShowExtensionsAction invoked")

        val project = e.project!! // Action is enabled only when project is not null
        val toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Extensions")
        toolWindow?.show(null)
    }
}
