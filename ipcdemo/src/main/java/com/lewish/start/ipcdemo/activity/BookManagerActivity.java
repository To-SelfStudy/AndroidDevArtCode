package com.lewish.start.ipcdemo.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.lewish.start.ipcdemo.IBookManager;
import com.lewish.start.ipcdemo.IOnNewBookArrivedListener;
import com.lewish.start.ipcdemo.R;
import com.lewish.start.ipcdemo.entity.Book;
import com.lewish.start.ipcdemo.service.BookManagerService;

import java.util.List;

public class BookManagerActivity extends AppCompatActivity {
    private static final String TAG = "BookManagerActivity";
    private IOnNewBookArrivedListener iOnNewBookArrivedListener = new IOnNewBookArrivedListener.Stub(){
        @Override
        public void onNewBookArrived(final Book book) throws RemoteException {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(BookManagerActivity.this, book.bookId+"  "+book.bookName, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            bookManager = IBookManager.Stub.asInterface(iBinder);

            try {
                bookManager.addBook(new Book(0,"语文"));
                List<Book> bookList = bookManager.getBookList();
                for (int i=0;i<bookList.size();i++){
                    Book book = bookList.get(i);
                    Log.d(TAG, "BookId="+book.bookId+",BookName="+book.bookName);
                }
                bookManager.registerOnBookArrivedListener(iOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    private IBookManager bookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_manager);
        Intent intent = new Intent(this, BookManagerService.class);
        bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onDestroy() {
        if(bookManager!=null&&bookManager.asBinder().isBinderAlive()) {
            try {
                bookManager.unRegisterOnBookArrivedListener(iOnNewBookArrivedListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        unbindService(serviceConnection);
        super.onDestroy();
    }
}
