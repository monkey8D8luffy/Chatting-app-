## 2024-10-24 - Improve Keyboard UX in Compose TextFields
**Learning:** For inputs triggering navigation/submit actions via `KeyboardActions`, explicitly hiding the keyboard using `LocalSoftwareKeyboardController.current?.hide()` ensures clean, uninterrupted transitions. Without it, the soft keyboard may remain on screen in subsequent views.
**Action:** Always map the appropriate `ImeAction` in `KeyboardOptions` and actively hide the keyboard within the `KeyboardActions` callback.
