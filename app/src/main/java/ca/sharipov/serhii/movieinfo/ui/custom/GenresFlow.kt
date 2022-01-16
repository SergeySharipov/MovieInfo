package ca.sharipov.serhii.movieinfo.ui.custom

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.helper.widget.Flow
import ca.sharipov.serhii.movieinfo.models.Genre


class GenresFlow(context: Context, attrs: AttributeSet?) : Flow(context, attrs) {

    fun setup(
        parentView: ViewGroup,
        genres: List<Genre>?
    ) {
        parentView.removeViews(1, parentView.childCount - 1)

        if (genres != null) {
            val referencedIds = IntArray(genres.size)

            for (i in genres.indices) {
                val cardView = createCardViewWithText(context, genres[i].name)
                cardView.id = View.generateViewId()

                parentView.addView(cardView)
                referencedIds[i] = cardView.id
            }
            this.referencedIds = referencedIds
        }
    }

    private fun createCardViewWithText(context: Context, text: String?): CardView {
        val cardView = CardView(context)
        val textView = TextView(context)

        textView.text = text

        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        val margin = getSizeInDp(3)
        params.setMargins(margin, margin, margin, margin)
        textView.layoutParams = params

        cardView.radius = getSizeInDp(10).toFloat()
        cardView.addView(textView)
        return cardView
    }

    private fun getSizeInDp(sizeInDP: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, sizeInDP.toFloat(), resources
                .displayMetrics
        ).toInt()
    }
}