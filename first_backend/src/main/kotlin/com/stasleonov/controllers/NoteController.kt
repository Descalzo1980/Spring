package com.stasleonov.controllers

import com.stasleonov.database.model.Note
import com.stasleonov.database.repository.NoteRepository
import org.bson.types.ObjectId
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

// GET http://localhost:8085/notes?ownedId=
// POST http://localhost:8085/notes
// DELETE http://localhost:8085/notes/123


@RestController
@RequestMapping("/notes")
class NoteController(
    private val repository: NoteRepository
) {

    data class NoteRequest(
        val id: String?,
        val title: String,
        val content: String,
        val color: Long,
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createAt: Instant
    )

    @PostMapping
    fun save(
        @RequestBody body: NoteRequest
    ): NoteResponse {
        val ownedId = SecurityContextHolder.getContext().authentication.principal as String
        val note = repository.save(
            Note(
                id = body.id?.let { ObjectId(it) } ?: ObjectId.get(),
                title = body.title,
                content = body.content,
                color = body.color,
                createAt = Instant.now(),
                ownedId = ObjectId(ownedId)
            )
        )
        return note.toResponse()
    }

    @GetMapping
    fun findByOwnerId(): List<NoteResponse> {
        val ownedId = SecurityContextHolder.getContext().authentication.principal as String
        return repository.findByOwnedId(ObjectId(ownedId)).map {
            it.toResponse()
        }
    }

    @DeleteMapping(path = ["/{id}"])
    fun deleteById(@PathVariable id: String) {
        val note = repository.findById(ObjectId(id)).orElseThrow {
            IllegalArgumentException("Note not found")
        }
        val ownedId = SecurityContextHolder.getContext().authentication.principal as String
        if (note.ownedId.toHexString() == ownedId) {
            repository.deleteById(ObjectId(id))
        }
    }
}

private fun Note.toResponse(): NoteController.NoteResponse {
    return NoteController.NoteResponse(
        id = id.toHexString(),
        title = title,
        content = content,
        color = color,
        createAt = createAt
    )
}