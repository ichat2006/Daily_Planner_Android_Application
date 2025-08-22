# Daily Planner — Android

A Kotlin Android app that helps you plan your day and stay focused. It’s built as a single-activity, multi-fragment app with Bottom Navigation and the Jetpack Navigation Component. Core modules include **Tasks** (with priorities and dates), **Social** (lightweight contacts), and a **Productivity** hub (timer, music player, quick math, word scramble).

> Tech highlights: Kotlin · Jetpack Navigation · ViewBinding · RecyclerView · Material · SQLite · MediaPlayer · (optional) custom tree indices for fast task ordering.

---

## ✨ Features

- **Tasks**
  - Add, edit, delete tasks with **priority**, **category**, and **planned/due** date & time.
  - Local storage with SQLite.
  - Fast, ordered views using in-memory indices (e.g., AVL/BST style structures)
- **Social (Contacts)**
  - Save name, phone, email, notes; filter by category.
  - Stored locally with SQLite.
- **Productivity**
  - **Timer** with start/pause/resume (built on `CountDownTimer`).
  - **Music Player** using `MediaPlayer` with bundled tracks in `res/raw`.
  - **Quick Math** & **Word Scramble** mini-games.
- **Home/Dashboard**
  - Surfaces urgent and high-priority tasks so you know what matters now.
- **Material Design**
  - Clean UI with Material components, dialogs, and empty states.

---

## 🧰 Tech Stack & Skills

- **Language:** Kotlin  
- **Android SDK:** compile **35**, target **35**, min **26**  
- **Architecture:** Single-Activity + **Jetpack Navigation**, modular **Fragments**  
- **UI:** **ViewBinding**, **RecyclerView**, TabLayout, Material Components  
- **Data layer:** SQLite via `SQLiteOpenHelper` (Room-ready path)  
- **Media & Timing:** `MediaPlayer`, `SeekBar` + `Handler`, `CountDownTimer`  
- **Algorithms:** In-app data structures (AVL/BST-style) for ordering/search  
- **Testing:** JUnit & AndroidX test scaffolding

---

## 🚀 Getting Started

### Prerequisites
- **Android Studio** (Hedgehog/Koala or newer)
- **JDK 17** (bundled with recent Android Studio)
- Android SDK with **API 35**

### Build & Run (Android Studio)
1. **Open** the project folder (repo root).
2. Let **Gradle sync** finish.
3. Select a device/emulator (Android **26+**).
4. Press **Run ▶**.

### Build from CLI
```bash
# from the project root (where gradlew lives)
./gradlew clean assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
