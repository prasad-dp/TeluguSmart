# Google Play Store Upload Assets

### 1. App Icons
- `play_store/icon_512x512.png`: Official Google Play Store high-resolution icon (512x512 px, 32-bit PNG, transparent/opaque matching Play Store requirements).
- `play_store/icon_1024x1024.png`: High-resolution master icon (1024x1024 px).
- `play_store/feature_graphic_1024x500.png`: Google Play Store Feature Graphic banner (1024x500 px, required for store listing).
- `icons/`: Standard Android launcher icon density sizes (48x48 mdpi, 72x72 hdpi, 96x96 xhdpi, 144x144 xxhdpi, 192x192 xxxhdpi, 512x512).

### 2. Android App Bundle (.aab) & APK
- In Google AI Studio, you can export the production **.aab** or **.apk** directly:
  1. Click on the project settings / gear menu in AI Studio.
  2. Select **"Export Android App Bundle (.aab)"** or **"Download APK"**.
  3. Upload the generated `.aab` file directly to the Google Play Console under **Production / Internal testing**.
- The debug package `app-debug.apk` is also backed up in this assets folder.
