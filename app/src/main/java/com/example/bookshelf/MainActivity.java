package com.example.bookshelf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;

public class MainActivity extends AppCompatActivity implements BookListFragment.BookSelectedInterface {

    private static final int SEARCH_ACTIVITY_REQUEST_CODE = 0;

    FragmentManager fm;

    Button searchButton;
    BookList bookList = new BookList();

    boolean twoPane;
    BookListFragment bookListFragment = BookListFragment.newInstance(bookList);
    BookDetailsFragment bookDetailsFragment;
    Book selectedBook;
    private final String KEY_SELECTED_BOOK = "selectedBook";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchButton = findViewById(R.id.searchButton);

        //Fetch selected book if there was one
        if (savedInstanceState != null)
            selectedBook = savedInstanceState.getParcelable(KEY_SELECTED_BOOK);

        twoPane = findViewById(R.id.container2) != null;

        fm = getSupportFragmentManager();

        Fragment fragment1;
        fragment1 = fm.findFragmentById(R.id.container1);


        // At this point, I only want to have BookListFragment be displayed in container_1
        if (fragment1 instanceof BookDetailsFragment) {
            fm.popBackStack();
        } else if (!(fragment1 instanceof BookListFragment))
            fm.beginTransaction()
                    .add(R.id.container1, bookListFragment)
            .commit();

        /*
        If we have two containers available, load a single instance
        of BookDetailsFragment to display all selected books
         */
        bookDetailsFragment = (selectedBook == null) ? new BookDetailsFragment() : BookDetailsFragment.newInstance(selectedBook);
        if (twoPane) {
            fm.beginTransaction()
                    .replace(R.id.container2, bookDetailsFragment)
                    .commit();
        } else if (selectedBook != null) {
            /*
            If a book was selected, and we now have a single container, replace
            BookListFragment with BookDetailsFragment, making the transaction reversible
             */
            fm.beginTransaction()
                    .replace(R.id.container1, bookDetailsFragment)
                    .addToBackStack(null)
                    .commit();
        }

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchSearchIntent = new Intent(MainActivity.this, BookSearchActivity.class);
                startActivityForResult(launchSearchIntent, SEARCH_ACTIVITY_REQUEST_CODE);
            }
        });

    }

    /*
    Generate an arbitrary list of "books" for testing

    private BookList getTestBooks() {
        BookList books = new BookList();
        Book book;
        for (int i = 1; i <= 10; i++) {
            books.add(book = new Book("Book" + i, "Author" + i));
        }
        return books;
    };
    */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SEARCH_ACTIVITY_REQUEST_CODE){
            System.out.println("Request Code working");
            if (resultCode == RESULT_OK) {
                BookList searched = data.getParcelableExtra("SearchedBook");
                System.out.println(searched.get(0).getAuthor());

                for(int i = 0; i < searched.size(); i++){
                    bookList.add(searched.get(i));
                }
                bookListFragment.update();
            }else{
                System.out.println("result Code not working");
            }
        }else{
            System.out.println("Request Code not working");
        }
    }

    @Override
    public void bookSelected(int index) {
        //Store the selected book to use later if activity restarts
        selectedBook = bookList.get(index);

        if (twoPane)
            /*
            Display selected book using previously attached fragment
             */
            bookDetailsFragment.displayBook(selectedBook);
        else {
            /*
            Display book using new fragment
             */
            fm.beginTransaction()
                    .replace(R.id.container1, BookDetailsFragment.newInstance(selectedBook))
                    // Transaction is reversible
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(KEY_SELECTED_BOOK, selectedBook);
    }

    @Override
    public void onBackPressed() {
        // If the user hits the back button, clear the selected book
        selectedBook = null;
        super.onBackPressed();
    }
}
