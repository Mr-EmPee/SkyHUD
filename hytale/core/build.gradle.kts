plugins {
  alias(libs.plugins.lombok)
  alias(libs.plugins.shadow)
}

repositories {
}

dependencies {
  shadow(libs.avajeinject)
  shadow(project(":common:models"))
  shadow(project(":common:multiplehud"))

  implementation(project(":common:models"))
  implementation(project(":common:multiplehud"))

  implementation(libs.avajeinject)

  annotationProcessor(libs.lombok)
  annotationProcessor(libs.avajeinject.processor)
}

tasks.shadowJar {
  configurations = listOf(project.configurations.shadow.get())

  archiveBaseName = rootProject.name

  enableAutoRelocation = true

  val pluginMainClass = providers.gradleProperty("plugin.mainClass").get()
  val relocationBase = pluginMainClass.substringBeforeLast('.').replace('.', '/')

  relocationPrefix = "$relocationBase/dependencies"

  mergeServiceFiles()

  duplicatesStrategy = DuplicatesStrategy.WARN
}

// Simple fix when hytale release a new version and gradle doesn't want to fetch
// configurations.all {
//   resolutionStrategy.cacheDynamicVersionsFor(0, "seconds")
//   resolutionStrategy.cacheChangingModulesFor(0, "seconds")
// }
