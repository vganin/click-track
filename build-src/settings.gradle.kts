@Suppress("UnstableApiUsage") // TODO: Remove when it's stable
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
