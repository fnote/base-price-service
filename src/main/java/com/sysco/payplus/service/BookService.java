package com.sysco.payplus.service;

import com.sysco.payplus.entity.Book;

import java.util.Optional;
/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */

public interface BookService {
    Optional<Book> findById(Long id);
    Book save(Book book);
}
