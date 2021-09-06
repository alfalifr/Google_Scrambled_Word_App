package com.example.android.unscramble.ui.game

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.android.unscramble.R
import java.lang.IllegalArgumentException

class GameViewModel: ViewModel() {
  private val _currentWordCount = MutableLiveData(1)
  val currentWordCount: LiveData<Int>
    get() = _currentWordCount

  private val _currentWord = MutableLiveData<String>()
  val currentWord: LiveData<String>
    get()= _currentWord

  private val _currentScrambledWord = MutableLiveData<String>()
  val currentScrambledWord: LiveData<String>
    get()= _currentScrambledWord

  private val _score = MutableLiveData(0)
  val score : LiveData<Int>
    get()= _score

  private val _errorMsg = MutableLiveData<String>()
  val errorMsg : LiveData<String>
    get()= _errorMsg

  private val _isFinished = MediatorLiveData<Boolean>().apply {
    addSource(_currentWordCount) {
      value = it?.compareTo(MAX_NO_OF_WORDS) == 1
    }
  }
  val isFinished: LiveData<Boolean>
    get()= _isFinished

  private val viewedWords = mutableListOf<String>()

  init {
    setNextWord()
  }

  fun submitWord(context: Context, guessed: String) {
    if(guessed.equals(currentWord.value, ignoreCase = true)) {
      _score.value = _score.value!! + SCORE_INCREASE
      _currentWordCount.value = _currentWordCount.value!! +1
      setError(context, false)
      if(_isFinished.value != true) {
        setNextWord()
      }
    } else {
      setError(context, true)
    }
  }

  fun skipWord() {
    setError(null, false)
    _currentWordCount.value = _currentWordCount.value!! +1
    if(_isFinished.value != true) {
      setNextWord()
    }
  }

  fun restartGame() {
    setError(null, false)
    _score.value = 0
    _currentWordCount.value = 1
    _isFinished.value = false
    setNextWord()
  }

  private fun setNextWord() {
    do {
      _currentWord.value = allWordsList.random()
    } while(_currentWord.value in viewedWords)

    viewedWords += _currentWord.value!!

    val currentWordChars = _currentWord.value!!.toCharArray()
    val scrambledChars = currentWordChars.copyOf()
    do {
      scrambledChars.shuffle()
    } while(scrambledChars.contentEquals(currentWordChars))

    _currentScrambledWord.value = String(scrambledChars)
  }

  private fun setError(context: Context?, error: Boolean) {
    _errorMsg.value = if(!error) null
    else context?.getString(R.string.try_again)
      ?: throw IllegalArgumentException(
        "`context` can't be null if `error` is true"
      )
  }
}