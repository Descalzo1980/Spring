package com.stasleonov.database.repository

import com.stasleonov.database.model.Note
import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository

interface NoteRepository: MongoRepository<Note, ObjectId> {
    fun findByOwnedId(ownedId: ObjectId): List<Note>
}