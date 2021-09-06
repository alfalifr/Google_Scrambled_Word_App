/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 */

package com.example.android.unscramble.ui.game

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.example.android.unscramble.R
import com.example.android.unscramble.databinding.GameFragmentBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Fragment where the game is played, contains the game logic.
 */
class GameFragment : Fragment() {
/*
    private var score = 0
    private var currentWordCount = 0
    private var currentScrambledWord = "test"
 */
    private val viewModel: GameViewModel by viewModels()


    // Binding object instance with access to the views in the game_fragment.xml layout
    private lateinit var binding: GameFragmentBinding

    // Create a ViewModel the first time the fragment is created.
    // If the fragment is re-created, it receives the same GameViewModel instance created by the
    // first fragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout XML file and return a binding object instance
        binding = DataBindingUtil.inflate(inflater, R.layout.game_fragment, container, false)
          //GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            maxQuestions = MAX_NO_OF_WORDS
            xmlViewModel = viewModel
            submit.setOnClickListener {
                viewModel.submitWord(
                    requireContext(),
                    binding.textInputEditText.text.toString()
                )
            }
            skip.setOnClickListener {
                viewModel.skipWord()
            }
        }

        viewModel.apply {
            currentScrambledWord.observe(viewLifecycleOwner) {
                if(it != null) {
                    //binding.textViewUnscrambledWord.text = it
                    binding.textInputEditText.text = null
                }
            }
            errorMsg.observe(viewLifecycleOwner) {
                binding.textField.error = it
            }
            isFinished.observe(viewLifecycleOwner) {
                if(it == true) {
                    showScoreDialog(score.value!!)
                    binding.wordCount.text = getString(
                        R.string.word_count, MAX_NO_OF_WORDS, MAX_NO_OF_WORDS
                    )
                }
            }
        }
    }

    private fun showScoreDialog(score: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.congratulations)
            .setMessage(
                getString(R.string.you_scored, score)
            )
            .setPositiveButton(R.string.play_again) { _, _ ->
                viewModel.restartGame()
            }
            .setNegativeButton(R.string.exit) { _, _ ->
                activity?.finishAndRemoveTask()
            }
            .show()
    }
}
