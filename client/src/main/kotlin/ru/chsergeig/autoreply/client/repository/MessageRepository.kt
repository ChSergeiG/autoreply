package ru.chsergeig.autoreply.client.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.chsergeig.autoreply.client.entity.Message
import java.util.UUID

@Repository
interface MessageRepository :
    CrudRepository<Message, UUID>
