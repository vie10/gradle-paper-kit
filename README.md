## How to use?

Ensure you have the jitpack repository declared:

```kotlin
maven("https://jitpack.io")
```

And then add the dependencies:

```kotlin
compileOnly("com.github.paper-kit", "gradle-paper-kit", "version")
ksp("com.github.paper-kit", "gradle-paper-kit", "version")
```