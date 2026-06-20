## 2024-06-20 - Ensure Keyboard Dismissal on Search Submits
**Learning:** In Compose, when users type into a Search/Send field that immediately triggers an action or filters content, failing to explicitly hide the soft keyboard leaves it covering screen real estate, causing poor UX. KeyboardOptions must map cleanly to KeyboardActions.
**Action:** Use `LocalSoftwareKeyboardController.current?.hide()` in both the button's `onClick` and the text field's `KeyboardActions(onSearch = ...)` to ensure the keyboard is cleanly dismissed when the primary action is taken.
