package com.example.notes.feature.notes.app

import com.example.notes.feature.notes.presentation.NotesMessageKey

data class NotesUiCopy(
    val backAction: String,
    val moreAction: String,
    val saveAction: String,
    val newNoteAction: String,
    val markCompleteAction: String,
    val markActiveAction: String,
    val deleteNoteAction: String,
    val titlePlaceholder: String,
    val bodyPlaceholder: String,
    val searchPlaceholder: String,
    val untitledFallback: String,
    val recentNotesTitle: String,
    val emptyRecentNotes: String,
    val allFilterLabel: String,
    val activeFilterLabel: String,
    val completedFilterLabel: String,
    val activeStatusLabel: String,
    val completedStatusLabel: String,
    val unsavedTimestamp: String,
    val editedPrefix: String,
    val colorSwatchLabel: String,
    val colorSelectedSuffix: String,
    val deleteDialogTitle: String,
    val deleteDialogMessage: String,
    val cancelAction: String,
    val confirmDeleteAction: String,
    val titleRequiredMessage: String,
    val saveSuccessMessage: String,
    val saveFailureMessage: String,
    val updateSuccessMessage: String,
    val updateFailureMessage: String,
    val deleteSuccessMessage: String,
    val deleteFailureMessage: String,
    val statusUpdateSuccessMessage: String,
    val statusUpdateFailureMessage: String,
) {
    fun messageFor(messageKey: NotesMessageKey): String =
        when (messageKey) {
            NotesMessageKey.TITLE_REQUIRED -> titleRequiredMessage
            NotesMessageKey.SAVE_SUCCESS -> saveSuccessMessage
            NotesMessageKey.SAVE_FAILURE -> saveFailureMessage
            NotesMessageKey.UPDATE_SUCCESS -> updateSuccessMessage
            NotesMessageKey.UPDATE_FAILURE -> updateFailureMessage
            NotesMessageKey.DELETE_SUCCESS -> deleteSuccessMessage
            NotesMessageKey.DELETE_FAILURE -> deleteFailureMessage
            NotesMessageKey.STATUS_UPDATE_SUCCESS -> statusUpdateSuccessMessage
            NotesMessageKey.STATUS_UPDATE_FAILURE -> statusUpdateFailureMessage
        }

    companion object {
        val English =
            NotesUiCopy(
                backAction = "Back",
                moreAction = "More",
                saveAction = "Save",
                newNoteAction = "New note",
                markCompleteAction = "Mark complete",
                markActiveAction = "Mark active",
                deleteNoteAction = "Delete note",
                titlePlaceholder = "Note title",
                bodyPlaceholder = "Start writing...",
                searchPlaceholder = "Search notes",
                untitledFallback = "Untitled",
                recentNotesTitle = "Recent notes",
                emptyRecentNotes = "Saved notes will appear here.",
                allFilterLabel = "All",
                activeFilterLabel = "Active",
                completedFilterLabel = "Completed",
                activeStatusLabel = "Active",
                completedStatusLabel = "Completed",
                unsavedTimestamp = "Not saved yet",
                editedPrefix = "Edited",
                colorSwatchLabel = "Color",
                colorSelectedSuffix = "selected",
                deleteDialogTitle = "Delete note?",
                deleteDialogMessage = "This note will be permanently removed.",
                cancelAction = "Cancel",
                confirmDeleteAction = "Delete",
                titleRequiredMessage = "Add a title before saving.",
                saveSuccessMessage = "Note saved.",
                saveFailureMessage = "Could not save note.",
                updateSuccessMessage = "Note updated.",
                updateFailureMessage = "Could not update note.",
                deleteSuccessMessage = "Note deleted.",
                deleteFailureMessage = "Could not delete note.",
                statusUpdateSuccessMessage = "Note status updated.",
                statusUpdateFailureMessage = "Could not update note status.",
            )
    }
}
