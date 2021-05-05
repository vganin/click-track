import br.com.devsrsouza.svg2compose.Svg2Compose
import br.com.devsrsouza.svg2compose.VectorType

buildscript {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
    dependencies {
        classpath("com.github.DevSrSouza:svg-to-compose:0.6.0")
    }
}

val inputDir = file("src/main/svg")
val outputDir = file("build/generated/icons_gen")

tasks.register("generateIcons") {
    inputs.dir(inputDir)
    outputs.dir(outputDir)
    doLast {
        Svg2Compose.parse(
            applicationIconPackage = "com.vsevolodganin.clicktrack.icons",
            accessorName = "ClickTrackIcons",
            vectorsDirectory = inputDir,
            outputSourceDirectory = outputDir,
            type = VectorType.SVG,
            allAssetsPropertyName = "AllIcons"
        )
    }
}
