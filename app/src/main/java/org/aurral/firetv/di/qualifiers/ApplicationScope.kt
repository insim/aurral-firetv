package org.aurral.firetv.di.qualifiers

import javax.inject.Qualifier

/** A [kotlinx.coroutines.CoroutineScope] that lives for the whole application. */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class ApplicationScope
