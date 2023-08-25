package com.kyu9.accountbook.application

import com.kyu9.accountbook.application.repository.TagRepoImpl
import com.kyu9.accountbook.application.repository.TagRepository
import com.kyu9.accountbook.common.CustomError
import com.kyu9.accountbook.common.MyTime
import com.kyu9.accountbook.domain.Tag
import com.kyu9.accountbook.swagger.model.GetSingleTagDto
import com.kyu9.accountbook.swagger.model.PostSingleTagDto
import lombok.extern.java.Log
import lombok.extern.log4j.Log4j
import lombok.extern.log4j.Log4j2
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CachePut
import org.springframework.stereotype.Service
import org.springframework.transaction.UnexpectedRollbackException
import javax.transaction.Transactional

@Service
@Log4j2
class TagService(
    private val tagRepoImpl: TagRepoImpl,

    //for TEST
    private val tagRepository: TagRepository
) {
    fun storeTag(dto: PostSingleTagDto): GetSingleTagDto{
//        log.info("")

        tagRepoImpl.storeEntity(
            Tag.of(
                dto.name!!,
                dto.color!!
            )
        ).let {
            return GetSingleTagDto(
                it.id?.toInt(),
                it.name,
                it.color,
                MyTime.toYyyymmddhhmmss(it.created),
                MyTime.toYyyymmddhhmmss(it.updated)
            )
        }
    }

    fun getAllTags(): List<GetSingleTagDto>{
        return tagRepoImpl.getAllTags().map {
            GetSingleTagDto(
                it.id?.toInt(),
                it.name,
                it.color,
                MyTime.toYyyymmddhhmmss(it.created),
                MyTime.toYyyymmddhhmmss(it.updated)
            )
        }
    }

    fun removeTag(id: Int){
        tagRepoImpl.removeEntityWithId(id.toLong())
    }

    fun removeTagError(id: Int){
        tagRepoImpl.deleteFunctionTest(id.toLong())
    }

    fun updateTag(id: Int, dto: PostSingleTagDto): GetSingleTagDto{
        return updateEntity(id, dto).let {
            GetSingleTagDto(
                it.id?.toInt(),
                it.name,
                it.color,
                MyTime.toYyyymmddhhmmss(it.created),
                MyTime.toYyyymmddhhmmss(it.updated)
            )
        }
    }

    @Transactional
    fun removeAndSave(id: Int, dto: PostSingleTagDto): GetSingleTagDto{
        println("============================================")
        val OptionalTag = tagRepoImpl.getOptionalWithId(id.toLong()).orElseThrow(CustomError.DATA_NOT_FOUND::doThrow)

        //이렇게해놓고 쿼리보면
        //select > insert > delete 순임 = 원하는대로 안나가는듯
//        deleteTag(OptionalTag)

        //ver2 flush >> 이건 원하는대로 잘만됨
//        tagRepoImpl.deleteWithEntity(OptionalTag)
//        tagRepository.flush()

        //ver3 query >> 이건 원하는대로 잘만됨
        deleteTagQuery(OptionalTag)

        OptionalTag.id = id.toLong()
        OptionalTag.name = dto.name!!
        OptionalTag.color = dto.color!!


        tagRepoImpl.storeEntity(
            OptionalTag
        ).let {
            return GetSingleTagDto(
                it.id?.toInt(),
                it.name,
                it.color,
                MyTime.toYyyymmddhhmmss(it.created),
                MyTime.toYyyymmddhhmmss(it.updated)
            )
        }
    }

    @Transactional
    fun deleteTag(tag: Tag){
        tagRepoImpl.deleteWithEntity(tag)
    }

    @Transactional
    fun deleteTagQuery(tag: Tag){
        tagRepoImpl.deleteWithEntity_query(tag)
    }

    @CachePut(value = ["tags"])
    fun updateEntity(id: Int, dto: PostSingleTagDto): Tag{
        tagRepoImpl.getOptionalWithId(id.toLong()).ifPresent{
            it.name = dto.name!!
            it.color = dto.color!!
            tagRepoImpl.storeEntity(it)
        }

        return tagRepoImpl.getEntityWithId(id.toLong())
    }

    @Transactional
    fun forDeadLockTest(tagId1: Long, tagId2: Long){
        val e1 = tagRepoImpl.getEntityWithId(tagId1)
        val e2 = tagRepoImpl.getEntityWithId(tagId2)

        var eName1: Int = e1.name.toInt()
        var eName2: Int = e2.name.toInt()

        e1.name = eName1++.toString()
        e2.name = eName2++.toString()

        tagRepoImpl.storeEntity(e1)
        tagRepoImpl.storeEntity(e2)
    }
}