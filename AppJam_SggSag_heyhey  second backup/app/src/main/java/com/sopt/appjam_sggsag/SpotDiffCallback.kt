package com.sopt.appjam_sggsag

import android.support.v7.util.DiffUtil
import com.sopt.appjam_sggsag.Data.DetailPosterData


class SpotDiffCallback(
    private val old: List<DetailPosterData>,
    private val new: List<DetailPosterData>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].posterIdx == new[newPosition].posterIdx
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}
