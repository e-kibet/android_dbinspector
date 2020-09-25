package im.dino.dbinspector.domain.schema.view

import android.database.Cursor
import im.dino.dbinspector.data.models.Row
import im.dino.dbinspector.domain.shared.AbstractDatabaseOperation
import kotlin.math.min

internal class AllViewsOperation(
    private val pageSize: Int,
    private val args: String?
) : AbstractDatabaseOperation<List<Row>>() {

    override fun query(): String = if (args.isNullOrBlank()) {
        String.format(FORMAT_ALL_VIEWS, "ASC")
    } else {
        String.format(FORMAT_SEARCH_VIEWS, args, "ASC")
    }

    override fun pageSize(): Int = pageSize

    override fun invoke(path: String, nextPage: Int?): List<Row> {
        database(path).use { database ->
            database.rawQuery(
                query(),
                null
            ).use { cursor ->
                pageCount(cursor.count)
                return collect(cursor, nextPage)
            }
        }
    }

    override fun collect(cursor: Cursor, page: Int?): List<Row> {
        val startRow = page?.let {
            pageSize() * it
        } ?: 0
        val endRow = min(startRow + pageSize(), cursor.count)

        return if (cursor.moveToPosition(startRow)) {
            (startRow until endRow).map { row ->
                Row(
                    position = row,
                    fields = (0 until cursor.columnCount).map {
                        cursor.getString(cursor.getColumnIndex(COLUMN_NAME))
                    }
                ).also {
                    cursor.moveToNext()
                }
            }
        } else {
            listOf()
        }
    }
}