package com.infinum.dbinspector.ui.settings

import android.os.Bundle
import android.widget.LinearLayout
import androidx.core.view.children
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.infinum.dbinspector.R
import com.infinum.dbinspector.databinding.DbinspectorActivitySettingsBinding
import com.infinum.dbinspector.databinding.DbinspectorItemIgnoredTableNameBinding
import com.infinum.dbinspector.domain.settings.models.Settings
import com.infinum.dbinspector.domain.shared.models.BlobPreviewMode
import com.infinum.dbinspector.domain.shared.models.TruncateMode
import com.infinum.dbinspector.ui.Presentation
import com.infinum.dbinspector.ui.shared.base.BaseActivity
import com.infinum.dbinspector.ui.shared.delegates.viewBinding
import kotlin.math.roundToInt
import org.koin.androidx.viewmodel.ext.android.viewModel

@Suppress("TooManyFunctions")
internal class SettingsActivity : BaseActivity<SettingsState, SettingsEvent>() {

    override val binding by viewBinding(DbinspectorActivitySettingsBinding::inflate)

    override val viewModel: SettingsViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initUi()

        viewModel.load()
    }

    override fun onState(state: SettingsState) =
        when (state) {
            is SettingsState.Settings -> setupUi(state.settings)
        }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.AddIgnoredTable -> addNewIgnoredTableNameView(event.name)
            is SettingsEvent.RemoveIgnoredTable -> removeIgnoredTableNameView(event.name)
        }
    }

    private fun initUi() =
        with(binding) {
            toolbar.setNavigationOnClickListener { finish() }

            linesSlider.valueFrom = Presentation.Constants.Settings.LINES_LIMIT_MINIMUM.toFloat()
            linesSlider.valueTo = Presentation.Constants.Settings.LINES_LIMIT_MAXIMUM.toFloat()

            decreaseLinesButton.setOnClickListener {
                linesSlider.value = (linesSlider.value - 1)
                    .coerceAtLeast(Presentation.Constants.Settings.LINES_LIMIT_MINIMUM.toFloat())
            }
            increaseLinesButton.setOnClickListener {
                linesSlider.value = (linesSlider.value + 1)
                    .coerceAtMost(Presentation.Constants.Settings.LINES_LIMIT_MAXIMUM.toFloat())
            }
        }

    private fun setupUi(settings: Settings) {
        setupIgnoredTableNames(settings)
        setupLinesLimit(settings)
        setupBlobPreview(settings)
    }

    private fun setupIgnoredTableNames(settings: Settings) =
        with(binding) {
            tableNameInputLayout.setEndIconOnClickListener {
                tableNameInputLayout.editText?.text?.toString().orEmpty().trim().split(",").forEach { newName ->
                    addIgnoredTableNameView(newName.trim())
                }
            }
            settings.ignoredTableNames.forEach {
                namesLayout.addView(
                    createIgnoredTableNameView(it)
                )
            }
        }

    private fun setupLinesLimit(settings: Settings) =
        with(binding) {
            linesCheckBox.isEnabled = true
            linesCheckBox.setOnCheckedChangeListener(null)
            linesCheckBox.isChecked = settings.linesLimitEnabled
            linesCheckBox.setOnCheckedChangeListener { _, isChecked ->
                linesView.isInvisible = isChecked.not()
                linesLayout.isVisible = isChecked
                viewModel.toggleLinesLimit(isChecked)
            }

            linesView.isInvisible = settings.linesLimitEnabled.not()
            linesLayout.isVisible = settings.linesLimitEnabled
            linesView.text = settings.linesCount.toString()

            linesSlider.clearOnChangeListeners()
            linesSlider.value = settings.linesCount.toFloat()
            linesSlider.addOnChangeListener { _, value, _ ->
                linesView.text = value.roundToInt().toString()
                viewModel.saveLinesCount(value.roundToInt())
            }

            truncateGroup.clearOnButtonCheckedListeners()
            truncateGroup.check(
                when (settings.truncateMode) {
                    TruncateMode.START -> R.id.truncateStart
                    TruncateMode.MIDDLE -> R.id.truncateMiddle
                    TruncateMode.END -> R.id.truncateEnd
                    else -> R.id.truncateEnd
                }
            )
            truncateGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.truncateStart -> TruncateMode.START
                        R.id.truncateMiddle -> TruncateMode.MIDDLE
                        R.id.truncateEnd -> TruncateMode.END
                        else -> TruncateMode.END
                    }.let {
                        viewModel.saveTruncateMode(it)
                    }
                }
            }
        }

    private fun setupBlobPreview(settings: Settings) =
        with(binding) {
            blobPreviewGroup.setOnCheckedChangeListener(null)
            placeHolderButton.isEnabled = true
            utf8Button.isEnabled = true
            hexadecimalButton.isEnabled = true
            base64Button.isEnabled = true
            blobPreviewGroup.check(
                when (settings.blobPreviewMode) {
                    BlobPreviewMode.PLACEHOLDER -> R.id.placeHolderButton
                    BlobPreviewMode.UTF_8 -> R.id.utf8Button
                    BlobPreviewMode.HEX -> R.id.hexadecimalButton
                    BlobPreviewMode.BASE_64 -> R.id.base64Button
                    else -> R.id.placeHolderButton
                }
            )
            blobPreviewGroup.setOnCheckedChangeListener { _, checkedId ->
                when (checkedId) {
                    R.id.placeHolderButton -> BlobPreviewMode.PLACEHOLDER
                    R.id.utf8Button -> BlobPreviewMode.UTF_8
                    R.id.hexadecimalButton -> BlobPreviewMode.HEX
                    R.id.base64Button -> BlobPreviewMode.BASE_64
                    else -> BlobPreviewMode.PLACEHOLDER
                }.let {
                    viewModel.saveBlobPreviewType(it)
                }
            }
        }

    private fun createIgnoredTableNameView(name: String): LinearLayout {
        val binding = DbinspectorItemIgnoredTableNameBinding.inflate(layoutInflater, binding.namesLayout, false)
        binding.nameView.text = name
        binding.removeButton.setOnClickListener {
            viewModel.removeIgnoredTableName(binding.nameView.text.toString())
        }
        binding.root.tag = name
        return binding.root
    }

    private fun addIgnoredTableNameView(name: String) =
        with(binding) {
            val ignoredNames = namesLayout.children
                .filterIsInstance<LinearLayout>()
                .mapNotNull { it.tag }
                .toList()

            if (name !in ignoredNames) {
                viewModel.saveIgnoredTableName(name)
            }
        }

    private fun addNewIgnoredTableNameView(name: String) {
        with(binding) {
            namesLayout.addView(
                createIgnoredTableNameView(name),
                0
            )
            tableNameInputLayout.editText?.text?.clear()
        }
    }

    private fun removeIgnoredTableNameView(name: String) =
        with(binding) {
            namesLayout.children
                .filterIsInstance<LinearLayout>()
                .find { it.tag == name }
                ?.let { namesLayout.removeView(it) }
        }
}
