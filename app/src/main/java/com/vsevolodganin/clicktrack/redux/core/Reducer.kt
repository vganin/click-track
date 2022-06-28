package com.vsevolodganin.clicktrack.redux.core

typealias Reducer<T> = T.(Action) -> T
