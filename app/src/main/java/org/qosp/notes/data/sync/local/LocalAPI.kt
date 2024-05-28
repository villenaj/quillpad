package org.qosp.notes.data.sync.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import org.qosp.notes.data.sync.local.model.LocalCapabilities
import org.qosp.notes.data.sync.local.model.LocalNote
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Url

const val baseURL = "index.php/apps/notes/api/v1/"

interface NextcloudAPI {
    @GET
    suspend fun getNotesAPI(
        @Url url: String,
        @Header("Authorization") auth: String,
    ): List<LocalNote>

    @GET
    suspend fun getNoteAPI(
        @Url url: String,
        @Header("Authorization") auth: String,
    ): LocalNote

    @POST
    suspend fun createNoteAPI(
        @Body note: LocalNote,
        @Url url: String,
        @Header("Authorization") auth: String,
    ): LocalNote

    @PUT
    suspend fun updateNoteAPI(
        @Body note: LocalNote,
        @Url url: String,
        @Header("If-Match") etag: String,
        @Header("Authorization") auth: String,
    ): LocalNote

    @DELETE
    suspend fun deleteNoteAPI(
        @Url url: String,
        @Header("Authorization") auth: String,
    )

    @Headers(
        "OCS-APIRequest: true",
        "Accept: application/json"
    )
    @GET
    suspend fun getAllCapabilitiesAPI(
        @Url url: String,
        @Header("Authorization") auth: String,
    ): ResponseBody
}

suspend fun NextcloudAPI.getNotesCapabilities(config: LocalConfig): LocalCapabilities? {
    val endpoint = "ocs/v2.php/cloud/capabilities"
    val fullUrl = config.remoteAddress + endpoint

    val response = withContext(Dispatchers.IO) {
        getAllCapabilitiesAPI(url = fullUrl, auth = config.credentials).string()
    }

    val element = Json
        .parseToJsonElement(response).jsonObject["ocs"]?.jsonObject
        ?.get("data")?.jsonObject
        ?.get("capabilities")?.jsonObject
        ?.get("notes")
    return element?.let { Json.decodeFromJsonElement<LocalCapabilities>(it) }
}

suspend fun NextcloudAPI.deleteNote(note: LocalNote, config: LocalConfig) {
    deleteNoteAPI(
        url = config.remoteAddress + baseURL + "notes/${note.id}",
        auth = config.credentials,
    )
}

suspend fun NextcloudAPI.updateNote(note: LocalNote, etag: String, config: LocalConfig): LocalNote {
    return updateNoteAPI(
        note = note,
        url = config.remoteAddress + baseURL + "notes/${note.id}",
        etag = "\"$etag\"",
        auth = config.credentials,
    )
}

suspend fun NextcloudAPI.createNote(note: LocalNote, config: LocalConfig): LocalNote {
    return createNoteAPI(
        note = note,
        url = config.remoteAddress + baseURL + "notes",
        auth = config.credentials,
    )
}

suspend fun NextcloudAPI.getNotes(config: LocalConfig): List<LocalNote> {
    return getNotesAPI(
        url = config.remoteAddress + baseURL + "notes",
        auth = config.credentials,
    )
}

suspend fun NextcloudAPI.testCredentials(config: LocalConfig) {
    getNotesAPI(
        url = config.remoteAddress + baseURL + "notes",
        auth = config.credentials,
    )
}
