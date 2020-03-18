package com.sysco.payplus.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysco.payplus.dto.BookDTO;
import com.sysco.payplus.entity.Book;
import com.sysco.payplus.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookController.class)
@EnableAutoConfiguration
class BookControllerTest {

    private final String API_PATH = "/api-blueprint/v1";
    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookService bookService;
    @Autowired
    private BookController bookController;

    @Test
    public void contexLoads() throws Exception {
        assertThat(bookController).isNotNull();
    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void whenSavingUpdatedBook_returnsUpdatedBook()
            throws Exception {
        BookDTO bookDTO = new BookDTO("Life in Jail", "ISBN 978-0-596-52068-7", "Rohana Kumara");
        String bookJson = new ObjectMapper().writeValueAsString(bookDTO);
        Book bookToBeSSaved = new Book(bookDTO.getName(), bookDTO.getIsbn(), bookDTO.getAuthor());
        Book bookSaved = new Book(bookDTO.getName(), bookDTO.getIsbn(), bookDTO.getAuthor());
        bookSaved.setId(100L);
        //mock the expected response from the service
        when(bookService.save(bookToBeSSaved)).thenReturn(bookSaved);
        mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/book")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
        bookSaved.setName("Life in a Jail");
        String savedBookJson = new ObjectMapper().writeValueAsString(bookSaved);
        //mock the expected response from the service
        when(bookService.save(bookSaved)).thenReturn(bookSaved);
        mvc.perform(MockMvcRequestBuilders.put(API_PATH + "/book")
                .content(savedBookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(bookSaved.getName())))
                .andExpect(jsonPath("$.isbn", is(bookSaved.getIsbn())))
                .andExpect(jsonPath("$.id", is(bookSaved.getId().intValue())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void whenSavingNewBook_returnsSavedBookWithId()
            throws Exception {
        BookDTO bookDTO = new BookDTO("Life in Jail", "ISBN 978-0-596-52068-7", "Rohana Kumara");
        String bookJson = new ObjectMapper().writeValueAsString(bookDTO);
        Book bookToBeSaved = new Book(bookDTO.getName(), bookDTO.getIsbn(), bookDTO.getAuthor());
        Book bookSaved = new Book(bookDTO.getName(), bookDTO.getIsbn(), bookDTO.getAuthor());
        bookSaved.setId(100L);
        //mock the expected response from the service
        when(bookService.save(bookToBeSaved)).thenReturn(bookSaved);
        mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/book")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name", is(bookSaved.getName())))
                .andExpect(jsonPath("$.isbn", is(bookSaved.getIsbn())))
                .andExpect(jsonPath("$.id", is(bookSaved.getId().intValue())))
                .andDo(print());
    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void whenSavingInvalidBook_returnsErrors()
            throws Exception {
        BookDTO bookDTO = new BookDTO();
        String bookJson = new ObjectMapper().writeValueAsString(bookDTO);
        //no need to mock as the call does not pass the validation phase
        mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/book")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.author", is("Author is mandatory")))
                .andExpect(jsonPath("$.data.isbn", is("Not a valid ISBN format")))
                .andExpect(jsonPath("$.data.name", is("Name is mandatory")))
                .andDo(print());

    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void whenSavingBookWithIncorrectISBN_returnsCustomErrors()
            throws Exception {
        Book book = new Book("Life in Jail", "isbn1234", "Rohana Kumara");
        String bookJson = new ObjectMapper().writeValueAsString(book);

        mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/book")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.isbn", is("Not a valid ISBN format")))
                .andDo(print());
    }

    @Test
    @WithMockUser(username="admin",roles={"USER","ADMIN"})
    public void whenInternalServerError_returnsInternalServerError() throws Exception {
        Book book = new Book("Life in Jail", "ISBN 978-0-596-52068-7", "Rohana Kumara");
        //mock the expected response from the service
        when(bookService.save(book)).thenThrow(new RuntimeException("Server error"));
        String bookJson = new ObjectMapper().writeValueAsString(book);
        mvc.perform(MockMvcRequestBuilders.post(API_PATH + "/book")
                .content(bookJson)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.data", is("Service team has been informed of the internal service failure")))
                .andDo(print());
    }

}