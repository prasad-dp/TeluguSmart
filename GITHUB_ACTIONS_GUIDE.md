# GitHub Actions Automated APK & AAB Build Pipeline

This project includes a complete GitHub Actions workflow configured in `.github/workflows/build-apk.yml`.

### Triggers
1. **Push to `main` or `master`** branches.
2. **Pull Requests** targeting `main` or `master`.
3. **Manual Run (`workflow_dispatch`)**: Trigger on-demand with one click from the **Actions** tab in your GitHub repository.

### Pipeline Stages
1. **Repository Checkout & JDK 17 Setup**: Uses Eclipse Temurin JDK 17.
2. **Gradle Caching**: Uses `gradle/actions/setup-gradle@v4` for fast builds.
3. **Automated Builds**:
   - Compiles **Debug APK** (`assembleDebug`).
   - Compiles **Android App Bundle (`.aab`)** (`bundleDebug`).
4. **Artifact Upload**:
   - `telugu-smart-keyboard-debug-apk`: Downloadable `.apk` file ready to install on Android devices.
   - `telugu-smart-keyboard-aab`: Downloadable `.aab` file ready for testing/Play Store staging.
   - Artifacts are stored for 30 days.

### How to use
1. Push the code to your GitHub repository.
2. Go to the **Actions** tab on GitHub.
3. Select **"Build Android APK & AAB"** and view the running build or download artifacts upon completion.
