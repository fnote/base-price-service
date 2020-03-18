package com.sysco.payplus.service;

import com.sysco.payplus.entity.Book;
import com.sysco.payplus.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;
/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
@Service
public class BookServiceImpl implements BookService {
    @Autowired
    BookRepository bookRepository;

    @Override
    @Secured ({"ROLE_USER", "ROLE_ADMIN"})
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Override
    @Transactional
    @Secured({"ROLE_ADMIN"})
    public Book save(Book book) {
          return bookRepository.save(book);
    }
}
