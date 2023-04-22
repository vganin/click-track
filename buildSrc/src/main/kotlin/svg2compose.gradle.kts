import br.com.devsrsouza.svg2compose.Svg2Compose
import br.com.devsrsouza.svg2compose.VectorType
import gradle.kotlin.dsl.accessors._9a33c1b87debd34fc7734fd23358d5b5.kotlin
import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

val inputDir = file("src/commonMain/resources/svg")
val outputDir = file("build/generated/source/svg2compose")

val convertSvgToCompose: Task by tasks.creating {
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

kotlin {
    sourceSets.named("commonMain") {
        kotlin.srcDir(outputDir)
    }
}

tasks.named("prepareKotlinIdeaImport") {
    dependsOn(convertSvgToCompose)
}
tasks.withType<KotlinCompilationTask<*>>().configureEach {
    dependsOn(convertSvgToCompose)
}
