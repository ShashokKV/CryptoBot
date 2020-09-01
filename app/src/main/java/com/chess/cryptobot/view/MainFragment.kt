package com.chess.cryptobot.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import com.chess.cryptobot.R
import com.chess.cryptobot.content.ContextHolder
import com.chess.cryptobot.model.ViewItem
import com.chess.cryptobot.view.adapter.RecyclerViewAdapter

abstract class MainFragment<T : RecyclerView.ViewHolder> : Fragment(), OnRefreshListener {
    private var adapter: RecyclerViewAdapter<T>? = null
    lateinit var holder: ContextHolder
        private set

    private var swipeRefreshLayout: SwipeRefreshLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = initFragmentView(inflater, container)
        holder = initHolder()
        val recyclerView = initRecyclerView(view)
        adapter = initAdapter(holder)
        recyclerView.adapter = adapter
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
        val dividerItemDecoration = DividerItemDecoration(recyclerView.context,
                layoutManager.orientation)
        dividerItemDecoration.setDrawable(ResourcesCompat.getDrawable(resources, R.drawable.divider, null)!!)
        recyclerView.addItemDecoration(dividerItemDecoration)
        swipeRefreshLayout = initSwipeRefresh(view)
        swipeRefreshLayout!!.setOnRefreshListener(this)
        swipeRefreshLayout!!.setColorSchemeResources(R.color.colorSecondary, R.color.colorSecondaryDark)
        return view
    }

    override fun onRefresh() {
        beforeRefresh()
        swipeRefreshLayout!!.isRefreshing = false
        holder.updateAllItems()
    }

    protected abstract fun beforeRefresh()
    protected abstract fun initFragmentView(inflater: LayoutInflater, container: ViewGroup?): View
    protected abstract fun initHolder(): ContextHolder
    protected abstract fun initRecyclerView(view: View): RecyclerView
    protected abstract fun initAdapter(holder: ContextHolder?): RecyclerViewAdapter<T>
    protected abstract fun initSwipeRefresh(view: View): SwipeRefreshLayout
    fun addItem() {
        adapter!!.notifyItemInserted()
    }

    fun updateItem(item: ViewItem?) {
        adapter!!.updateItem(item)
    }

    fun deleteItemByPosition(position: Int) {
        adapter!!.deleteItem(position)
    }

    fun itemNameByPosition(position: Int): String {
        return adapter!!.itemNameByPosition(position)
    }

    fun updateAllItems() {
        adapter!!.notifyDataSetChanged()
    }

    fun makeToast(message: String?) {
        if (message != null && message.isNotEmpty()) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }
    }

    fun showSpinner() {
        val activity = activity ?: return
        val spinner = activity.findViewById<ProgressBar>(R.id.progressBar)
        spinner.visibility = View.VISIBLE
    }

    fun hideSpinner() {
        val activity = activity ?: return
        val spinner = activity.findViewById<ProgressBar>(R.id.progressBar)
        spinner.visibility = View.GONE
    }

}