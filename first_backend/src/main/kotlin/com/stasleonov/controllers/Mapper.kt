/*
package com.stasleonov.controllers

import com.stasleonov.database.model.Note
import com.stasleonov.database.repository.NoteRepository
import org.bson.types.ObjectId
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Instant

interface Mapper<in I, out O> {
    fun map(input: I): O
}

class NoteMapper : Mapper<NoteController.NoteRequest, NoteController.NoteResponse> {
    override fun map(input: NoteController.NoteRequest): NoteController.NoteResponse {
        return NoteController.NoteResponse(
            id = input.id ?: ObjectId.get().toHexString(),
            title = input.title,
            content = input.content,
            color = input.color,
            createAt = Instant.now()
        )
    }
}

class EntityMapper : Mapper<NoteController.NoteRequest, Note> {
    override fun map(input: NoteController.NoteRequest): Note {
        return Note(
            id = input.id?.let { ObjectId(it) } ?: ObjectId.get(),
            title = input.title,
            content = input.content,
            color = input.color,
            createAt = Instant.now(),
            ownedId = ObjectId(input.ownedId)
        )
    }
}

class ResponseMapper : Mapper<Note, NoteController.NoteResponse> {
    override fun map(input: Note): NoteController.NoteResponse {
        return NoteController.NoteResponse(
            id = input.id.toHexString(),
            title = input.title,
            content = input.content,
            color = input.color,
            createAt = input.createAt
        )
    }
}

@RestController
@RequestMapping("/notes")
class NoteController(
    private val repository: NoteRepository,
    private val entityMapper: EntityMapper,
    private val responseMapper: ResponseMapper
) {

    data class NoteRequest(
        val id: String?,
        val title: String,
        val content: String,
        val color: Long,
        val ownedId: String,
    )

    data class NoteResponse(
        val id: String,
        val title: String,
        val content: String,
        val color: Long,
        val createAt: Instant
    )

    @PostMapping
    fun save(body: NoteRequest): NoteResponse {
        val note = repository.save(entityMapper.map(body))
        return responseMapper.map(note)
    }
}*/
