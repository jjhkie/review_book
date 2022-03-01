package com.work.review_book

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.work.review_book.adapter.BookAdapter
import com.work.review_book.adapter.HistoryAdapter
import com.work.review_book.api.BookService
import com.work.review_book.databinding.ActivityMainBinding
import com.work.review_book.model.BestSellerDto
import com.work.review_book.model.History
import com.work.review_book.model.SearchBookDto
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: BookAdapter
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var bookService: BookService

    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        initBookRecyclerView()
        initHistoryRecyclerView()

        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "BookSearchDB"
        ).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://book.interpark.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        bookService = retrofit.create(BookService::class.java)

        bookService.getBestSellerBooks(getString(R.string.interparkAPIKey))
            .enqueue(object : Callback<BestSellerDto> {
                //enqueue 비동기 실행
                //응답이 성공일 때
                override fun onResponse(
                    call: Call<BestSellerDto>,
                    response: Response<BestSellerDto>
                ) {
                    //TODO 성공처리


                    if (response.isSuccessful.not()) {
                        Log.d(TAG, "NOT!!SUCCESS")
                        return
                    }
                    //body()에는 bestSellerDto 형식이 담겨있다.
                    response.body()?.let {
                        Log.d(TAG, it.toString())

                        it.books.forEach { book ->
                            Log.d(TAG, book.toString())
                        }
                        adapter.submitList(it.books)
                    }
                }

                //응답이 실패일 때
                override fun onFailure(call: Call<BestSellerDto>, t: Throwable) {
                    //TODO 실패처리
                    Log.d(TAG, t.toString())
                }

            })

        //검색 기능 만들기
        binding.searchEditText.setOnKeyListener { view, keyCode, keyEvent ->
            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.action == MotionEvent.ACTION_DOWN) {
                search(binding.searchEditText.text.toString())
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

    }

    private fun search(keyword: String) {
        bookService.getBooksByName(getString(R.string.interparkAPIKey), keyword)
            .enqueue(object : Callback<SearchBookDto> {
                //응답이 성공일 때
                override fun onResponse(
                    call: Call<SearchBookDto>,
                    response: Response<SearchBookDto>
                ) {
                    //TODO 성공처리
                    saveSearchKeyword(keyword)

                    if (response.isSuccessful.not()) {
                        Log.d(TAG, "NOT!!SUCCESS")
                        return
                    }

                    adapter.submitList(response.body()?.books.orEmpty())
                }

                //응답이 실패일 때
                override fun onFailure(call: Call<SearchBookDto>, t: Throwable) {
                    //TODO 실패처리
                    Log.d(TAG, t.toString())
                }

            })
    }

    private fun initBookRecyclerView() {
        adapter = BookAdapter()

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.bookRecyclerView.adapter = adapter

    }

    private fun initHistoryRecyclerView(){
        historyAdapter = HistoryAdapter(historyDeleteClickedListener = {
            deleteSearchKeyword(it)
        })
    }

    //hisotyRecycler visibility
    private fun showHistoryView(){
        Thread{
            val keywords = db.historyDao().getAll().reversed()
        }

        binding.historyRecyclerView.isVisible =true
    }
    private fun hideHistoryView(){

        binding.historyRecyclerView.isVisible =false
    }

    private fun saveSearchKeyword(keyword: String){
        Thread{
            db.historyDao().insertHistory(History(null,keyword))
        }.start()
    }

    private fun deleteSearchKeyword(keyword: String){
        Thread{
            db.historyDao().delete(keyword)
            //TODO view 갱신
        }
    }

    companion object {
        private const val TAG = "Main"
    }
}