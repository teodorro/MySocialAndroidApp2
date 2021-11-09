package com.example.mysocialandroidapp2.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mysocialandroidapp2.dto.Job
import com.example.mysocialandroidapp2.dto.Post

@Entity
data class JobEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val position: String,
    val start: Long,
    val finish: Long? = null,
    val link: String? = null,
){
    fun toDto() = Job(id, name, position, start, finish, link)

    companion object{
        fun fromDto(dto: Job) = JobEntity(
            dto.id, dto.name, dto.position, dto.start, dto.finish, dto.link
        )
    }
}

fun List<JobEntity>.toDto(): List<Job> = map(JobEntity::toDto)
fun List<Job>.toEntity(): List<JobEntity> {
    var jobEntities = mutableListOf<JobEntity>()
    for (job in this){
        jobEntities.add(JobEntity.fromDto(job))
    }
    return jobEntities
}
