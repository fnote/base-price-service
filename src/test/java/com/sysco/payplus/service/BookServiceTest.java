package com.sysco.payplus.service;

import com.sysco.payplus.entity.Book;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
class BookServiceTest {

    @Autowired
    BookService bookService;

    @Test
    @WithMockUser(username="user",roles={"USER"})
    public void givenUnauthenticated_whenCallService_thenThrowsException() {
        assertThrows(AccessDeniedException.class, () -> {
            Book bookToCreate=new Book("Back door deals","ISBN 978-0-596-53568-7","Sisil Gamage");
            Book bookSaved=bookService.save(bookToCreate);
        });

    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"})
    public void givenAuthenticated_whenCallServiceWithSecured_thenOk() {
        Book bookToCreate=new Book("Back door deals","ISBN 978-0-596-54068-7","Sisil Gamage");
        Book bookSaved=bookService.save(bookToCreate);
        assertNotNull(bookSaved.getId());
    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"})
    public void whenCalledSave_thenBook(){
        Book bookToCreate=new Book("Back door deals","ISBN 978-0-566-52068-7","Sisil Gamage");
        Book bookSaved=bookService.save(bookToCreate);
        assertEquals(bookToCreate.getIsbn(),bookSaved.getIsbn());
        assertNotNull(bookSaved.getId());
        //test that the jpa interceptor related fields are being set
        assertEquals("admin",bookSaved.getUpdatedBy());
        assertNotNull(bookSaved.getUpdatedBy());
    }

    @Test
    @WithMockUser(username="admin",roles={"ADMIN"})
    public void whenCalledSaveUpdatedBook_thenUpdatedBook(){
        Book bookToCreate=new Book("Back door deals","ISBN 978-0-596-52468-7","Sisil Gamage");
        Book bookSaved=bookService.save(bookToCreate);
        assertNotNull(bookSaved.getId());
        //update the same book
        bookSaved.setName("Famous back door deals");
        Book bookUpdated=bookService.save(bookToCreate);
        assertEquals(bookSaved.getId(),bookUpdated.getId());
        assertEquals(bookSaved.getName(),bookUpdated.getName());
    }



}