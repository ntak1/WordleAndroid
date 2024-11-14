import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.wordle.R
import com.example.wordle.business.*

// WordleAdapter.kt
class WordleAdapter(private val cells: List<Cell>, private val wordleState: WordleState) :
    RecyclerView.Adapter<WordleAdapter.WordleViewHolder>() {

    private lateinit var holder: WordleViewHolder
    companion object {
        private fun getHolderCellByIndex(holder: WordleViewHolder, index: Int): WordleViewHolder? {
            return holder.itemView.rootView.findViewById<RecyclerView>(R.id.wordleGrid)
                .findViewHolderForAdapterPosition(index)
                    as? WordleViewHolder
        }

    }

    fun resetBoard() {
        for (i in 0..<WordleState.HEIGHT*WordleState.WIDTH) {
            getHolderCellByIndex(holder, i)?.letterView?.setBackgroundColor(getCellColorFor(CellState.NOT_EVALUATED))
            getHolderCellByIndex(holder, i)?.letterView?.text = null
        }
        for (i in 0..<WordleState.WIDTH) {
            getHolderCellByIndex(holder, i)?.letterView?.isEnabled = true
        }
        for (i in WordleState.WIDTH..<WordleState.HEIGHT*WordleState.WIDTH) {
            getHolderCellByIndex(holder, i)?.letterView?.isEnabled = false
        }
        getHolderCellByIndex(holder, 0)?.letterView?.requestFocus()
    }

    inner class WordleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val letterView: EditText = view.findViewById(R.id.cell_letter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_item, parent, false)
        holder = WordleViewHolder(view)
        return this.holder
    }

    override fun onBindViewHolder(holder: WordleViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val cell = cells[holder.adapterPosition]

        // Set initial text and background color for the cell
        holder.letterView.text = null
        holder.letterView.setBackgroundColor(cell.color)
        // Only the first row should be editable
        if (position >= WordleState.WIDTH) {
            holder.letterView.isEnabled = false
        }

        // Listen for text input changes
        holder.letterView.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Update the cell letter when user inputs a character
                if (!s.isNullOrEmpty()) {
                    if (s.isNotBlank()) {
                        cell.letter = s[0]
                        Log.d(WordleState.LOGGER_TAG, String.format("position %d, s=%s", position, s.toString()))

                        if ((position + 1) % WordleState.WIDTH == 0) {
                            Log.d(WordleState.LOGGER_TAG, "reached end of line")
                            val startPosition = position - WordleState.WIDTH + 1
                            val wordLine = StringBuilder()
                            for (i in startPosition..position) {
                                wordLine.append(cells[i].letter.toString().trim())
                            }
                            Log.d(WordleState.LOGGER_TAG, String.format("wordLine %s", wordLine))
                            wordleState.currentWord = Word(wordLine.toString(), listOf())
                        }
                        // Shift the focus to the next cell in the row
                        else if (position < cells.size - 1 && (position + 1) % WordleState.WIDTH != 0) {
                            holder.letterView.clearFocus()
                            getHolderCellByIndex(holder, position +1)?.letterView?.requestFocus()
                        }
                    } else {
                        cell.letter = null
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        holder.letterView.setOnKeyListener { _, keyCode, event ->
            // Detect backspace key when the cell is empty
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                if (holder.letterView.text.isEmpty() && position > 0) {
                    // Move focus to the previous cell
                    holder.letterView.clearFocus()
                    val previousCell = getHolderCellByIndex(holder, position -1)
                    previousCell?.letterView?.requestFocus()
                    previousCell?.letterView?.setSelection(previousCell.letterView.text.length)
                    return@setOnKeyListener true // Consume the backspace event
                }
            }
            // Detect enter key in the end of the line
            else if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                // Color each letter of the word
                val answerFrequencyMap = wordleState.chosenWord?.getWordFrequencyMap()
                if (answerFrequencyMap != null) {
                    val startIndex = wordleState.currentRow * WordleState.WIDTH
                    Log.d(WordleState.LOGGER_TAG, "[colorWord] startIndex $startIndex")
                    Log.d(WordleState.LOGGER_TAG, "wordleState ${wordleState.chosenWord}, ${wordleState.currentWord}")
                    for ((j, i) in (startIndex..<startIndex + WordleState.WIDTH).withIndex()) {
                        val currChar = wordleState.currentWord?.word?.get(j)
                        val ansChar = wordleState.chosenWord?.word?.get(j)
                        if (currChar != null && currChar == ansChar) {
                            getHolderCellByIndex(holder, i)?.letterView
                                ?.setBackgroundColor(getCellColorFor(CellState.RIGHT_POSITION))
                            answerFrequencyMap[currChar] = answerFrequencyMap[currChar]!! -1
                        } else {
                            if (currChar != null && answerFrequencyMap.containsKey(currChar) && answerFrequencyMap[currChar]!! > 0) {
                                getHolderCellByIndex(holder, i)?.letterView
                                    ?.setBackgroundColor(getCellColorFor(CellState.CONTAINS_WRONG_POSITION))
                                answerFrequencyMap[currChar] = answerFrequencyMap[currChar]!! -1
                            } else {
                                getHolderCellByIndex(holder, i)?.letterView
                                    ?.setBackgroundColor(getCellColorFor(CellState.NOT_CONTAINS))
                            }
                        }
                    }
                }

                // Check if the rowWord matches the chosenWord
                if (wordleState.chosenWord?.word == wordleState.currentWord?.word) {
                    Log.d(WordleState.LOGGER_TAG, "Match!!")
                    setEditable(holder, wordleState.currentRow, false)
                    wordleState.setGameWon()
                    return@setOnKeyListener true
                }

                // If achieved the last row and didn't win - then lost
                if (wordleState.currentRow == WordleState.HEIGHT -1) {
                    setEditable(holder, wordleState.currentRow, false)
                    wordleState.setLostGame()
                    return@setOnKeyListener true
                }

                // Move the position to the next cell in the beginning of the row
                holder.letterView.clearFocus()
                // The enter event probably adds up a row...
                // 4 -> ((4 +1)/5 -1) * 5 = 0
                val nextCellIndex = ((position + 1) / WordleState.WIDTH - 1) * WordleState.WIDTH
                Log.d(WordleState.LOGGER_TAG, String.format("nextCellIndex %d", nextCellIndex))
                val nextCell = getHolderCellByIndex(holder, nextCellIndex)
                nextCell?.letterView?.requestFocus()
                nextCell?.letterView?.setSelection(0) // at the start of the text
                setEditable(holder, wordleState.currentRow, false)

                wordleState.currentRow += 1

                setEditable(holder, wordleState.currentRow, true)

                return@setOnKeyListener true // Consume the enter event
            }
            false
        }


    }

    override fun getItemCount() = cells.size

    private fun setEditable(holder: WordleViewHolder,rowNumber: Int, editable: Boolean) {
        val start = rowNumber * WordleState.WIDTH
        for (i in start..<(start + WordleState.WIDTH)) {
            getHolderCellByIndex(holder, i)?.letterView?.isEnabled = editable
        }
    }
}

