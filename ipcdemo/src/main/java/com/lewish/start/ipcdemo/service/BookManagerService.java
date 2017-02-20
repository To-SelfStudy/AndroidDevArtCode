package com.lewish.start.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;

import com.lewish.start.ipcdemo.IBookManager;
import com.lewish.start.ipcdemo.entity.Book;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";
    private CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();
    private Binder binder = new IBookManager.Stub(){
        BookManagerService bookManagerService = BookManagerService.this;
        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            bookList.add(book);
        }
    };
    public BookManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bookList.add(new Book(1,"高数"));
        bookList.add(new Book(2,"大学物理"));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }
}
