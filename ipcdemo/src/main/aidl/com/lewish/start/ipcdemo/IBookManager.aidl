// IBookManager.aidl
package com.lewish.start.ipcdemo;
import com.lewish.start.ipcdemo.entity.Book;
// Declare any non-default types here with import statements

interface IBookManager {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    List<Book> getBookList();
    void addBook(in Book book);
}
