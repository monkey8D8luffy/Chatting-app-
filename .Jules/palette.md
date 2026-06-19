## 2024-06-25 - Compose Keyboard Actions
**Learning:** For Android apps built with Jetpack Compose, text inputs requiring action (like submitting a form or code) often neglect the software keyboard's "done" or "send" action. This results in the user having to tap to close the keyboard before tapping the action button, which breaks flow.
**Action:** Always implement `KeyboardOptions` (e.g. `imeAction = ImeAction.Done`) and a corresponding `KeyboardActions` handler that hides the keyboard using `LocalSoftwareKeyboardController.current?.hide()` and triggers the target action directly.
