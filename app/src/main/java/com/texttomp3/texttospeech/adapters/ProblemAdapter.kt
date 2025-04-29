package com.texttomp3.texttospeech.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.texttomp3.texttospeech.R
import com.texttomp3.texttospeech.base.BaseRecyclerView
import com.texttomp3.texttospeech.data.models.Problem
import com.texttomp3.texttospeech.databinding.ItemProblemBinding

class ProblemAdapter(private val listener: OnClickListener) : BaseRecyclerView<ItemProblemBinding, Problem>() {
    override fun getItemLayout(inflater: LayoutInflater, parent: ViewGroup): ItemProblemBinding {
        return ItemProblemBinding.inflate(inflater, parent, false)
    }

    override fun areContentsTheSame(oldItem: Problem, newItem: Problem): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(oldItem: Problem, newItem: Problem): Boolean {
        return oldItem.text == newItem.text
    }

    override fun setData(binding: ItemProblemBinding, item: Problem, layoutPosition: Int) {
        with(binding) {
            tvProblem.text = item.text

            if (item.isSelection) {
                clItem.setBackgroundResource(R.drawable.bg_problem_active)
                tvProblem.setTextColor(binding.root.context.getColor(R.color.white))
            } else {
                clItem.setBackgroundResource(R.drawable.bg_problem_unactive)
                tvProblem.setTextColor(binding.root.context.getColor(R.color.grey_text_main))
            }

            clItem.setOnClickListener {
                listener.onClick(item)
            }
        }
    }

    interface OnClickListener {
        fun onClick(item: Problem)
    }
}