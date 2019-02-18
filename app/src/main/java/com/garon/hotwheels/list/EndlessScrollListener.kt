package com.garon.hotwheels.list

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.concurrent.atomic.AtomicBoolean

class EndlessScrollListener (
    private val layoutManager: LinearLayoutManager,
    private val loadMoreListener: LoadMoreListener,
    private val loading: AtomicBoolean = AtomicBoolean(false)
) : RecyclerView.OnScrollListener() {

    private companion object {
        const val ITEM_THRESHOLD = 3
    }

    private var totalItemCount: Int = 0
    private var lastVisibleItem: Int = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        totalItemCount = layoutManager.itemCount
        lastVisibleItem = layoutManager.findLastVisibleItemPosition()
        val nextCount = lastVisibleItem + ITEM_THRESHOLD

        if (totalItemCount <= nextCount && !loading.getAndSet(true)) {
            loadMoreListener.invoke()
        }
    }
}

typealias LoadMoreListener = () -> Unit
