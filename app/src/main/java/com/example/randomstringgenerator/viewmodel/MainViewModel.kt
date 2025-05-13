package com.example.randomstringgenerator.viewmodel

import android.app.Application
import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.randomstringgenerator.data.RandomTextProvider
import com.example.randomstringgenerator.model.RandomText
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val _randomTexts = MutableLiveData<List<RandomText>>()
    val randomTexts: LiveData<List<RandomText>> = _randomTexts

    private val charset = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    fun generateRandomText(lengthRange: IntRange) {
        val randomText = generateRandomTextString(lengthRange)
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Insert into the ContentProvider
        val values = ContentValues().apply {
            put("timestamp", timestamp)
            put("length", randomText.length)
            put("text", randomText)
        }

        try {
            val uri = getApplication<Application>().contentResolver.insert(RandomTextProvider.CONTENT_URI, values)
            Log.d("MainViewModel", "Inserted into provider: $uri")
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error inserting random text: ${e.message}")
        }

        // Query the data from the ContentProvider
        val cursor = getApplication<Application>().contentResolver.query(
            RandomTextProvider.CONTENT_URI, null, null, null, null
        )

        val randomTextList = mutableListOf<RandomText>()

        cursor?.use {
            while (it.moveToNext()) {
                val randomText = RandomText(
                    timestamp = it.getString(0),
                    length = it.getInt(1),
                    text = it.getString(2)
                )
                randomTextList.add(randomText)
            }
        }

        _randomTexts.value = randomTextList
    }

    private fun generateRandomTextString(lengthRange: IntRange): String {
        val length = lengthRange.random()
        return (1..length).map { charset.random() }.joinToString("")
    }

    fun deleteRandomText(textToDelete: String) {
        try {
            val rowsDeleted = getApplication<Application>().contentResolver.delete(
                RandomTextProvider.CONTENT_URI,
                "text = ?",
                arrayOf(textToDelete)
            )
            Log.d("MainViewModel", "Deleted $rowsDeleted rows from provider")

            // Refresh list
            refreshRandomTexts()
        } catch (e: Exception) {
            Log.e("MainViewModel", "Error deleting random text: ${e.message}")
        }
    }

    private fun refreshRandomTexts() {
        val cursor = getApplication<Application>().contentResolver.query(
            RandomTextProvider.CONTENT_URI, null, null, null, null
        )

        val randomTextList = mutableListOf<RandomText>()

        cursor?.use {
            while (it.moveToNext()) {
                val randomText = RandomText(
                    timestamp = it.getString(0),
                    length = it.getInt(1),
                    text = it.getString(2)
                )
                randomTextList.add(randomText)
            }
        }

        _randomTexts.value = randomTextList
    }

}
