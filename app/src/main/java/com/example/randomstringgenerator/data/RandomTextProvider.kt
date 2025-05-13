package com.example.randomstringgenerator.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri

class RandomTextProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.example.randomstringgenerator"
        val CONTENT_URI: Uri = Uri.parse("content://$AUTHORITY/randomtext")

        private const val RANDOM_TEXT = 1
        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(AUTHORITY, "randomtext", RANDOM_TEXT)
        }

        private val dataList = mutableListOf<ContentValues>() // In-memory storage
    }

    override fun onCreate(): Boolean = true

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        if (uriMatcher.match(uri) == RANDOM_TEXT && values != null) {
            dataList.add(values)
        }
        return uri
    }

    override fun query(
        uri: Uri, projection: Array<out String>?, selection: String?,
        selectionArgs: Array<out String>?, sortOrder: String?
    ): Cursor? {
        if (uriMatcher.match(uri) != RANDOM_TEXT) return null

        val cursor = MatrixCursor(arrayOf("timestamp", "length", "text"))
        for (value in dataList) {
            cursor.addRow(arrayOf(value["timestamp"], value["length"], value["text"]))
        }
        return cursor
    }

    override fun getType(uri: Uri): String? = null
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        if (uriMatcher.match(uri) != RANDOM_TEXT || selection != "text = ?" || selectionArgs.isNullOrEmpty()) return 0

        val targetText = selectionArgs[0]
        val iterator = dataList.iterator()
        var count = 0

        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item["text"] == targetText) {
                iterator.remove()
                count++
            }
        }
        return count
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int = 0
}