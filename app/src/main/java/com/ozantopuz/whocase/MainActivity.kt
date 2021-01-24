package com.ozantopuz.whocase

import DataSource
import FetchCompletionHandler
import FetchError
import FetchResponse
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.ozantopuz.whocase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var peopleAdapter: PeopleAdapter

    private val dataSource: DataSource by lazy { DataSource() }
    private var nextValue: String? = null

    private val recyclerViewOnScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (!recyclerView.canScrollVertically(1) && nextValue != null) {
                fetchData()
            }
        }
    }

    private val fetchCompletionHandler: FetchCompletionHandler = object : FetchCompletionHandler{
        override fun invoke(p1: FetchResponse?, p2: FetchError?) {
            binding.swipeRefreshLayout.isRefreshing = false
            binding.progressBar.visibility = View.INVISIBLE

            p1?.let { fetchResponse ->
                if (fetchResponse.people.isNullOrEmpty()){
                    showError(getString(R.string.response_is_empty),getString(R.string.try_again)) {
                        fetchData()
                    }
                }else{
                    loadData(fetchResponse)
                }
            }

            p2?.let { fetchError ->
                showError(fetchError.errorDescription, getString(R.string.try_again)) {
                    fetchData()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        initRecyclerView()

        fetchData()

        binding.swipeRefreshLayout.setOnRefreshListener {
            nextValue = null
            fetchData(false)
        }
    }

    private fun initRecyclerView(){
        peopleAdapter = PeopleAdapter()
        with(binding.recyclerView){
            adapter = peopleAdapter
            addOnScrollListener(recyclerViewOnScrollListener)
        }
    }

    private fun fetchData(isProgressBarVisible: Boolean = true){
        if (isProgressBarVisible){
            binding.progressBar.visibility = View.VISIBLE
        }

        dataSource.fetch(nextValue, fetchCompletionHandler)
    }

    private fun loadData(response: FetchResponse){
        with(response){
            if (nextValue == null){
                peopleAdapter.refreshList(people)
            }else {
                peopleAdapter.updateList(people)
            }

            nextValue = next

            if (next == null && peopleAdapter.itemCount > 0){
                showError(getString(R.string.end_of_the_list), getString(R.string.refresh_list)) {
                    binding.recyclerView.scrollToPosition(0)
                    fetchData()
                }
            }
        }
    }

    private fun showError(message: String, positiveButtonTitle: String, body: () -> Unit){
        val builder = AlertDialog.Builder(this)
        with(builder){
            setTitle(getString(R.string.alert_title))
            setMessage(message)

            setPositiveButton(positiveButtonTitle) { dialog, _ ->
                dialog.cancel()
                body.invoke()
            }

            setNegativeButton(getString(R.string.alert_negative_button_title)) { dialog, _ ->
                dialog.cancel()
            }

            show()
        }
    }
}