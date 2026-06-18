## 2024-06-18 - Compose Keyboard UX
**Learning:** In Jetpack Compose, inputs without defined `KeyboardOptions` and `KeyboardActions` cause dead-ends in UX where soft-keyboard 'Enter' does nothing and the keyboard remains obscuring the view after button taps.
**Action:** Always map `imeAction` appropriately and utilize `LocalSoftwareKeyboardController` to explicitly dismiss the keyboard on action callbacks.
