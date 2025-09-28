Diagnostics JetBrains — Project Structure Recommendations

This repository is a minimal IntelliJ Platform plugin. Below is a pragmatic structure you can follow as the plugin grows. It favors clarity, testability, and ease of navigation in IntelliJ IDEA.

1) Top-level layout
- build.gradle.kts — Gradle build (IntelliJ Platform Gradle Plugin)
- settings.gradle.kts — Project name and included modules (single-module for now)
- gradle.properties — Build cache, configuration cache, Kotlin stdlib flag
- gradle/ wrapper — Gradle wrapper files
- src/
  - main/
    - kotlin/ — Production Kotlin sources
    - resources/ — Resources (plugin.xml, icons, messages, etc.)
  - test/
    - kotlin/ — Tests using the IntelliJ Platform test framework

As the project grows, consider multi-module:
- :plugin — IntelliJ-specific code (actions, services, UI)
- :core — Pure Kotlin logic with no IntelliJ dependencies (unit-test friendly)
This separation keeps platform-independent logic easy to test and reuse.

2) Package structure (src/main/kotlin)
Use feature-oriented packages with a small set of conventional folders. Example:
- com.github.joechung2008.diagnostics
  - actions/ — Action classes (e.g., ShowDiagnosticsAction)
  - services/ — Application/Project-level services
  - listeners/ — Message bus or VFS/Project listeners
  - startup/ — StartupActivity implementations
  - toolwindow/ — ToolWindow factories and UI controllers
  - inspections/ — Local/Global inspections
  - intentions/ — Intentions, quick-fixes
  - ui/ — Swing components, dialogs, models (keep thin; prefer controllers under features)
  - util/ — Shared helpers that don’t fit in features
  - diagnostics/ — Your core feature code (create more feature packages as needed)

Guidelines:
- Prefer feature packages (diagnostics, analysis, reporting) over technical-only packages.
- Keep UI code thin; put behavior in controllers/services to simplify testing.
- Avoid static singletons; use services/DI provided by the platform when suitable.

3) Resources (src/main/resources)
- META-INF/plugin.xml — Register extensions/actions. Keep it organized:
  - group actions under <actions> with clear IDs and descriptions
  - keep extension points grouped by feature
- META-INF/pluginIcon.svg — 40x40 SVG for Plugins UI
- messages/Bundle.properties — Externalize all user-visible strings and use a Messages bundle (recommended for localization)
- icons/ — Per-feature icons; name consistently (e.g., diagnostics/diagnostics.svg)

4) Build configuration
- Target a stable IntelliJ Platform version in build.gradle.kts (done: IC 2025.1).
- Set ideaVersion.sinceBuild to the correct baseline (done: 251). Add untilBuild only if necessary.
- Keep dependencies minimal. Add bundledPlugin("com.intellij.java") only when you use Java PSI/inspections, etc.
- Consider adding the IntelliJ Plugin Verifier Gradle task for CI before publishing.

5) Testing
- Place tests under src/test/kotlin and use the IntelliJ Platform test framework already configured by the plugin:
  - LightPlatformTestCase / BasePlatformTestCase for editor/PSI-related tests
  - Useful for verifying actions, file type behaviors, and inspections
- For logic that doesn’t need the IDE, extract it into :core and test with plain JUnit.

6) Coding conventions & quality
- Kotlin: target JVM 21 (already configured). Enable explicit API mode in Kotlin if you publish a library module.
- Add .editorconfig for consistent formatting (Kotlin & XML). Optionally add ktlint or detekt.
- Keep plugin.xml lean: move strings to Bundle, keep IDs stable, add change notes.

7) Run, debug, package
- Run the IDE with the plugin: gradlew runIde
- Build the plugin zip: gradlew buildPlugin
- Install in an IDE: use the built ZIP from build/distributions

8) When to split modules
- Split when you have logic that you want to unit-test without the IDE or share externally.
- Common layout:
  - :core — pure Kotlin
  - :plugin — depends on :core and on IntelliJ Platform

9) Naming and IDs
- Keep action IDs stable (e.g., Diagnostics.Show). Changing IDs breaks keymaps and references.
- Use Title Case for action/menu texts and sentence case for descriptions.

10) Next steps for this repo
- Create packages: actions (move ShowDiagnosticsAction), diagnostics (feature code as it grows), util (shared helpers) — only when needed.
- Add a messages/Bundle.properties and use a bundle in actions to externalize strings.
- Add a basic test under src/test/kotlin once you introduce behavior that benefits from testing.
- If growth continues, extract :core module for non-IDE logic.

References
- IntelliJ Platform Plugin Development: https://plugins.jetbrains.com/docs/intellij/
- Gradle IntelliJ Plugin: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
- Testing Plugins: https://plugins.jetbrains.com/docs/intellij/testing-plugins.html
