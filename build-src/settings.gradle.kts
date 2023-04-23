dependencyResolutionManagement {
    @Suppress("UnstableApiUsage") // TODO: Remove when it's stable
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}
