package com.vsevolodganin.clicktrack

import androidx.compose.ui.window.Application

fun provideComposeUIViewController() = Application("Click Track") { TestComposeUi() }
