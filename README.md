# 📱 AskStack - Modern Android StackOverflow Search App + AI Assistant(Interact with StackOverflow using OpenAI Assistant )

## Android RAG Assistant (OpenAI)
See the full guide here:
- [Android RAG Assistant (OpenAI Assistants API + Jetpack Compose)](app/README.md)

<div align="center">

![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-orange.svg)
![Architecture](https://img.shields.io/badge/Architecture-MVVM+MVI-purple.svg)
![Tests](https://img.shields.io/badge/Tests-27%20Passing-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-yellow.svg)

<i>A modern Android sample app demonstrating MVVM + MVI with Clean Architecture, built with Jetpack Compose, Hilt, Coroutines, and Retrofit.</i>

</div>

## 🚀 Overview

AskStack lets you search StackOverflow questions with a clean, reactive UI. It showcases production-grade architecture, modern Android tools, and comprehensive testing.

- Clean Architecture (presentation, domain, data)
- MVVM + MVI (unidirectional state flow)
- Reactive UI with Jetpack Compose
- Robust error handling, retry, and network state awareness

## ✨ Features

- 🔎 Debounced search (300ms) with min 3-character validation
- 📄 Question list with score, answers, tags, and link
- 🧾 Bottom sheet for detailed question content (HTML rendering)
- 🌐 Live connectivity banner with transient “Connected” message
- ⏳ Loading, ❌ error, and ∅ empty-results states with retry

## 🏗️ Architecture

```
Presentation (Compose, ViewModel, MVI States/Intents)
        ↓
Domain (UseCases, Repository interfaces, Models)
        ↓
Data (Repository impl, Retrofit API, DTOs, Mappers, Error handling)
```

- MVI in `HomeViewModel` via `ViewState` and UI intents
- Use case: `SearchQuestionUseCase`
- Repository: `QuestionRepository` + `QuestionRepositoryImpl`
- Networking: `ApiService` (Retrofit), `RequestWrapper` for error mapping
- DI: Hilt modules in `application/di` and `features/.../di`

## 🛠️ Tech Stack

- Kotlin 2.2.0
- Jetpack Compose + Material 3
- Coroutines + Flow
- Dagger Hilt 2.57
- Retrofit + OkHttp (logging)
- Gson converter (DTO mapping to domain)
- Navigation Compose
- Gradle Kotlin DSL, AGP 8.11.1
- Min/Target SDK: 27 / 36

## 📦 Project Structure

```
app/src/main/java/com/thesubgraph/askstack/
├─ application/
│  ├─ AskStackApplication.kt
│  └─ di/                # App & Retrofit modules
├─ base/
│  ├─ components/        # UI building blocks: banners, placeholders, etc.
│  ├─ theme/             # Colors, typography, theme
│  └─ utils/             # Date utils, modifiers, network core
└─ features/search/
   ├─ data/              # Remote ApiService, repository impl, DTOs
   ├─ domain/            # Models, repository interface, use cases
   ├─ view/              # Compose screens & components
   └─ viewmodel/         # HomeViewModel (MVI states)
```

## 🔌 API

- StackExchange API v2.3
- Endpoint: `GET /2.3/search/advanced?q=...&site=stackoverflow&order=desc&filter=withbody`
- See `features/stackoverflow/data/remote/ApiService.kt`

## 🧪 Testing

- Total tests: 27 (debug unit tests)
- Distribution:
  - `QuestionRepositoryImplTest`: repository behavior, error types, flow behavior, edge cases
  - `SearchQuestionUseCaseTest`: delegation + results
  - `HomeViewModelTest`: debouncing, distinctUntilChanged, state transitions, retry
  - `ExampleUnitTest`: baseline sanity

Run tests:

```bash
./gradlew test
# or debug unit tests only
./gradlew testDebugUnitTest
```

## ▶️ Getting Started

Prerequisites:

- Android Studio Flamingo (or newer)
- JDK 11+
- Android SDK 27+

Build & run:

```bash
git clone https://github.com/yeshwanthmunisifreddy/askstack.git
cd askstack
./gradlew build
./gradlew installDebug
```

## 🔧 Notable Implementations

- `RequestWrapper`: Centralized API call + error mapping to domain `ErrorModel`
- `ViewState`: `Initial`, `Loading`, `Loaded`, `Error(ErrorModel)`
- Search UX: `_searchQuery.debounce(300).distinctUntilChanged()`

## 🗺️ Roadmap

- Paging 3 for results
- Local caching (Room) + Offline mode
- Advanced search filters (tags/date/score)
- Favorites
- Dark theme & accessibility improvements
- CI with GitHub Actions

## 🤝 Contributing

1. Fork and create a feature branch
2. Add tests for changes
3. Ensure tests pass: `./gradlew test`
4. Open a Pull Request

## 📄 License

MIT License. See `LICENSE`.

## 🙏 Acknowledgments

- StackExchange API
- Android Developers & Jetpack Compose team
- Kotlin & Dagger teams


