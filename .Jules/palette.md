## 2024-07-08 - Keyboard Actions Triggering Submit
**Learning:** Text inputs that require a follow-up action (like Connect or Submit) cause friction if the user has to manually dismiss the soft keyboard to tap the button or if the soft keyboard's "Enter/Done" button does nothing.
**Action:** Always map `keyboardOptions` with `imeAction = ImeAction.Done` and `keyboardActions` with an `onDone` handler that both hides the keyboard (`LocalSoftwareKeyboardController.current?.hide()`) and triggers the primary screen action if valid.
