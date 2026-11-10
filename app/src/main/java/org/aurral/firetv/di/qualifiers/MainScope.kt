package org.aurral.firetv.di.qualifiers

import javax.inject.Qualifier

/**
 * A [kotlinx.coroutines.CoroutineScope] confined to the main thread.
 *
 * Required by anything that touches a Media3 `Player`/`MediaController`, which
 * may only be accessed on the thread that created it.
 */
@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class MainScope
