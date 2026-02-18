import dev.scaffoldit.hytale.wire.HytaleManifest

rootProject.name = "SkyHUD"

plugins {
  id("dev.scaffoldit") version "0.2.9"
}

common {
  dependencies {
    compileOnly("com.hypixel.hytale:Server:+")
  }

  repositories {
    maven("https://maven.hytale.com/release")
  }

  include("multiplehud", "models")
}

hytale {
  include("core")

  usePatchline("release")
  useVersion("latest")

  val pluginMainClass = providers.gradleProperty("plugin.mainClass").get()

  manifest {
    Version = providers.gradleProperty("version").get()

    Group = "MrEmPee"
    Main = pluginMainClass

    ServerVersion = "2026.02.18-f3b8fff95"
    //TODO Infer from dep

    //TODO Add in gradle prop
    Name = "SkyHUD"
    Description = "Never lose track of the world above while mining or exploring underground. Cave Sky Indicator adds a lightweight, non-intrusive overlay that displays the current surface conditions whenever you are unable to see the sky."
    Website = "https://github.com/Mr-EmPee/SkyHUD"

    OptionalDependencies = mapOf(
      Pair("Buuz135:MultipleHUD", "*")
    )

    IncludesAssetPack = true

    Authors = listOf(
      HytaleManifest.Author("Mr. EmPee", "empee@null.com", "https://github.com/Mr-EmPee")
    )
  }
}
