package com.kyu9.accountbook.common

import com.kyu9.accountbook.AccountBookApplication
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = [AccountBookApplication::class])
@ExtendWith(MockitoExtension::class, SpringExtension::class)
@AutoConfigureMockMvc
class TestFrame(
) {
    @Autowired lateinit var mockMvc: MockMvc

    fun postPerform(url: String, req: String, status: Int=200): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(status))
    }

    fun getPerform(url: String, status: Int=200): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(status))
    }

    fun putPerform(url: String, req: String, status: Int=200): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(req)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(status))
    }

    fun deletePerform(url: String, status: Int=200): ResultActions {
        return mockMvc.perform(
            MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_JSON)
        )
            .andExpect(MockMvcResultMatchers.status().`is`(status))
    }
}