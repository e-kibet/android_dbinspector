package com.infinum.dbinspector.domain.raw.control.mappers

import com.infinum.dbinspector.data.models.local.cursor.output.QueryResult
import com.infinum.dbinspector.domain.Mappers
import com.infinum.dbinspector.domain.shared.models.Page

internal class RawQueryMapper(
    private val cellMapper: Mappers.Cell
) : Mappers.RawQuery {

    override suspend fun invoke(model: QueryResult): Page =
        Page(
            beforeCount = model.beforeCount,
            afterCount = model.afterCount,
            cells = model.rows.map { row ->
                row.fields.toList().map { field -> cellMapper(field) }
            }.flatten(),
            nextPage = model.nextPage
        )
}
