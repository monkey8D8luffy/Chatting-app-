## 2025-06-15 - Improve Form Input UX with LocalSoftwareKeyboardController
**Learning:** In Jetpack Compose, explicitly handling `ImeAction.Done` and actively hiding the software keyboard using `LocalSoftwareKeyboardController.current?.hide()` vastly improves form submission UX, avoiding a persistent soft keyboard blocking the UI.
**Action:** When adding `OutlinedTextField` inputs that submit data, ensure `keyboardOptions` maps `imeAction` properly and `keyboardActions` closes the keyboard on submit.
