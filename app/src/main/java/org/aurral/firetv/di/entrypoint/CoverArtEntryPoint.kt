package org.aurral.firetv.di.entrypoint

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.aurral.firetv.data.api.SubsonicUrlBuilder

/** Lets composables obtain URL building without a ViewModel. */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface CoverArtEntryPoint {
    fun subsonicUrlBuilder(): SubsonicUrlBuilder
}
