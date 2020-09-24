package im.dino.dbinspector.ui.tables.content

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingData
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import im.dino.dbinspector.R
import im.dino.dbinspector.databinding.DbinspectorActivityContentBinding
import im.dino.dbinspector.ui.shared.Constants
import im.dino.dbinspector.ui.tables.pragma.PragmaActivity

internal class ContentActivity : AppCompatActivity() {

    lateinit var binding: DbinspectorActivityContentBinding

    private lateinit var viewModel: ContentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DbinspectorActivityContentBinding.inflate(layoutInflater)

        setContentView(binding.root)

        intent.extras?.let {
            val databaseName = it.getString(Constants.Keys.DATABASE_NAME)
            val databasePath = it.getString(Constants.Keys.DATABASE_PATH)
            val tableName = it.getString(Constants.Keys.TABLE_NAME)
            if (databaseName.isNullOrBlank().not() && databasePath.isNullOrBlank().not() && tableName.isNullOrBlank().not()) {
                viewModel = ViewModelProvider(
                    this,
                    ContentViewModelFactory(databasePath!!, tableName!!)
                ).get(ContentViewModel::class.java)

                setupToolbar(databasePath, databaseName!!, tableName)
                setupSwipeRefresh()
                setupRecyclerView()
            } else {
                showError()
            }
        } ?: showError()
    }

    private fun setupToolbar(databasePath: String, databaseName: String, tableName: String) =
        with(binding.toolbar) {
            setNavigationOnClickListener { finish() }
            subtitle = listOf(databaseName, tableName).joinToString(" / ")
            setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.clear -> {
                        clearTable(tableName)
                        true
                    }
                    R.id.pragma -> {
                        showTablePragma(databaseName, databasePath, tableName)
                        true
                    }
                    R.id.refresh -> {
                        (binding.recyclerView.adapter as? ContentAdapter)?.submitData(lifecycle, PagingData.empty())
                        query()
                        true
                    }
                    else -> false
                }
            }
        }

    private fun setupSwipeRefresh() =
        with(binding.swipeRefresh) {
            setOnRefreshListener {
                isRefreshing = false

                (binding.recyclerView.adapter as? ContentAdapter)?.let { adapter ->
                    adapter.submitData(lifecycle, PagingData.empty())
                    query()
                }
            }
        }

    private fun setupRecyclerView() =
        with(binding.recyclerView) {
            viewModel.header { tableHeaders ->
                layoutManager = GridLayoutManager(context, tableHeaders.size)
                adapter = ContentAdapter(tableHeaders)

                query()
            }
        }

    private fun showError() {

    }

    private fun showTablePragma(databaseName: String?, databasePath: String?, tableName: String) {
        startActivity(
            Intent(this, PragmaActivity::class.java)
                .apply {
                    putExtra(Constants.Keys.DATABASE_NAME, databaseName)
                    putExtra(Constants.Keys.DATABASE_PATH, databasePath)
                    putExtra(Constants.Keys.TABLE_NAME, tableName)
                }
        )
    }

    private fun clearTable(name: String) =
        MaterialAlertDialogBuilder(this)
            .setMessage(String.format(getString(R.string.dbinspector_clear_table_confirm), name))
            .setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int ->
                clear()
                dialog.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .create()
            .show()

    private fun query() =
        viewModel.query() {
            (binding.recyclerView.adapter as? ContentAdapter)?.submitData(it)
        }

    private fun clear() =
        viewModel.clear() {
            (binding.recyclerView.adapter as? ContentAdapter)?.submitData(it)
        }
}