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
import com.example.wordle.business.Cell
import com.example.wordle.business.WordleState

// WordleAdapter.kt
class WordleAdapter(private val cells: List<Cell>, private val wordleState: WordleState) :
    RecyclerView.Adapter<WordleAdapter.WordleViewHolder>() {

    inner class WordleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val letterView: EditText = view.findViewById(R.id.cell_letter)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_item, parent, false)
        return WordleViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordleViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val cell = cells[holder.adapterPosition]

        // Set initial text and background color for the cell
        holder.letterView.text = null
        holder.letterView.setBackgroundColor(cell.color)

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
                            wordleState.currentWord = wordLine.toString()
                        }
                        // Shift the focus to the next cell in the row
                        else if (position < cells.size - 1 && (position + 1) % WordleState.WIDTH != 0) {
                            holder.letterView.clearFocus()
                            holder.itemView.rootView.findViewById<RecyclerView>(R.id.wordleGrid)
                                .findViewHolderForAdapterPosition(position + 1)?.itemView
                                ?.findViewById<EditText>(R.id.cell_letter)?.requestFocus()
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
                    val previousCell = holder.itemView.rootView.findViewById<RecyclerView>(R.id.wordleGrid)
                        .findViewHolderForAdapterPosition(position - 1)
                            as? WordleViewHolder
                    previousCell?.letterView?.requestFocus()
                    previousCell?.letterView?.setSelection(previousCell.letterView.text.length)
                    return@setOnKeyListener true // Consume the backspace event
                }
            }
            // Detect enter key in the end of the line
            else if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                // Move the position to the next cell in the beginning of the row
                holder.letterView.clearFocus()
                // The enter event probably adds up a row...
                // 4 -> ((4 +1)/5 -1) * 5 = 0
                val nextCellIndex = ((position + 1) / WordleState.WIDTH - 1) * WordleState.WIDTH
                Log.d(WordleState.LOGGER_TAG, String.format("nextCellIndex %d", nextCellIndex))
                if (nextCellIndex >= WordleState.WIDTH * WordleState.HEIGHT) {
                    return@setOnKeyListener true
                }

                // Check if the rowWord matches the chosenWord
                if (wordleState.chosenWord?.word == wordleState.currentWord) {
                    Log.d(WordleState.LOGGER_TAG, "Match!!")
                    return@setOnKeyListener true
                }
                val nextCell = holder.itemView.rootView.findViewById<RecyclerView>(R.id.wordleGrid)
                    .findViewHolderForAdapterPosition(nextCellIndex) as? WordleViewHolder

                nextCell?.letterView?.requestFocus()
                nextCell?.letterView?.setSelection(0) // at the start of the text

                return@setOnKeyListener true // Consume the enter event
            }
            false
        }
    }


    override fun getItemCount() = cells.size

}

