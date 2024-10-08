package ru.chsergeig.autoreply.client.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.chsergeig.autoreply.client.entity.Setting
import java.util.UUID

@Repository
interface SettingRepository : CrudRepository<Setting, UUID> {

    fun findBySettingKey(
        key: String,
    ): Setting?
}
