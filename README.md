# WordFreq 📊

**WordFreq** is a modern, feature-rich Android application built with **Jetpack Compose** designed for advanced text analysis, word frequency counting, and linguistic statistics.

---

## ✨ Features

- **Advanced Text Analysis**: Instantly analyze blocks of text to count word frequencies, total word count, unique word count, character count, and average word length.
- **Customizable Tokenization**: Configure how words are parsed (case sensitivity, punctuation handling, number inclusion).
- **Stop Words Filtering**: Filter out common stop words using built-in lists or by defining custom stop words.
- **History Tracking**: Save your analysis history, review past texts, and manage previous results easily.
- **CSV Export**: Export word frequency analysis results to CSV format for external processing or reporting.
- **Modern Material 3 UI**: Clean, intuitive interface supporting both Light and Dark themes with smooth navigation.
- **Haptic Feedback**: Enhanced user interactions with subtle haptic feedback.

---

## 🛠️ Tech Stack & Architecture

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) & [Material 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel) with Unidirectional Data Flow (UDF)
- **Concurrency**: Kotlin Coroutines & Flow for asynchronous data processing
- **Navigation**: Jetpack Navigation Compose
- **Testing**: JUnit 4 unit tests covering tokenization, word frequency analysis, history management, and CSV export.

---

## 📂 Project Structure

```
app/src/main/java/com/example/wordfreq/
│
├── analyzer/         # Core text analysis & stop words logic
├── data/             # Data models, repositories, and preferences (DataStore)
├── export/           # CSV export utilities
├── formatter/        # Text formatting utilities
├── history/          # History management
├── navigation/       # Navigation routes and NavHost
├── tokenizer/        # Word tokenization engine
├── ui/               # Jetpack Compose UI screens (Home, History, Settings, About, Splash)
│   └── theme/        # Material 3 colors, typography, and theme
└── util/             # Helper utilities (e.g., Haptic feedback)
```

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug or newer
- JDK 17 or higher
- Android SDK (API level 24 or higher, target API 34+)

### Building and Running
1. Clone the repository:
   ```bash
   git clone https://github.com/abdullah-ateeq/WordFreq.git
   cd WordFreq
   ```
2. Open the project in Android Studio.
3. Sync Gradle and run the `:app` module on an emulator or physical Android device.

---

## 🧪 Testing

The project includes unit tests for core analysis and tokenizer logic. You can run unit tests via Gradle:

```bash
./gradlew test
```

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
