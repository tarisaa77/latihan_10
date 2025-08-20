package ac.id.latihan_10

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MainActivity : AppCompatActivity() {
    private lateinit var rcv : RecyclerView
    private lateinit var fab : FloatingActionButton
    private lateinit var adapter : NewsAdapter
    private var newsList = mutableListOf<News>()
    private var db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rcv = findViewById(R.id.rcvNews)
        rcv.layoutManager = LinearLayoutManager(this)

        fab = findViewById(R.id.floatAddNews)
        fab.setOnClickListener{
            val intent = Intent (this,AddEditNewsActivity::class.java)
            startActivity(intent)
        }
        fetchNews()
    }
    fun fetchNews(){
        db.collection("news")
            .get()
            .addOnSuccessListener { documentReference ->
                newsList.clear()
                for (document in documentReference){
                    val news = document.toObject(News::class.java)
                    news.id = document.id
                    newsList.add(news)
                }

                adapter = NewsAdapter(newsList,
                    onItemClick = { selectedNews->
                        val options = arrayOf("Lihat", "Edit")
                        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
                        builder.setTitle("Pilih Aksi")
                        builder.setItems(options) { _, which ->
                            when (which) {
                                0 -> { // Lihat
                                    val intent = Intent(this, DetailNewsActivity::class.java)
                                    intent.putExtra("news", selectedNews)
                                    startActivity(intent)
                                }
                                1 -> { // Edit
                                    val intent = Intent(this, AddEditNewsActivity::class.java)
                                    intent.putExtra("news", selectedNews)
                                    startActivity(intent)
                                }
                            }
                        }
                        builder.show()

                    },
                    onDeleteClick = {selectedNews->
                        selectedNews.id?.let { docId ->
                            db.collection("news").document(docId)
                                .delete()
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Berhasil dihapus", Toast.LENGTH_SHORT).show()
                                    fetchNews()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(this, "Gagal menghapus", Toast.LENGTH_SHORT).show()
                                }
                        }

                    },
                    )
                adapter.notifyDataSetChanged()
                rcv.adapter = adapter
            }
            .addOnFailureListener { e -> }

    }

    override fun onResume(){
        super.onResume()
        fetchNews()
    }

    override fun onStart() {
        super.onStart()
        fetchNews()
    }
}