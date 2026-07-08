## 2024-10-25 - RTL Icon Support and Soft Keyboard UX
**Learning:** Directional icons (like Chat/Send) must be mirrored for Right-To-Left (RTL) languages for accessibility. Also, form inputs that trigger actions without form submission often leave the soft keyboard open, disrupting UX.
**Action:** Always prefer `Icons.AutoMirrored` variants over default directional icons. For text inputs triggering actions on submit, explicitly configure `KeyboardOptions(imeAction = ...)` and map `KeyboardActions` to use `LocalSoftwareKeyboardController.current?.hide()` to dismiss the keyboard cleanly.
