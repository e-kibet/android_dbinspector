package com.infinum.dbinspector.domain.database.interactors

import com.infinum.dbinspector.data.Sources
import com.infinum.dbinspector.shared.BaseTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.get
import org.mockito.kotlin.any

@DisplayName("GetDatabasesInteractor tests")
internal class GetDatabasesInteractorTest : BaseTest() {

    override fun modules(): List<Module> = listOf(
        module {
            factory { mockk<Sources.Raw>() }
        }
    )

    @Test
    fun `Invoking interactor invokes source getDatabases`() {
        val source: Sources.Raw = get()
        val interactor = GetDatabasesInteractor(source)

        coEvery { source.getDatabases(any()) } returns mockk()

        test {
            interactor.invoke(any())
        }

        coVerify(exactly = 1) { source.getDatabases(any()) }
    }
}
