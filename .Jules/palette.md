## 2024-05-18 - [Explicit Soft Keyboard Management in Jetpack Compose]
**Learning:** [Jetpack Compose TextFields do not automatically hide the soft keyboard on custom actions like "Connect" or "Search". Users can be confused when tapping the action button triggers the function but the keyboard continues to obscure the screen.]
**Action:** [Always inject `LocalSoftwareKeyboardController.current` and explicitly map `keyboardActions` (like `onGo` or `onDone`) to hide the soft keyboard, providing immediate UI feedback when an action starts.]
