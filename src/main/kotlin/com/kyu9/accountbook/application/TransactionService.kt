package com.kyu9.accountbook.application

import com.kyu9.accountbook.application.repository.UsageTransactionRepoImpl
import com.kyu9.accountbook.common.MyTime
import com.kyu9.accountbook.domain.UsageTransaction
import com.kyu9.accountbook.domain.properties.MoneyType
import com.kyu9.accountbook.swagger.model.GetSingleTransResponseDto
import com.kyu9.accountbook.swagger.model.PostTranRequestDto
import com.kyu9.accountbook.swagger.model.PostTransResponseDto
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
@RequiredArgsConstructor
class TransactionService(
    private val transactionRepoImpl: UsageTransactionRepoImpl
) {

    fun storeFromDto(tranReqDto: PostTranRequestDto): PostTransResponseDto{
        val registered = MyTime.toLocalDateTime(tranReqDto.registeredAt!!)

        //todo categoryId 검증 필요

        return transactionRepoImpl.storeEntity(
            UsageTransaction(
                amount = tranReqDto.amount?.toLong()!!,
                registered = registered,
                title = tranReqDto.title!!,
                content = tranReqDto.content!!,
                categoryId = tranReqDto.categoryId?.toLong()!!,
                moneyType = tranReqDto.moneyType.toString()
            )
        ).let {
            PostTransResponseDto(it.id?.toInt())
        }
    }

    fun getSingleTransaction(utid: String): GetSingleTransResponseDto {
        return transactionRepoImpl.getEntityWithId(utid.toLong()).let{
            GetSingleTransResponseDto(
                utid = it.id?.toInt(),
                amount = it.amount.toInt(),
                registeredAt = MyTime.toYyyymmddhhmmss(it.registered),
                title = it.title,
                content = it.content,
                categoryId = it.categoryId?.toInt(),
                moneyType = GetSingleTransResponseDto.MoneyType.valueOf(it.moneyType.toString().uppercase()),
                created = MyTime.toYyyymmddhhmmss(it.created),
                updated = MyTime.toYyyymmddhhmmss(it.updated)
            )
        }
    }
}