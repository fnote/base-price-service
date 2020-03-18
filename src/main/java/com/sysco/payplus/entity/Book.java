package com.sysco.payplus.entity;


import com.sysco.payplus.dto.BookDTO;
import com.sysco.payplus.validators.annotations.ISBNFormat;
import org.modelmapper.ModelMapper;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
@Entity
@Table(name = "book")
public class Book implements AuditableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", length = 45, nullable = false, unique = false)
    @NotBlank(message = "Name is mandatory")
    private String name;
    @Column(name = "isbn", length = 45, nullable = false, unique = true)
    @ISBNFormat(message = "Not a valid ISBN format")
    private String isbn;
    @Column(name = "author", length = 45, nullable = false, unique = false)
    @NotBlank(message = "Author is mandatory")
    private String author;
    @Column(name = "updated_by", length = 45, nullable = false, unique = false)
    private String updatedBy;
    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    public Book() {
    }

    public static Book toBook(BookDTO bookDTO) {
        ModelMapper modelMapper=new ModelMapper();
        return modelMapper.map(bookDTO,Book.class);

    }
    public Book(String name, String isbn, String author) {
        this.name = name;
        this.isbn = isbn;
        this.author = author;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return Objects.equals(id, book.id) &&
                Objects.equals(name, book.name) &&
                Objects.equals(isbn, book.isbn) &&
                Objects.equals(author, book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, isbn, author);
    }
}
