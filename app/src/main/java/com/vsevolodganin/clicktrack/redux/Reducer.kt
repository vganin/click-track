package com.vsevolodganin.clicktrack.redux

typealias Reducer<T> = T.(Action) -> T
