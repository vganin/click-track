package net.ganin.vsevolod.clicktrack.utils

inline fun <reified T : Any?> Any?.optionalCast(): T? = this as? T
inline fun <reified T : Any?> Any?.cast(): T = this as T
