package ru.chsergeig.autoreply.client.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.util.UUID

@Entity
data class Setting(

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    var settingKey: String? = null,
    var settingValue: String? = null,

)
