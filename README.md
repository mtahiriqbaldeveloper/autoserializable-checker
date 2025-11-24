# Autoserializable Checker

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Version](https://img.shields.io/badge/version-1.0.1-blue)
![Platform](https://img.shields.io/badge/platform-IntelliJ%20IDEA-orange)
![Performance](https://img.shields.io/badge/performance-optimized-success)

A high-performance IntelliJ IDEA plugin that helps developers safely modify Java files containing `@AutoSerializable` annotation or implementing `AutoSerializable` interface. Provides multiple checking methods to ensure serialization compatibility.

## Features

- 📋 **Smart Code Inspection** - Automatic warnings during normal IDE analysis (always active)
- 🖱️ **On-Demand Action** - Right-click to check files when needed (zero overhead)
- 🔔 **Optional Real-time Monitoring** - Can enable file change notifications (disabled by default for performance)
- ⚡ **Highly Optimized** - Cached checks, debouncing, and smart pre-filtering
- ⚙️ **Configurable Settings** - Full control over plugin behavior

## Installation

### From JetBrains Marketplace (Coming Soon)
1. Open IntelliJ IDEA
2. Go to `Settings/Preferences` → `Plugins`
3. Search for "Autoserializable Checker"
4. Click `Install` and restart the IDE

### Manual Installation
1. Download the latest release from [Releases](../../releases)
2. Open IntelliJ IDEA
3. Go to `Settings/Preferences` → `Plugins`
4. Click the ⚙️ gear icon → `Install Plugin from Disk...`
5. Select the downloaded `.zip` file
6. Restart IntelliJ IDEA

## Usage

The plugin offers **three ways** to check for AutoSerializable classes:

### 1. Code Inspection (Recommended ✅)
- Runs automatically as part of IntelliJ's code inspection
- Shows warnings directly in the editor
- Always active, no configuration needed
- Minimal performance impact (results are cached)

### 2. Manual Action (On-Demand 🖱️)
1. Open any Java file
2. Right-click → **"Check for @AutoSerializable"**
3. View instant results in notification
- Zero performance impact when not used
- Perfect for code reviews

### 3. Real-time Notifications (Optional 🔔)
- Enable in: `Settings` → `Tools` → `Autoserializable Checker`
- Shows notifications when you modify @AutoSerializable files
- **Disabled by default** for best performance
- Only enable if your workflow requires it

📖 **[See full usage guide →](USAGE.md)**

## Requirements

- IntelliJ IDEA 2023.1 or later (Community or Ultimate)
- Java plugin enabled

## Building from Source

```bash
# Clone the repository
git clone https://github.com/yourusername/autoserializable-checker.git
cd autoserializable-checker

# Build the plugin
./gradlew build

# The plugin ZIP will be in build/distributions/
```

## Development

### Project Structure
```
autoserializable-checker/
├── src/main/java/com/brotech/autoserializablechecker/core/
│   ├── AutoserializableUtil.java            # Centralized cached utility
│   ├── AutoserializableFileListener.java    # Optional real-time monitoring
│   ├── AutoserializableInspection.java      # Code inspection
│   ├── CheckAutoserializableAction.java     # Manual check action
│   ├── AutoserializableSettings.java        # Settings UI
│   ├── AutoserializableSettingsState.java   # Persistent settings
│   └── AutoserializableStartupActivity.java # Plugin initialization
├── src/main/resources/META-INF/
│   ├── plugin.xml
│   └── pluginIcon.svg
├── USAGE.md                                 # User guide
├── PERFORMANCE_IMPROVEMENTS.md              # Technical details
└── build.gradle.kts
```

### Running in Development Mode
```bash
# Run the plugin in a sandboxed IDE instance
./gradlew runIde
```

### Running Tests
```bash
./gradlew test
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

- 🐛 **Bug Reports**: [Issue Tracker](../../issues)
- 💡 **Feature Requests**: [Issue Tracker](../../issues)
- 📧 **Contact**: support@brotech.com

## Changelog

### Version 1.0.1 (Performance Update)
- ⚡ **Major performance improvements** - 10-20x faster
- ⚡ Real-time monitoring now **disabled by default** for zero overhead
- ⚡ Added IntelliJ platform caching for repeated checks
- ⚡ Smart pre-filtering to skip files without autoserializable classes
- ⚡ 1-second debouncing to prevent lag during typing
- ⚡ Centralized cached utility eliminates duplicate code
- 📚 Added comprehensive documentation (USAGE.md, PERFORMANCE_IMPROVEMENTS.md)
- 🎨 Improved settings UI with clear explanations

### Version 1.0.0
- ✨ Initial release
- ✨ Real-time file modification detection
- ✨ Warning notifications for @AutoSerializable changes
- ✨ Code inspection for serialization issues
- ✨ Configurable settings

## Performance

This plugin is designed for **maximum performance**:
- ✅ Zero overhead by default (real-time monitoring opt-in)
- ✅ All checks use IntelliJ's caching system
- ✅ Text-based pre-filtering before expensive operations
- ✅ Debouncing prevents rapid consecutive processing
- ✅ Bounded recursive checks (max depth: 10)

📊 **[See performance details →](PERFORMANCE_IMPROVEMENTS.md)**

---

Made with ❤️ by [brotech](https://brotech.com)

