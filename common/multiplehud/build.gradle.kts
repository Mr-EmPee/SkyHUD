plugins {
  alias(libs.plugins.lombok)
}

repositories {
  maven("https://maven.hytale-mods.dev/releases")
}

dependencies {
  compileOnly("com.buuz135:MultipleHUD:1.0.5")
}