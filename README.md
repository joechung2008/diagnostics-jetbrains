# Diagnostics JetBrains Plugin

Azure Portal Extensions Dashboard implemented in Kotlin as a JetBrains IDE plugin.

## Developer Workflow

### Prerequisites

- **JDK 21** (required for IntelliJ Platform plugins)
- **[Gradle](https://gradle.org/)** (use the included Gradle wrapper: `./gradlew`)
- **IntelliJ IDEA** (recommended for plugin development)

### Building the Project

Run the following command from the project root:

```bash
./gradlew build
```

This will compile the plugin and run all checks. The built plugin ZIP will be in `build/distributions/`.

### Running the Plugin in a Sandbox IDE

To launch a development instance of IntelliJ IDEA with the plugin installed:

```bash
./gradlew runIde
```

### Testing

This project is set up for IntelliJ Platform plugin testing, but currently has **no test sources**. To add tests:

1. Create test files under `src/test/kotlin` (create the directory if it does not exist).
2. Use the IntelliJ Platform test framework (e.g., `LightPlatformTestCase`).
3. Run tests with:

```bash
./gradlew test
```

### Project Structure

- `build.gradle.kts` — Gradle build script (uses IntelliJ Platform Gradle Plugin)
- `settings.gradle.kts` — Project settings
- `gradle.properties` — Build and cache configuration
- `src/main/kotlin` — Plugin source code
- `src/main/resources` — Plugin resources (plugin.xml, icons, etc.)
- `src/test/kotlin` — (optional) Test sources

### Useful Resources

- [IntelliJ Platform Plugin Development Guide](https://plugins.jetbrains.com/docs/intellij/)
- [Gradle IntelliJ Plugin](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html)

---

For more details on project structure and best practices, see comments in the source and `build.gradle.kts`.

3. Resources (src/main/resources)

- META-INF/plugin.xml — Register extensions/actions. Keep it organized:
  - group actions under <actions> with clear IDs and descriptions
  - keep extension points grouped by feature
- META-INF/pluginIcon.svg — 40x40 SVG for Plugins UI
- messages/Bundle.properties — Externalize all user-visible strings and use a Messages bundle (recommended for localization)
- icons/ — Per-feature icons; name consistently (e.g., diagnostics/diagnostics.svg)

4. Build configuration

- Target a stable IntelliJ Platform version in build.gradle.kts (done: IC 2025.1).
- Set ideaVersion.sinceBuild to the correct baseline (done: 251). Add untilBuild only if necessary.
- Keep dependencies minimal. Add bundledPlugin("com.intellij.java") only when you use Java PSI/inspections, etc.
- Consider adding the IntelliJ Plugin Verifier Gradle task for CI before publishing.

5. Testing

- Place tests under src/test/kotlin and use the IntelliJ Platform test framework already configured by the plugin:
  - LightPlatformTestCase / BasePlatformTestCase for editor/PSI-related tests
  - Useful for verifying actions, file type behaviors, and inspections
- For logic that doesn’t need the IDE, extract it into :core and test with plain JUnit.

6. Coding conventions & quality

- Kotlin: target JVM 21 (already configured). Enable explicit API mode in Kotlin if you publish a library module.
- Add .editorconfig for consistent formatting (Kotlin & XML). Optionally add ktlint or detekt.
- Keep plugin.xml lean: move strings to Bundle, keep IDs stable, add change notes.

7. Run, debug, package

- Run the IDE with the plugin: gradlew runIde
- Build the plugin zip: gradlew buildPlugin
- Install in an IDE: use the built ZIP from build/distributions

8. When to split modules

- Split when you have logic that you want to unit-test without the IDE or share externally.
- Common layout:
  - :core — pure Kotlin
  - :plugin — depends on :core and on IntelliJ Platform

9. Naming and IDs

- Keep action IDs stable (e.g., Diagnostics.Show). Changing IDs breaks keymaps and references.
- Use Title Case for action/menu texts and sentence case for descriptions.

10. Next steps for this repo

- Create packages: actions (move ShowDiagnosticsAction), diagnostics (feature code as it grows), util (shared helpers) — only when needed.
- Add a messages/Bundle.properties and use a bundle in actions to externalize strings.
- Add a basic test under src/test/kotlin once you introduce behavior that benefits from testing.
- If growth continues, extract :core module for non-IDE logic.

References

- IntelliJ Platform Plugin Development: https://plugins.jetbrains.com/docs/intellij/
- Gradle IntelliJ Plugin: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
- Testing Plugins: https://plugins.jetbrains.com/docs/intellij/testing-plugins.html
