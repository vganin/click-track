package com.vsevolodganin.clicktrack.lib.android

import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parceler
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

public actual typealias AndroidParcelize = Parcelize

public actual typealias AndroidIgnoredOnParcel = IgnoredOnParcel

public actual typealias AndroidTypeParceler<T, P> = TypeParceler<T, P>

public actual typealias AndroidParceler<T> = Parceler<T>
