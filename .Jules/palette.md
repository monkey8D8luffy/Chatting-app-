## 2024-05-14 - Soft Keyboard Support & RTL Support
**Learning:** Found an instance of `Icons.Default.Chat` which doesn't flip properly in RTL layouts, and an `OutlinedTextField` acting as a search bar missing the IME Action connection.
**Action:** Always prefer AutoMirrored icons (e.g., `Icons.AutoMirrored.Filled.Chat`) for directional vectors to support RTL out-of-the-box. Ensure text inputs that trigger actions on submit have proper `KeyboardOptions(imeAction = ...)` and `KeyboardActions` mapping, allowing soft keyboards to properly fire the event (a huge mobile UX win).
