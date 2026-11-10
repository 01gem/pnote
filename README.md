# PNote 🚀

A modern, minimalist Android notes application engineered for focus, high-contrast readability, and seamless **Extract to CSV** export.

Built with **Jetpack Compose**, **Material 3**, and **Kotlin**, PNote combines an energetic **Black & Orange** visual aesthetic with a **headers-first** browsing experience and **rounded entry cards**.

---

## ✨ Features

### 🏷️ Headers First Before Clicking
- **Prominent Header Hierarchy**: Every note displays its title/header front-and-center in bold typography before opening the note.
- **Visual Accent Indicator**: Each card includes an electric orange vertical accent pill to guide the eye.
- **Card Previews**: Displays creation date, word count, and truncated body snippets in the list view.
- **Focused Editor**: Tapping any card opens a distraction-free note viewer/editor with header inputs positioned first.

### 📊 Dedicated "Extract to CSV" Main Feature
Export your entire note collection with a single tap:
- **Universal RFC 4180 Compliance**: Properly escapes internal double quotes (`""`), commas, and multiline text.
- **UTF-8 BOM Encoded**: Opens cleanly in **Microsoft Excel**, **Google Sheets**, **Apple Numbers**, and **LibreOffice** without character corruption.
- **Flexible Export Options**:
  - **Save to Device**: Uses Android's modern Storage Access Framework (`CreateDocument`) to save `.csv` directly to Downloads, Documents, or Cloud storage.
  - **Share File**: Native Android Share Sheet integration (`Intent.ACTION_SEND` via `FileProvider`) to send notes directly to Google Drive, Gmail, Slack, WhatsApp, etc.
  - **Copy to Clipboard**: Instant copy of raw CSV data for fast pasting.
- **Live CSV Preview**: Inspect note count, word count, and file size before saving.

### 🎨 Modern Black & Orange Design
- **OLED Deep Black**: Pitch black background (`#090A0C`) and dark slate surfaces (`#181920`).
- **Electric Orange Accents**: Vibrant orange (`#FF6D00` / `#FF7A00`) on headers, FAB, buttons, search focus, and status indicators.
- **Rounded Entry Cards**: Smooth `22dp` rounded corners on all note cards with subtle charcoal-orange borders.
- **Edge-to-Edge**: Full support for Android system navigation insets and dark status bars.

### 💾 Local Offline Persistence
- **100% Offline & Private**: Powered by native Android SQLite (`SQLiteOpenHelper`) — no cloud tracking or account requirements.
- **Instant Search**: Real-time filtering across both headers and note contents.
- **Pin Important Notes**: Quick pin toggle to keep priority notes at the top.

---

## 📱 Tech Stack & Architecture

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with [Material 3](https://m3.material.io/)
- **Architecture**: Clean MVVM (Model-View-ViewModel) + Coroutines Flow
- **Database**: Android SQLite (`SQLiteOpenHelper`) with background I/O dispatcher
- **File Sharing**: Android `FileProvider` + Storage Access Framework (SAF)
- **Minimum SDK**: Android 11 (API level 30)
- **Target SDK**: Android 15+ (API level 37)

---

## 🗂️ Project Structure

```
com.gem.pnote
├── data/
│   ├── Note.kt                 # Note data model (Header, Content, Timestamps)
│   ├── NotesDatabaseHelper.kt  # SQLite database implementation & sample seeding
│   ├── NoteRepository.kt      # Coroutine-based CRUD & query operations
│   └── CsvExporter.kt         # RFC 4180 CSV serializer & intent builder
├── ui/
│   ├── components/
│   │   ├── NoteCard.kt         # Rounded 22dp card showing headers first
│   │   ├── NoteEditorScreen.kt # Header-first note editor & viewer
│   │   └── CsvExportDialog.kt  # CSV live preview & export sheet
│   ├── theme/
│   │   ├── Color.kt            # Black & Orange color definitions
│   │   ├── Theme.kt            # PNote dark color scheme
│   │   ├── Type.kt             # Modern typography scale
│   │   └── PNoteIcons.kt       # Built-in lightweight vector icons
│   ├── NotesViewModel.kt       # StateFlow UI state holder & search handler
│   └── NotesScreen.kt          # Main notes dashboard with Extract CSV action
└── MainActivity.kt             # Edge-to-edge Compose entry point
```

---

## 🛠️ How to Build & Run

### Prerequisites
- **Android Studio Ladybug / Meerkat (or newer)**
- **JDK 17 or JDK 21+**
- Android SDK 30+ installed

### Build via Command Line
```powershell
# Build Debug APK
./gradlew assembleDebug

# Run Unit Tests
./gradlew test
```

The output APK will be generated at:
`app/build/outputs/apk/debug/app-debug.apk`

---

## 📄 License
This project is open-source and available under the [MIT License](LICENSE).
