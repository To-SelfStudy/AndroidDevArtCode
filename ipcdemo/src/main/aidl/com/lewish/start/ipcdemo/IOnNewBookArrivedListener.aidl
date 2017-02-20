// IOnNewBookArrivedListener.aidl
package com.lewish.start.ipcdemo;
import com.lewish.start.ipcdemo.entity.Book;
// Declare any non-default types here with import statements

interface IOnNewBookArrivedListener {
    void onNewBookArrived(in Book book);
}
