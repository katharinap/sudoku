# Sudoku Android App

A feature-rich, high-performance Sudoku application built with modern Android development practices, Clean Architecture, and Jetpack Compose.

## 🎮 Features

- **Puzzles for Everyone**: Four difficulty levels: Easy, Medium, Hard, and Expert.
- **Smart Highlighting**: Automatically highlights cells with the same value and cells that contain the selected value in their notes.
- **Advanced Tools**:
    - **Undo/Redo**: Infinite history for the current session.
    - **Note Mode**: Toggle to draft possibilities in cells.
    - **Hint System**: Intelligent logic to help you find the next logical move.
    - **Board Check**: Instantly visualize conflicts (duplicate numbers) on the grid.
    - **Reset**: Clear all your moves and start the current puzzle fresh.
- **Seamless Experience**:
    - **State Persistence**: Automatically saves your progress; resumes exactly where you left off, even after process death.
    - **Statistics**: Track your best times and win rates for each difficulty.
    - **Personalization**: Customize themes (Dark/Light mode), sound, and haptics in Settings.
- **Accessibility**: Full support for TalkBack with dynamic cell descriptions (value, notes, and coordinates).
- **Polished UI**: Material 3 design, custom adaptive icon, and a smooth splash screen.

## 🛠️ Tech Stack

- **UI**: Jetpack Compose with Material 3.
- **Architecture**: Clean Architecture (Domain, Data, Presentation).
- **Navigation**: Jetpack Navigation Compose with type-safe routes.
- **Dependency Injection**: Hilt.
- **Persistence**: 
    - **Room**: Stores game state and player statistics.
    - **DataStore**: Manages user preferences and settings.
- **Asynchronous Flow**: Kotlin Coroutines & Flow.
- **Serialization**: Kotlinx Serialization for complex data structures.

## 🏗️ Project Structure

The project follows a modular Clean Architecture pattern:

```text
app/src/main/java/com/katharina/sudoku/
├── data/           # Data Layer: Room DAOs, Repositories, DataSources, and Mappers
├── di/             # Dependency Injection modules (Hilt)
├── domain/         # Domain Layer: Use Cases, Repository interfaces, and Business Logic
│   └── model/      # Core data models (Board, Cell, Position)
├── presentation/   # Presentation Layer: ViewModels, Screens, and Components
│   ├── game/       # Sudoku Grid, Controls, and Game Logic
│   ├── menu/       # Main Menu and Difficulty Selection
│   ├── settings/   # User Preferences
│   └── stats/      # Historical Data View
└── ui/             # App Theme and styling
```

## 🧪 Testing

The app is extensively tested to ensure logic integrity and UI stability:
- **Unit Tests**: Domain logic (Sudoku generation, validation), Data mapping, and ViewModels.
- **Instrumented Tests**: UI components and end-to-end screen interactions using the Compose Test Library and Robolectric.

---

Built with ❤️ using the latest Android standards.
