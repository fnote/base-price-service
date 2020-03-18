package com.sysco.payplus.controller;

import com.sysco.payplus.dto.BookDTO;
import com.sysco.payplus.entity.Book;
import com.sysco.payplus.service.BookService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

/**
 * Created by IntelliJ IDEA.
 * Author: rohana.kumara@sysco.com
 * Date: 3/13/20
 * Time: 12:54 PM
 */
@RestController
@RequestMapping("/api-blueprint")
@Slf4j
class BookController {
    Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    private BookService bookService;
    @GetMapping("/v1/books/{id}")
    public @ResponseBody
    Optional<Book> get(@PathVariable("id") Long id) {
        return bookService.findById(id);
    }

    @PostMapping("v1/book")
    @ApiOperation(value = "Creates a new book", notes = "Returns the newly created Book with its auto assigned Id", code = 201, response = Book.class)
    /*@ApiResponses(value = {
            @ApiResponse(code = 500, message = "System error", response = ExceptionResponse.class),
            @ApiResponse(code = 404, message = "List is not found",response = ExceptionResponse.class),
            @ApiResponse(code = 400, message = "Bad request", response = ExceptionResponse.class)
    })*/
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody
    BookDTO saveBook(@Valid @RequestBody(required = true) BookDTO bookDTO) {
        logger.trace("Entered the saveBook handler");
        return BookDTO.toBookDTO((bookService.save(Book.toBook(bookDTO))));
    }

    @PutMapping(value = "/v1/book")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO updateBook(@Valid @RequestBody BookDTO bookDTO) {
        logger.trace("Entered the updateBook handler");
        return BookDTO.toBookDTO(bookService.save(Book.toBook(bookDTO)));
    }

    @PutMapping(value = "/v2/book")
    @ResponseStatus(HttpStatus.OK)
    public BookDTO updateBookV2(@Valid @RequestBody BookDTO bookDTO) {
        logger.trace("Entered the updateBook handler V2");
        return  BookDTO.toBookDTO(bookService.save(Book.toBook((bookDTO))));
    }
}
