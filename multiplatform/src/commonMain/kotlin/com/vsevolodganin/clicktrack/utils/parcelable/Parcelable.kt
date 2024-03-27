@file:Suppress("DEPRECATION") // FIXME: Look into Parcelize not working with K2

package com.vsevolodganin.clicktrack.utils.parcelable

import com.arkivanov.essenty.parcelable.IgnoredOnParcel
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parceler
import com.arkivanov.essenty.parcelable.Parcelize
import com.arkivanov.essenty.parcelable.TypeParceler

typealias Parcelable = Parcelable
typealias Parcelize = Parcelize
typealias Parceler<T> = Parceler<T>
typealias TypeParceler<T, P> = TypeParceler<T, P>
typealias IgnoredOnParcel = IgnoredOnParcel
