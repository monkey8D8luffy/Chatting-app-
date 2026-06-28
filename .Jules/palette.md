## 2024-06-25 - Improve Search Input Keyboard UX
**Learning:** Explicitly mapping `KeyboardOptions(imeAction = ImeAction.Search)` and firing `keyboardController?.hide()` via `KeyboardActions` is necessary to dismiss the soft keyboard gracefully on custom Compose text fields, providing proper feedback.
**Action:** Always map the IME action and use `LocalSoftwareKeyboardController.current?.hide()` in search/submit text fields to ensure the keyboard properly dismisses upon action completion.
