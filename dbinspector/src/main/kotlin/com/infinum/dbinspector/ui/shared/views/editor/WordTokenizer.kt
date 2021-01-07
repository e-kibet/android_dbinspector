package com.infinum.dbinspector.ui.shared.views.editor

import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.widget.MultiAutoCompleteTextView.Tokenizer

internal class WordTokenizer(
    private val keywords: List<Keyword>
) : Tokenizer {

    companion object {
        private const val TOKEN_SPACE = ' '
        private const val TOKEN_SEMICOLON = ';'
        private val TOKEN_NEW_LINE = System.lineSeparator().first()
    }

    override fun findTokenStart(text: CharSequence, cursor: Int): Int {
        var i = cursor
        while (i > 0 && (text[i - 1] != TOKEN_SPACE)) {
            i--
        }
        while (i < cursor && text[i] == TOKEN_NEW_LINE) {
            i++
        }
        return i
    }

    override fun findTokenEnd(text: CharSequence, cursor: Int): Int {
        var i = cursor
        val length = text.length
        while (i < length) {
            if (text[i] == TOKEN_NEW_LINE) { // || text[i] == TOKEN_SEMICOLON
                return i
            } else {
                i++
            }
        }
        return length
    }

    @Suppress("NestedBlockDepth")
    override fun terminateToken(text: CharSequence): CharSequence {
        var i = text.length
        while (i > 0 && text[i - 1] == TOKEN_SPACE) {
            i--
        }
        return if (i > 0 && text[i - 1] == TOKEN_SPACE) {
            applySpan(text)?.let {
                SpannableString(text).apply {
                    setSpan(
                        it,
                        0,
                        text.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            } ?: text
        } else {
            if (text is Spanned) {
                val spannableString = SpannableString("$text$TOKEN_NEW_LINE")
                TextUtils.copySpansFrom(
                    text,
                    0,
                    text.length,
                    Any::class.java,
                    spannableString,
                    0
                )
                spannableString
            } else {
                applySpan(text)?.let {
                    SpannableString("$text$TOKEN_SPACE").apply {
                        setSpan(
                            it,
                            0,
                            text.length,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                } ?: "$text$TOKEN_SPACE"
            }
        }
    }

    private fun applySpan(token: CharSequence) =
        keywords
            .find { it.value == token.toString() }
            ?.let {
                when (it.type) {
                    KeywordType.NAME -> StyleSpan(Typeface.ITALIC)
                    KeywordType.SQL -> StyleSpan(Typeface.BOLD)
                }
            }
}
