package com.bookstore.online_bookstore.controller;

import com.bookstore.controller.BookController;
import com.bookstore.dto.BookRequest;
import com.bookstore.entity.Book;
import com.bookstore.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private Book book;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        book = mock(Book.class);
        bookRequest = mock(BookRequest.class);
    }

    @Test
    void getBooks_shouldReturnAllBooks() {
        List<Book> books = List.of(book);

        when(bookService.findAll()).thenReturn(books);

        List<Book> result = bookController.getBooks();

        assertSame(books, result);
        assertEquals(1, result.size());

        verify(bookService, times(1)).findAll();
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void getBook_shouldReturnBookById() {
        Long bookId = 1L;

        when(bookService.findById(bookId)).thenReturn(book);

        Book result = bookController.getBook(bookId);

        assertSame(book, result);

        verify(bookService, times(1)).findById(bookId);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void createBook_shouldCreateAndReturnBook() {
        when(bookService.create(bookRequest)).thenReturn(book);

        Book result = bookController.createBook(bookRequest);

        assertSame(book, result);

        verify(bookService, times(1)).create(bookRequest);
        verifyNoMoreInteractions(bookService);
    }

    @Test
    void updateBook_shouldUpdateAndReturnBook() {
        Long bookId = 1L;

        when(bookService.update(bookId, bookRequest))
                .thenReturn(book);

        Book result = bookController.updateBook(bookId, bookRequest);

        assertSame(book, result);

        verify(bookService, times(1))
                .update(bookId, bookRequest);

        verifyNoMoreInteractions(bookService);
    }

    @Test
    void deleteBook_shouldDeleteBook() {
        Long bookId = 1L;

        doNothing().when(bookService).delete(bookId);

        bookController.deleteBook(bookId);

        verify(bookService, times(1)).delete(bookId);
        verifyNoMoreInteractions(bookService);
    }
}

