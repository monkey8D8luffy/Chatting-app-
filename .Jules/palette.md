## 2024-06-17 - Compose Keyboard Handling
**Learning:** In Jetpack Compose, text fields that perform an action on submit (like Search, Done, or Send) should explicitly set the IME Action via KeyboardOptions and hide the keyboard using `LocalSoftwareKeyboardController.current?.hide()` in KeyboardActions to prevent the keyboard from obscuring the screen after the action is taken.
**Action:** Always check `KeyboardOptions` and `KeyboardActions` on `OutlinedTextField` and similar inputs to ensure they hide the keyboard on submit, especially in single-screen flows or when searching.
