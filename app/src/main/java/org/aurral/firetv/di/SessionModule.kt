package org.aurral.firetv.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.aurral.firetv.data.session.CredentialsProvider
import org.aurral.firetv.data.session.SessionManager
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionModule {

    @Binds
    @Singleton
    abstract fun bindCredentialsProvider(sessionManager: SessionManager): CredentialsProvider
}
