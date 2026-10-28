# GitHub Actions Android APK Build Pipeline (Manual Run)

This project includes a manual on-demand GitHub Actions workflow in [`.github/workflows/build-apk.yml`](.github/workflows/build-apk.yml) to compile and deliver the Android APK whenever you need it.

---

## ⚡ How to Run the Pipeline (Manually on GitHub)

1. Open your repository on GitHub: [**github.com/prasad-dp/TeluguSmart**](https://github.com/prasad-dp/TeluguSmart)
2. Click on the **Actions** tab at the top.
3. In the left sidebar, click **Build Android APK**.
4. Click the **Run workflow** dropdown on the right side.
5. Select the **Branch** (`main`) and choose the **Build Type** (`debug` is recommended for immediate testing, or `release`/`both`).
6. Click the green **Run workflow** button.

---

## 📱 How to Download & Install the APK

1. Once the run finishes (green checkmark ✅, usually takes 2–4 minutes):
2. Click on the completed run.
3. Scroll down to the **Artifacts** section at the bottom.
4. Click **`TeluguSmart-Android-APK`** to download the ZIP file.
5. Extract the ZIP on your computer or phone to get **`TeluguSmartKeyboard-debug.apk`**.
6. Transfer to your phone and install!

---

## 🛠️ Pipeline Architecture

- **Runner**: `ubuntu-latest`
- **JDK**: Eclipse Temurin Java 17
- **Android SDK**: Pre-installed GitHub Actions Android SDK with pre-approved licenses
- **Gradle**: Gradle 9.x with dependency caching
- **Safety**:
  - Auto-creates mock `app/google-services.json` if missing
  - Auto-provisions `.env` if missing
  - Auto-generates `debug.keystore` with `keytool` before compilation
