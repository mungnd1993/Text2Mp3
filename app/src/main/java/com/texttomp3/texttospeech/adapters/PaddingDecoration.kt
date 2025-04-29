package com.texttomp3.texttospeech.adapters

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager

class PaddingDecoration(
    private val space: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val layoutManager = parent.layoutManager
        if (layoutManager is FlexboxLayoutManager) {
            val lp = view.layoutParams as FlexboxLayoutManager.LayoutParams
            outRect.bottom = space
            outRect.right = if (lp.isWrapBefore) 0 else space
        }
    }
}
