package com.example.laboratorio11

import com.example.laboratorio11.network.dto.login.LoginRequest
import com.example.laboratorio11.network.service.AuthService
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class LoginTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var authService: AuthService

    //Este fragmento de codigo debe de suceder antes que las demas pruebas
    @Before
    fun setup (){
        mockWebServer = MockWebServer()

        authService= Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

    @Test
    fun loginTest() = runTest{
        val mockResponse = MockResponse()
        mockResponse.setBody("""{"msg":"Login successful", "token":"Test token"}""")
        mockResponse.setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val response = authService.login(LoginRequest("admin", "admin"))
        mockWebServer.takeRequest()

        Assert.assertEquals("Login successful", response.message)
        Assert.assertEquals("Test token", response.token)
    }

    @Test
    fun unSuccessfulLogin() = runTest{
        val mockResponse = MockResponse()
        mockResponse.setBody("""{"msg": "Check credentials"}""")
        mockWebServer.enqueue(mockResponse)

        val response = authService.login(LoginRequest("admin","admin"))
        mockWebServer.takeRequest()

        Assert.assertEquals("Check credentials", response.message)
    }

    //Este fragmento de codigo debe suceder despues de la prueba del login
    @After
    fun tearDown(){
        mockWebServer.shutdown()
    }

}