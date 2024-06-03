@file:Suppress("DEPRECATION") // FIXME: Look into Parcelize not working with K2

package com.vsevolodganin.clicktrack.utils.parcelable

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parceler

// FIXME: Workarounds problems with K2: https://issuetracker.google.com/issues/315775835#comment16
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
expect annotation class Parcelize()

@Retention(AnnotationRetention.SOURCE)
@Repeatable
@Target(AnnotationTarget.CLASS, AnnotationTarget.PROPERTY)
expect annotation class TypeParceler<T, P : Parceler<in T>>()

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
expect annotation class IgnoredOnParcel()

typealias Parcelable = Parcelable
typealias Parceler<T> = Parceler<T>
