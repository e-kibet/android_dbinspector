package im.dino.dbinspector.ui.schema.triggers

import im.dino.dbinspector.domain.UseCases
import im.dino.dbinspector.domain.shared.models.Query
import im.dino.dbinspector.domain.shared.models.Statements
import im.dino.dbinspector.ui.schema.shared.SchemaDataSource
import im.dino.dbinspector.ui.shared.base.BaseViewModel

internal class TriggersDataSource(
    private val databasePath: String,
    private val getSchema: UseCases.GetTriggers
) : SchemaDataSource() {

    override var query: Query = Query(
        databasePath = databasePath,
        statement = Statements.Schema.triggers(),
        pageSize = BaseViewModel.PAGE_SIZE
    )

    override var argument: String?
        get() {
            return ""
        }
        set(value) {
            query = query.copy(
                statement = value?.let { Statements.Schema.searchTriggers(it) } ?: Statements.Schema.triggers(),
                page = 1
            )
            invalidate()
        }

    override fun refresh() {
        query = query.copy(page = 1)
        invalidate()
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, String> {
        val page = getSchema(query)
        query = query.copy(page = page.nextPage)
        return LoadResult.Page(
            data = page.fields,
            prevKey = null,
            nextKey = page.nextPage
        )
    }
}
