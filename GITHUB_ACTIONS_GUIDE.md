# GitHub Actions Automated Android APK Pipeline

This project is configured with an automated continuous integration (CI) pipeline in [`.github/workflows/build-apk.yml`](.github/workflows/build-apk.yml) that automatically builds the Telugu Smart Keyboard Android APK.

---

## ⚡ Workflow Triggers

The workflow runs under three scenarios:

1. **Automatic Build on Code Push**:
   - Any commit pushed to the `main` or `master` branch immediately triggers a build.
2. **Pull Requests**:
   - Any PR targeting `main` or `master` is built to verify that code changes compile cleanly.
3. **Manual Trigger (`workflow_dispatch`)**:
   - You can trigger a build on demand anytime with one click directly in your browser:
     - Go to GitHub &rarr; **Actions** tab &rarr; **Build Android APK** &rarr; click **Run workflow**.
     - You can select build type: `debug` (default), `release`, or `both`.
4. **Automated Releases on Version Tags**:
   - Pushing a git tag (e.g. `git tag v1.0.0 && git push origin v1.0.0`) builds the APK and automatically attaches it to a new **GitHub Release** for direct download without needing to log in.

---

## 🛠️ Pipeline Stages

1. **Checkout & JDK 17 Setup**:
   - Checks out the repository and configures Eclipse Temurin JDK 17 (recommended for Android Gradle Plugin 9.x).
2. **Android SDK & License Agreements**:
   - Sets up Android command-line tools using `android-actions/setup-android@v3` and automatically accepts SDK licenses.
3. **Gradle Build Environment & Caching**:
   - Configures Gradle via `gradle/actions/setup-gradle@v4` with dependency caching for faster builds.
4. **Automated Asset & Keystore Preparation**:
   - Creates a valid mock `google-services.json` if not present in `app/`.
   - Generates `.env` from `.env.example` if missing.
   - Generates `debug.keystore` using `keytool` if not present, preventing any keystore missing errors during signing.
5. **Compilation**:
   - Builds `TeluguSmartKeyboard-debug.apk` (`./gradlew assembleDebug`).
   - Optionally builds release APK or Android App Bundle (`.aab`).
6. **Artifact Upload**:
   - The APK is packaged and uploaded as `TeluguSmart-Android-APK` (available for 30 days).
   - Generates a visual GitHub Job Summary with file details and direct installation steps.

---

## 📱 How to Get and Install the APK

### Step 1: Push Changes to GitHub
Make sure your latest code and workflow files are pushed to GitHub:
```bash
git add .
git commit -m "Configure GitHub Actions pipeline for Android APK"
git push origin main
```

### Step 2: View the Build
1. Open your repository on GitHub: [`https://github.com/prasad-dp/TeluguSmart`](https://github.com/prasad-dp/TeluguSmart)
2. Click on the **Actions** tab at the top.
3. You will see **Build Android APK** running. Click on it to view live logs.
   - *To run manually*: Click **Build Android APK** on the left &rarr; click the **Run workflow** dropdown on the right &rarr; click the green **Run workflow** button.

### Step 3: Download the APK
1. When the run finishes (green checkmark ✅), click on the completed run.
2. Scroll to the bottom of the page to find the **Artifacts** section.
3. Click on **TeluguSmart-Android-APK** to download the ZIP package.
4. Extract the ZIP file — inside you will find `TeluguSmartKeyboard-debug.apk` (and `app-debug.apk`).

### Step 4: Install on Your Phone
1. Transfer `TeluguSmartKeyboard-debug.apk` to your Android phone (or download it directly from GitHub using Chrome on your phone).
2. Tap the file in your phone's file manager or downloads to install.
3. If prompted, toggle **Allow installation from this source**.
4. Launch the Telugu Smart Keyboard and enable it in **Settings &rarr; System &rarr; Languages & Input &rarr; On-screen Keyboard**.
