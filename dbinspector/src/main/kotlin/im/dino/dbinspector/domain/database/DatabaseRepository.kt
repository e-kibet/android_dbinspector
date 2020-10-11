package im.dino.dbinspector.domain.database

import im.dino.dbinspector.domain.Interactors
import im.dino.dbinspector.domain.Repositories
import im.dino.dbinspector.domain.database.models.DatabaseDescriptor
import im.dino.dbinspector.domain.database.models.Operation

internal class DatabaseRepository(
    private val getPageInteractor: Interactors.GetDatabases,
    private val importInteractor: Interactors.ImportDatabases,
    private val removeInteractor: Interactors.RemoveDatabase,
    private val renameInteractor: Interactors.RenameDatabase,
    private val copyInteractor: Interactors.CopyDatabase
) : Repositories.Database {

    override suspend fun getPage(operation: Operation): List<DatabaseDescriptor> =
        getPageInteractor(operation).map {
            DatabaseDescriptor(
                it.exists(),
                it.parentFile?.absolutePath.orEmpty(),
                it.nameWithoutExtension,
                extension = it.extension
            )
        }

    override suspend fun import(operation: Operation): List<DatabaseDescriptor> =
        importInteractor(operation).map {
            DatabaseDescriptor(
                it.exists(),
                it.parentFile?.absolutePath.orEmpty(),
                it.nameWithoutExtension,
                extension = it.extension
            )
        }

    override suspend fun remove(operation: Operation): List<DatabaseDescriptor> =
        removeInteractor(operation).map {
            DatabaseDescriptor(
                it.exists(),
                it.parentFile?.absolutePath.orEmpty(),
                it.nameWithoutExtension,
                extension = it.extension
            )
        }

    override suspend fun rename(operation: Operation): List<DatabaseDescriptor> =
        renameInteractor(operation).map {
            DatabaseDescriptor(
                it.exists(),
                it.parentFile?.absolutePath.orEmpty(),
                it.nameWithoutExtension,
                extension = it.extension
            )
        }

    override suspend fun copy(operation: Operation): List<DatabaseDescriptor> =
        copyInteractor(operation).map {
            DatabaseDescriptor(
                it.exists(),
                it.parentFile?.absolutePath.orEmpty(),
                it.nameWithoutExtension,
                extension = it.extension
            )
        }
}