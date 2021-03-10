package com.vsevolodganin.clicktrack.migration

interface CanMigrate {
    fun migrate(fromVersion: Int, toVersion: Int)
}
