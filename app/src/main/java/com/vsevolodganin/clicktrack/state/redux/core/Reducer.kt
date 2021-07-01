package com.vsevolodganin.clicktrack.state.redux.core

typealias Reducer<T> = T.(Action) -> T
