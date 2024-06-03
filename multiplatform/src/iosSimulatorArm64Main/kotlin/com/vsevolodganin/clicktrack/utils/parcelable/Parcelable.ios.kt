@file:Suppress("DEPRECATION")

package com.vsevolodganin.clicktrack.utils.parcelable

import com.arkivanov.essenty.parcelable.IgnoredOnParcel
import com.arkivanov.parcelize.darwin.Parcelize
import com.arkivanov.parcelize.darwin.TypeParceler

actual typealias Parcelize = Parcelize

actual typealias TypeParceler<T, P> = TypeParceler<T, P>

actual typealias IgnoredOnParcel = IgnoredOnParcel
