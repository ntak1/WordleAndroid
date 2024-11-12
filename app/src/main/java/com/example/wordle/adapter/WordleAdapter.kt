import android.annotation.SuppressLint
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.RecyclerView
import com.example.wordle.R
import com.example.wordle.business.Cell

// WordleAdapter.kt
class WordleAdapter(private val cells: List<Cell>) :
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
                        // Move focus to the next cell if within bounds
                        if (position < cells.size - 1) {
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

        // Detect backspace key when the cell is empty
        holder.letterView.setOnKeyListener { _, keyCode, event ->
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
            false
        }
    }


    override fun getItemCount() = cells.size

}

