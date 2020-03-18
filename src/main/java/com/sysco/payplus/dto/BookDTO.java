package com.sysco.payplus.dto;

import com.sysco.payplus.entity.Book;
import com.sysco.payplus.validators.annotations.ISBNFormat;
import org.modelmapper.ModelMapper;

import javax.validation.constraints.NotBlank;
import java.util.Objects;
/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
public class BookDTO {

    private Long id;
    @NotBlank(message = "Name is mandatory")
    private String name;
    @ISBNFormat(message = "Not a valid ISBN format")
    private String isbn;
    @NotBlank(message = "Author is mandatory")
    private String author;

    public BookDTO() {
    }

    public static BookDTO toBookDTO(Book book) {
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(book, BookDTO.class);

    }

    public BookDTO(String name, String isbn, String author) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BookDTO book = (BookDTO) o;
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
