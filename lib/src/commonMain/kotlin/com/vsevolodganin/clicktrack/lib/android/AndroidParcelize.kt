package com.vsevolodganin.clicktrack.lib.android

@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
public expect annotation class AndroidParcelize()

@OptionalExpectation
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.BINARY)
public expect annotation class AndroidIgnoredOnParcel()
