## 2024-06-18 - Compose Soft Keyboard Obstructs Viewport
**Learning:** In Jetpack Compose, single-line text inputs (like OutlinedTextFields) combined with `enableEdgeToEdge` can lead to soft keyboards permanently obstructing the viewport or search results if not explicitly dismissed when the action (like ImeAction.Search) is fired.
**Action:** Always map `KeyboardOptions` (especially ImeAction triggers like Search, Done, Go) to `KeyboardActions` and explicitly call `LocalSoftwareKeyboardController.current?.hide()` on the trigger.
