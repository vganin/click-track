import br.com.devsrsouza.svg2compose.Svg2Compose
import br.com.devsrsouza.svg2compose.VectorType
import com.android.build.gradle.BaseExtension

val inputDir = file("src/main/svg")
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

configure<BaseExtension> {
    sourceSets["main"].java {
        srcDir(outputDir)
    }
}

tasks["preBuild"].dependsOn(convertSvgToCompose)
