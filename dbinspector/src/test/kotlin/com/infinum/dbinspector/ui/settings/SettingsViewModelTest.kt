package com.infinum.dbinspector.ui.settings

import com.infinum.dbinspector.domain.UseCases
import com.infinum.dbinspector.domain.settings.models.Settings
import com.infinum.dbinspector.domain.shared.models.BlobPreviewMode
import com.infinum.dbinspector.domain.shared.models.TruncateMode
import com.infinum.dbinspector.shared.BaseTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.get
import org.mockito.kotlin.any

@DisplayName("SettingsViewModel tests")
internal class SettingsViewModelTest : BaseTest() {

    override fun modules(): List<Module> = listOf(
        module {
            single { mockk<UseCases.GetSettings>() }
            single { mockk<UseCases.SaveIgnoredTableName>() }
            single { mockk<UseCases.RemoveIgnoredTableName>() }
            single { mockk<UseCases.ToggleLinesLimit>() }
            single { mockk<UseCases.SaveLinesCount>() }
            single { mockk<UseCases.SaveTruncateMode>() }
            single { mockk<UseCases.SaveBlobPreviewMode>() }
            factory { SettingsViewModel(get(), get(), get(), get(), get(), get(), get()) }
        }
    )

    @Test
    fun `Get current default settings`() {
        val expected: Settings = mockk {
            every { linesLimitEnabled } returns false
            every { linesCount } returns 100
            every { truncateMode } returns TruncateMode.END
            every { blobPreviewMode } returns BlobPreviewMode.PLACEHOLDER
            every { ignoredTableNames } returns listOf()
        }
        val useCase: UseCases.GetSettings = get()
        val result: Settings = mockk()
        coEvery { useCase.invoke(any()) } returns result

        val viewModel: SettingsViewModel = get()
        viewModel.load()

        coVerify(exactly = 1) { useCase.invoke(any()) }
        assertEquals(viewModel.stateFlow.replayCache.first(), SettingsState.Settings(settings = expected))
        // TODO Lambdas are replaced
//        coVerify(exactly = 1) { action.invoke(result) }
    }

    @Test
    fun `Save ignored table name`() {
        val name = "android_metadata"
        val useCase: UseCases.SaveIgnoredTableName = get()
        coEvery { useCase.invoke(any()) } returns Unit

        val viewModel: SettingsViewModel = get()
        viewModel.saveIgnoredTableName(name)

        coVerify(exactly = 1) { useCase.invoke(any()) }
        // TODO Lambdas are replaced
//        coVerify(exactly = 1) { action.invoke(result) }
    }

    @Test
    fun `Remove ignored table name`() {
        val name = "android_metadata"
        val useCase: UseCases.RemoveIgnoredTableName = get()
        coEvery { useCase.invoke(any()) } returns Unit

        val viewModel: SettingsViewModel = get()
        viewModel.removeIgnoredTableName(name)

        coVerify(exactly = 1) { useCase.invoke(any()) }
        // TODO Lambdas are replaced
//        coVerify(exactly = 1) { action.invoke(result) }
    }

    @Test
    fun `Toggle ON lines limit`() {
        val state = true
        val useCase: UseCases.ToggleLinesLimit = get()
        coEvery { useCase.invoke(any()) } returns Unit

        val viewModel: SettingsViewModel = get()
        viewModel.toggleLinesLimit(state)

        coVerify(exactly = 1) { useCase.invoke(any()) }
    }

    @Test
    fun `Toggle OFF lines limit`() {
        val state = false
        val useCase: UseCases.ToggleLinesLimit = get()
        coEvery { useCase.invoke(any()) } returns Unit

        val viewModel: SettingsViewModel = get()
        viewModel.toggleLinesLimit(state)

        coVerify(exactly = 1) { useCase.invoke(any()) }
    }

    @Test
    fun `Save lines limit count`() {
        val useCase: UseCases.SaveLinesCount = get()
        coEvery { useCase.invoke(any()) } returns Unit

        val viewModel: SettingsViewModel = get()
        viewModel.saveLinesCount(any())

        coVerify(exactly = 1) { useCase.invoke(any()) }
    }

    @ParameterizedTest
    @EnumSource(TruncateMode::class)
    fun `Save truncate per mode`(truncateMode: TruncateMode) {
        val useCase: UseCases.SaveTruncateMode = get()
        coEvery { useCase.invoke(any()) } returns Unit

        val viewModel: SettingsViewModel = get()
        viewModel.saveTruncateMode(truncateMode)

        coVerify(exactly = 1) { useCase.invoke(any()) }
    }

    @ParameterizedTest
    @EnumSource(BlobPreviewMode::class)
    fun `Save blob preview per mode`(blobPreviewMode: BlobPreviewMode) {
        val useCase: UseCases.SaveBlobPreviewMode = get()
        coEvery { useCase.invoke(any()) } returns Unit

        val viewModel: SettingsViewModel = get()
        viewModel.saveBlobPreviewType(blobPreviewMode)

        coVerify(exactly = 1) { useCase.invoke(any()) }
    }
}
