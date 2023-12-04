package group10.com.guesstheera.mainview

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import group10.com.guesstheera.R
import group10.com.guesstheera.backend.Score

class ScoreListAdapter(private val context: Context,
                       private var scoreList: List<Score>):
    BaseAdapter() {

    fun replace(list: List<Score>) {
        scoreList = list
    }

    override fun getCount(): Int {
        return scoreList.size
    }

    override fun getItem(position: Int): Score {
        return scoreList.get(position)
    }

    override fun getItemId(position: Int): Long {
        // we currently don't have int-id's
        // thanks, firebase
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = View.inflate(context, R.layout.layout_leaderboard_entry, null)
        val item = getItem(position)
        view.findViewById<TextView>(R.id.username_text).text = item.user_id
        view.findViewById<TextView>(R.id.wins_text).text = item.wins.toString()
        return view
    }
}