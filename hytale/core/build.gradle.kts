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

  val pluginVersion = providers.gradleProperty("plugin.version").get()
  val pluginMainClass = providers.gradleProperty("plugin.mainClass").get()

  archiveVersion = pluginVersion
  archiveBaseName = rootProject.name
  archiveClassifier = ""

  enableAutoRelocation = true

  val relocationBase = pluginMainClass.substringBeforeLast('.').replace('.', '/')

  relocationPrefix = "$relocationBase/dependencies"

  mergeServiceFiles()

  duplicatesStrategy = DuplicatesStrategy.WARN
}
