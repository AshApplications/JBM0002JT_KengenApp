package com.example.matrix

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.example.matrix.databinding.ItemBoxBinding

class MyAdapter(
    private val context: Context,
    private val matrix: Array<IntArray>,
    private val row: Int,
    private val col: Int,
    private val height: Int
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var isMultipleBlink = false
    private var x = 0
    private var y = 0
    private var selectedPosition: HashMap<Int, Int> = HashMap()
    private var animation = AnimationUtils.loadAnimation(context, R.anim.anim_blink)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(ItemBoxBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return row * col
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            if (y != 0 && position % col == 0) {
                x++
                y = 0
            }
            if (selectedPosition[position] == position) {
                holder.binding.tvText.startAnimation(animation)
            } else {
                holder.binding.tvText.clearAnimation()
            }
            holder.binding.tvText.text = matrix[x][y].toString()
            holder.binding.tvText.setOnClickListener {
                if (selectedPosition[position] == position) {
                    selectedPosition.remove(position)
                    x = 0
                    y = 0
                    notifyDataSetChanged()
                    return@setOnClickListener
                }
                x = 0
                y = 0
                if (!isMultipleBlink) {
                    selectedPosition.clear()
                }
                selectedPosition[position] = position
                notifyDataSetChanged()
                return@setOnClickListener
            }
            y++
        }
    }

    fun setMultipleBlink(isMultipleBlink: Boolean) {
        this.isMultipleBlink = isMultipleBlink
    }

    inner class ViewHolder(val binding: ItemBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.tvText.layoutParams.height = height / row
        }
    }
}