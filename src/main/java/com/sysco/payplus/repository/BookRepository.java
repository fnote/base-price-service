package com.sysco.payplus.repository;

import com.sysco.payplus.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
//todo: Can we add bare sql support
//todo: Can we add dynamic queries
//todo: support of NoSql
//todo: Can we have routig data source for read and write

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "SELECT b FROM Book b WHERE b.author = :author and b.name = :name", nativeQuery = false)
    List<Book> findBooksByAuthorAndName(
            @Param("author") String author, @Param("name") String name);

}