package net.ganin.vsevolod.clicktrack.redux

typealias Reducer<T> = T.(Action) -> T
