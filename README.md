# Autoserializable Checker

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![Version](https://img.shields.io/badge/version-1.0.0-blue)
![Platform](https://img.shields.io/badge/platform-IntelliJ%20IDEA-orange)

An IntelliJ IDEA plugin that monitors changes to Java files containing `@Autoserializable` annotation or implementing `Autoserializable` interface. It displays warnings when such files are modified to ensure serialization compatibility.

## Features

- 🔍 **Real-time File Monitoring** - Automatically detects modifications to Autoserializable classes
- ⚠️ **Warning Notifications** - Displays balloon notifications when Autoserializable files are changed
- 🛡️ **Code Inspection** - Built-in inspection to identify potential serialization issues
- ⚙️ **Configurable Settings** - Customize plugin behavior through IntelliJ settings

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

Once installed, the plugin automatically monitors your Java files:

1. **Automatic Detection**: When you modify a file with `@Autoserializable` or implementing `Autoserializable`, you'll receive a notification
2. **Code Inspection**: The plugin adds a "Serialization" inspection that highlights potential issues
3. **Settings**: Configure the plugin behavior in `Settings` → `Tools` → `Serialization Checker`

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
├── src/main/
│   ├── kotlin/com/brotech/autoserializablechecker/core/
│   │   ├── AutoserializableFileListener.java
│   │   ├── AutoserializableInspection.java
│   │   ├── AutoserializableSettings.java
│   │   └── AutoserializableStartupActivity.java
│   └── resources/META-INF/
│       ├── plugin.xml
│       └── pluginIcon.svg
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

### Version 1.0.0
- ✨ Initial release
- ✨ Real-time file modification detection
- ✨ Warning notifications for @Autoserializable changes
- ✨ Code inspection for serialization issues
- ✨ Configurable settings

---

Made with ❤️ by [brotech](https://brotech.com)

