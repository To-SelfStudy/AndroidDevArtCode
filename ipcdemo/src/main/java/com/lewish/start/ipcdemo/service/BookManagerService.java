package com.lewish.start.ipcdemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.lewish.start.ipcdemo.IBookManager;
import com.lewish.start.ipcdemo.IOnNewBookArrivedListener;
import com.lewish.start.ipcdemo.entity.Book;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class BookManagerService extends Service {
    private static final String TAG = "BookManagerService";
    private CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();
    private RemoteCallbackList<IOnNewBookArrivedListener> newBookArrivedObservers = new RemoteCallbackList<>();
    private AtomicBoolean isBookManagerServiceDestroyed = new AtomicBoolean(false);
    private Binder binder = new IBookManager.Stub() {
        BookManagerService bookManagerService = BookManagerService.this;

        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            bookList.add(book);
        }

        @Override
        public void registerOnBookArrivedListener(IOnNewBookArrivedListener iListener) throws RemoteException {
            newBookArrivedObservers.register(iListener);
            int N = newBookArrivedObservers.beginBroadcast();
            newBookArrivedObservers.finishBroadcast();
            Log.d(TAG, "registerOnBookArrivedListener:  current size="+N);
        }

        @Override
        public void unRegisterOnBookArrivedListener(IOnNewBookArrivedListener iListener) throws RemoteException {
            boolean isSuccess = newBookArrivedObservers.unregister(iListener);
            if(isSuccess) {
                Log.d(TAG, "unRegisterOnBookArrivedListener: success");
            }else {
                Log.d(TAG, "unRegisterOnBookArrivedListener: not found, can not unregister");
            }
            final int N = newBookArrivedObservers.beginBroadcast();
            newBookArrivedObservers.finishBroadcast();
            Log.d(TAG, "unregisterListener, current size:" + N);
        }
    };

    public BookManagerService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bookList.add(new Book(1, "高数"));
        bookList.add(new Book(2, "大学物理"));
        new Thread(new ServiceWorker()).start();
    }

    @Override
    public void onDestroy() {
        isBookManagerServiceDestroyed.set(true);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    private class ServiceWorker implements Runnable{
        @Override
        public void run() {
            while (!isBookManagerServiceDestroyed.get()){
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int newBookId = bookList.size() + 1;
                Book newBook = new Book(newBookId-3, "newBook" + (newBookId - 3));
                bookList.add(newBook);
                notifyObservers(newBook);
            }
        }
    }

    private void notifyObservers(Book newBook) {
        int N = newBookArrivedObservers.beginBroadcast();
        for (int i=0;i<N;i++){
            try {
                IOnNewBookArrivedListener iOnNewBookArrivedListener = newBookArrivedObservers.getBroadcastItem(i);
                Log.d(TAG, "run: IOnNewBookArrivedListener="+iOnNewBookArrivedListener);
                if(iOnNewBookArrivedListener!=null) {
                    iOnNewBookArrivedListener.onNewBookArrived(newBook);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        newBookArrivedObservers.finishBroadcast();
    }
}
