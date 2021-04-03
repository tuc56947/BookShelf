package com.example.bookshelf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class BookSearchActivity extends AppCompatActivity {

    EditText searchEditText;
    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);

        searchEditText = findViewById(R.id.editTextSearch);
        searchButton = findViewById(R.id.button);

        final RequestQueue requestQueue = Volley.newRequestQueue(this);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = searchEditText.getText().toString();
                String searchUrl = "https://kamorris.com/lab/cis3515/search.php?term=" + searchTerm;
                System.out.println("Calling " + searchUrl);

                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                        Request.Method.GET,
                        searchUrl,
                        (JSONArray) null,
                        new Response.Listener<JSONArray>() {
                            BookList list = new BookList();
                            @Override
                            public void onResponse(JSONArray response) {
                                for(int i = 0; i < response.length(); i++){
                                    try{
                                        list.add(
                                                new Book(
                                                        response.getJSONObject(i).getString("title"),
                                                        response.getJSONObject(i).getString("author")
                                                )
                                        );
                                    }catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                Intent bookData = new Intent();
                                bookData.putExtra("SearchedBook", list);
                                setResult(RESULT_OK, bookData);

                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                System.out.println("Error: " + error.toString());
                            }
                        }
                );
                requestQueue.add(jsonArrayRequest);
            }
        });
    }


}