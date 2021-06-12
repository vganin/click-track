package com.vsevolodganin.clicktrack.lib.android

@OptionalExpectation
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
public expect annotation class AndroidParcelize()

@OptionalExpectation
@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
public expect annotation class AndroidIgnoredOnParcel()

@Repeatable
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
public expect annotation class AndroidTypeParceler<T, P : AndroidParceler<in T>>()

public expect interface AndroidParceler<T>
