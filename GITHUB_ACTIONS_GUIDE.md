# GitHub Actions Android APK Build Pipeline (Manual Run)

This project includes a manual on-demand GitHub Actions workflow in [`.github/workflows/build-apk.yml`](.github/workflows/build-apk.yml) that executes automated test suites and builds the Android APK.

---

## 🛡️ Pre-Build Automated Test Suites

Before any APK is generated, the pipeline executes the full unit test suite via:
```bash
./gradlew testDebugUnitTest --no-daemon --stacktrace
```

If **any** test case fails, the build **immediately halts**, ensuring no broken APK is ever packaged or released.

### Test Coverage Highlights:
1. **Phonetic Transliteration Engine** (`TeluguTransliterationEngineTest`):
   - Direct dictionary lookups (greetings, conversational phrases, festivals, pop culture).
   - Case-insensitivity (`NAMASTE`, `BaGuNnAvA`).
   - Empty input, whitespace trimming, and punctuation/special character preservation.
   - Contextual next-word predictions with context words (`ఉగాది`, `నమస్తే`).
   - Rule-based phonetic converter (independent vowels, consonants with matras, virama/halant).
   - Compound consonants and conjuncts (`క్క`, `త్త`, `ప్ప`, `మ్మ`, `య్య`, `ల్ల`, `స్త్ర`, `క్ష`, `ప్ర`, `త్ర`).
   - Telugu digits conversion (`0-9` to `౦-౯`).
   - High-throughput LRU cache consistency and repeat query resolution.
   - Dictionary search (`searchDictionary`) across English and Telugu script.
2. **Telugu Script & Grammar Constants** (`TeluguScriptConstantsTest`):
   - Telugu vowels (Achulu) and consonants (Hallulu) completeness.
   - Guninthalu generation (16 standard phonetic forms for all consonants).
   - Pollu/virama stripping and clean matra attachment.
   - Common Vottulu (conjuncts) matrix generation.
3. **Telugu Translation Engine** (`TeluguTranslatorEngineTest`):
   - English to Telugu phrase translation.
   - Telugu to English message translation.
   - Case-insensitivity, whitespace, and transliteration fallbacks.
4. **Tone & Style Rephraser** (`ToneRephraserTest`):
   - Casual tone conversion (formal to conversational Telugu + slang particles `బ్రో`, `మావా`, `రా`).
   - Formal tone conversion (casual to respectful Telugu + polite suffix `నమస్కారం అండి`).
   - Classical/poetic Telugu transformation (`వందనములు`, `కుశలమా మిత్రమా`, `చిత్రరాజము`).
   - English translation and quick summary generators.
   - Empty input handling across all tones.
5. **Keyboard Themes & Palettes** (`KeyboardThemeColorsTest`):
   - Comprehensive palette resolution for all themes (`PITCH_BLACK`, `MIDNIGHT_SLATE`, `CLEAN_LIGHT`, `DESH_ROYAL_BLUE`, `SAFFRON_ORANGE`, `EMERALD_GREEN`, `CHERRY_BLOSSOM_WHITE`, `MATCHA_CREAM`, `COFFEE_ESPRESSO`, `CYBER_NEON_CONTRAST`, `NORDIC_GLACIER`, `MECHANICAL_GLOW`).
   - Custom accent color overrides.
6. **Data Models & State Integrity** (`DataModelsTest`):
   - Entity defaults, uniqueness of sound and effect IDs, haptic strength durations, and keyboard preferences defaults.

---

## ⚡ How to Run the Pipeline (Manually on GitHub)

1. Open your repository on GitHub: [**github.com/prasad-dp/TeluguSmart**](https://github.com/prasad-dp/TeluguSmart)
2. Click on the **Actions** tab at the top.
3. In the left sidebar, click **Build Android APK**.
4. Click the **Run workflow** dropdown on the right side.
5. Select the **Branch** (`main`) and choose the **Build Type** (`debug` is recommended for immediate testing, or `release`/`both`).
6. Click the green **Run workflow** button.

---

## 📱 How to Download the Artifacts

1. When the run finishes (green checkmark ✅, ~2–4 minutes):
2. Click on the completed run.
3. Scroll down to the **Artifacts** section at the bottom:
   - **`TeluguSmart-Android-APK`**: Downloadable ZIP containing `TeluguSmartKeyboard-debug.apk`.
   - **`TeluguSmart-Unit-Test-Report`**: HTML test report detailing all executed test suites and passing statuses.
