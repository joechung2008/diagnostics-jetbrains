package com.github.joechung2008.diagnostics.ui

import com.github.joechung2008.diagnostics.models.AzureEnvironment
import com.intellij.openapi.ui.ComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JLabel
import javax.swing.DefaultComboBoxModel
import javax.swing.DefaultListCellRenderer
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.BorderFactory
import com.intellij.ui.JBColor
import java.awt.Component
import java.awt.Container
import java.awt.LayoutManager
import com.intellij.ui.jcef.JBCefBrowser

/**
 * Custom layout that wraps Extension unit to next row when needed.
 */
class WrapLayout : LayoutManager {
    override fun addLayoutComponent(name: String?, comp: Component?) {}
    override fun removeLayoutComponent(comp: Component?) {}

    override fun preferredLayoutSize(parent: Container): Dimension {
        val components = parent.components
        if (components.size != 2) return Dimension(0, 0)

        val envPanel = components[0]
        val extPanel = components[1]
        val envSize = envPanel.preferredSize
        val extSize = extPanel.preferredSize
        val parentWidth = parent.width

        val totalWidth = envSize.width + extSize.width + 32 // 16px gap + 16px margins

        return if (parentWidth in 1..<totalWidth) {
            // Will wrap - stack vertically
            val width = maxOf(envSize.width, extSize.width) + 16 // margins
            val height = envSize.height + extSize.height + 24 // 8px gap + 16px margins
            Dimension(width, height)
        } else {
            // Single row
            val width = totalWidth
            val height = maxOf(envSize.height, extSize.height) + 16 // margins
            Dimension(width, height)
        }
    }

    override fun minimumLayoutSize(parent: Container): Dimension {
        val components = parent.components
        if (components.size != 2) return Dimension(0, 0)

        val envPanel = components[0]
        val extPanel = components[1]
        val envSize = envPanel.minimumSize
        val extSize = extPanel.minimumSize

        val width = maxOf(envSize.width, extSize.width)
        val height = envSize.height + extSize.height + 8 // 8px vertical gap
        return Dimension(width, height)
    }

    override fun layoutContainer(parent: Container) {
        val components = parent.components
        if (components.size != 2) return

        val envPanel = components[0]
        val extPanel = components[1]
        val parentSize = parent.size
        val envPrefSize = envPanel.preferredSize
        val extPrefSize = extPanel.preferredSize

        val totalWidth = envPrefSize.width + extPrefSize.width + 16

        if (totalWidth <= parentSize.width) {
            // Both fit on same row
            val margin = 8
            envPanel.setBounds(margin, margin, envPrefSize.width, envPrefSize.height)
            extPanel.setBounds(parentSize.width - extPrefSize.width - margin,
                margin, extPrefSize.width, extPrefSize.height)
        } else {
            // Extension wraps to next row
            val margin = 8
            val gap = 8
            envPanel.setBounds(margin, margin, envPrefSize.width, envPrefSize.height)
            extPanel.setBounds(margin, margin + envPrefSize.height + gap, extPrefSize.width, extPrefSize.height)
        }
    }
}

class ExtensionsToolWindowPanel : JPanel(BorderLayout()) {
    val envCombo: ComboBox<AzureEnvironment>
    val extCombo: ComboBox<String>
    val browser: JBCefBrowser = JBCefBrowser("about:blank")
    val browserComponent: JComponent = browser.component

    init {
        // Use custom layout for proper wrapping behavior
        val selectorsPanel = JPanel(WrapLayout())

        // Environment selector
        envCombo = ComboBox(DefaultComboBoxModel(AzureEnvironment.entries.toTypedArray()))
        envCombo.selectedItem = null
        envCombo.renderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: javax.swing.JList<*>, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean
            ): java.awt.Component {
                val display = if (value == null) "Select..." else (value as? AzureEnvironment)?.name ?: ""
                val comp = super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus)
                if (value == null) {
                    foreground = JBColor.GRAY
                }
                return comp
            }
        }

        // Environment selector label
        val envLabel = JLabel("Environment")
        envLabel.displayedMnemonic = 'E'.code
        envLabel.labelFor = envCombo

        // Extension selector
        extCombo = ComboBox()
        extCombo.selectedItem = null
        extCombo.minimumSize = Dimension(240, extCombo.preferredSize.height)
        extCombo.renderer = object : DefaultListCellRenderer() {
            override fun getListCellRendererComponent(
                list: javax.swing.JList<*>, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean
            ): java.awt.Component {
                val display = value?.toString() ?: "Select..."
                val comp = super.getListCellRendererComponent(list, display, index, isSelected, cellHasFocus)
                if (value == null) {
                    foreground = JBColor.GRAY
                }
                return comp
            }
        }

        // Extension selector label
        val extLabel = JLabel("Extension")
        extLabel.displayedMnemonic = 'X'.code
        extLabel.labelFor = extCombo

        // Panels that keep label and combo box together
        val envPanel = JPanel(BorderLayout(8, 0))
        envPanel.add(envLabel, BorderLayout.WEST)
        envPanel.add(envCombo, BorderLayout.CENTER)

        val extPanel = JPanel(BorderLayout(8, 0))
        extPanel.add(extLabel, BorderLayout.WEST)
        extPanel.add(extCombo, BorderLayout.CENTER)

        selectorsPanel.add(envPanel)
        selectorsPanel.add(extPanel)

        // WebView (JCEF)
        browserComponent.minimumSize = Dimension(200, 200)
        browserComponent.border = BorderFactory.createEmptyBorder(8, 8, 8, 8)

        add(selectorsPanel, BorderLayout.NORTH)
        add(browserComponent, BorderLayout.CENTER)
    }
}
