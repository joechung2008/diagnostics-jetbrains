package com.github.joechung2008.diagnostics.controllers

import com.github.joechung2008.diagnostics.models.AzureEnvironment
import com.github.joechung2008.diagnostics.models.ExtensionBase
import com.github.joechung2008.diagnostics.services.DiagnosticsFetcher
import com.github.joechung2008.diagnostics.services.HtmlRendererService
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.ColorUtil
import com.intellij.ui.jcef.JBCefBrowser
import com.intellij.util.ui.UIUtil
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.UIManager

class ExtensionsPanelController(
    private val envCombo: ComboBox<AzureEnvironment>,
    private val extCombo: ComboBox<String>,
    private val browser: JBCefBrowser,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Main)
) {
    // Store extensions as a private field for lookup
    private var extensionsMap: Map<String, ExtensionBase> = emptyMap()

    val browserComponent: JComponent = browser.component

    private fun toCss(color: java.awt.Color): String = "#" + ColorUtil.toHex(color)

    private fun getThemeColors(): Map<String, String> {
        val text = UIUtil.getLabelForeground()
        val muted = UIUtil.getLabelDisabledForeground()
        val accent = UIManager.getColor("Link.activeForeground")
            ?: UIManager.getColor("Link.foreground")
            ?: text
        val border = UIManager.getColor("Component.borderColor")
            ?: UIManager.getColor("Separator.foreground")
            ?: UIManager.getColor("Borders.color")
            ?: text
        val surface = UIUtil.getPanelBackground()

        return mapOf(
            "vs-text" to toCss(text),
            "vs-text-strong" to toCss(text), // No JetBrains IDE equivalent
            "vs-text-muted" to toCss(muted),
            "vs-text-disabled" to toCss(muted), // No JetBrains IDE equivalent
            "vs-accent" to toCss(accent),
            "vs-border" to toCss(border),
            "vs-surface" to toCss(surface)
        )
    }

    init {
        envCombo.addActionListener {
            renderExtension(null)
            val selected = envCombo.selectedItem as? AzureEnvironment
            if (selected != null) {
                fetchAndPopulateExtensions(selected)
            } else {
                extCombo.removeAllItems()
            }
        }
        extCombo.addActionListener {
            val selected = extCombo.selectedItem as? String
            renderExtension(extensionsMap[selected])
        }
        // Re-render the currently selected extension when the IDE theme (Look & Feel) changes
        UIManager.addPropertyChangeListener { evt ->
            if (evt.propertyName == "lookAndFeel") {
                val selected = extCombo.selectedItem as? String
                renderExtension(extensionsMap[selected])
            }
        }
    }

    private fun fetchAndPopulateExtensions(environment: AzureEnvironment) {
        scope.launch {
            extCombo.removeAllItems()
            try {
                val diagnostics = DiagnosticsFetcher.fetchDiagnostics(environment)
                extensionsMap = diagnostics.extensions
                val extensions = extensionsMap.keys.sorted()
                extCombo.model = DefaultComboBoxModel(extensions.toTypedArray())
                extCombo.selectedItem = null
            } catch (_: Exception) {
                extensionsMap = emptyMap()
                extCombo.removeAllItems()
            }
        }
    }

    private fun renderExtension(extension: ExtensionBase?) {
        val html = if (extension != null) {
            HtmlRendererService.renderFullHtml(extension, getThemeColors())
        } else {
            ""
        }

        try {
            browser.loadHTML(html)
            browserComponent.revalidate()
            browserComponent.repaint()
        } catch (e: NoClassDefFoundError) {
            javax.swing.JOptionPane.showMessageDialog(
                browserComponent,
                "JCEF browser is unavailable. Cannot display extension content.",
                "Browser Unavailable",
                javax.swing.JOptionPane.WARNING_MESSAGE
            )
        }
    }
}
