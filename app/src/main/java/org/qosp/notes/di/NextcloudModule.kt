package org.qosp.notes.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.qosp.notes.data.repo.IdMappingRepository
import org.qosp.notes.data.repo.NoteRepository
import org.qosp.notes.data.repo.NotebookRepository
import org.qosp.notes.data.sync.nextcloud.NextcloudAPI
import org.qosp.notes.data.sync.nextcloud.NextcloudBackend
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NextcloudModule {
    private val json = Json { ignoreUnknownKeys = true }

    @Provides
    @Singleton
    fun provideNextcloud() = getRetrofitted<NextcloudAPI>()

    @Provides
    @Singleton
    fun provideNextcloudManager(
        nextcloudAPI: NextcloudAPI,
        @Named(NO_SYNC) noteRepository: NoteRepository,
        @Named(NO_SYNC) notebookRepository: NotebookRepository,
        idMappingRepository: IdMappingRepository,
    ) = NextcloudBackend(nextcloudAPI, noteRepository, notebookRepository, idMappingRepository)


    @OptIn(ExperimentalSerializationApi::class)
    private inline fun <reified T> getRetrofitted(): T {
        return Retrofit.Builder()
            .baseUrl("http://localhost/") // Since the URL is configurable by the user we set it later during the request
            .addConverterFactory(
                json.asConverterFactory("application/json".toMediaType())
            )
            .build()
            .create()
    }
}
