package org.qosp.notes.data.sync.local.model

import org.qosp.notes.data.model.Note

fun Note.asNextcloudNote(id: Long, category: String): LocalNote = LocalNote(
    id = id,
    title = title,
    content = if (isList) taskListToMd() else content,
    category = category,
    favorite = isPinned,
    modified = modifiedDate
)

fun LocalNote.asNewLocalNote(id: Long, notebookId: Long?) = Note(
    id = id,
    title = title,
    content = content,
    isPinned = favorite,
    modifiedDate = modified,
    notebookId = notebookId
)
