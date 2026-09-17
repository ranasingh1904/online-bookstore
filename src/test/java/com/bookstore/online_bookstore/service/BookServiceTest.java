package com.bookstore.online_bookstore.service;

import com.bookstore.dto.BookRequest;
import com.bookstore.entity.Book;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import com.bookstore.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldCreateBook() {

        BookRequest request = new BookRequest(
                "Five Point Someone",
                "Chetan Bhagat",
                new BigDecimal("45.00")
        );

        Book savedBook = new Book(
                "Five Point Someone",
                "Chetan Bhagat",
                new BigDecimal("45.00")
        );

        when(bookRepository.save(any(Book.class)))
                .thenReturn(savedBook);

        Book result = bookService.create(request);

        assertNotNull(result);
        assertEquals("Five Point Someone", result.getTitle());
        assertEquals("Chetan Bhagat", result.getAuthor());
        assertEquals(
                new BigDecimal("45.00"),
                result.getPrice()
        );

        verify(bookRepository).save(any(Book.class));
    }


    @Test
    void shouldFailToCreateBookWhenRepositoryThrowsException() {

        BookRequest request = new BookRequest(
                "Five Point Someone",
                "Chetan Bhagat",
                new BigDecimal("45.00")
        );

        when(bookRepository.save(any(Book.class)))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookService.create(request)
        );

        assertEquals("Database error", exception.getMessage());

        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void shouldGetAllBooks() {

        List<Book> books = List.of(
                new Book(
                        "Five Point Someone",
                        "Chetan Bhagat",
                        new BigDecimal("45.00")
                ),
                new Book(
                        "The White Tiger",
                        "Aravind Adiga",
                        new BigDecimal("55.00")
                )
        );

        when(bookRepository.findAll())
                .thenReturn(books);

        List<Book> result = bookService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());

        verify(bookRepository).findAll();
    }


    @Test
    void shouldReturnEmptyListWhenNoBooksExist() {

        when(bookRepository.findAll())
                .thenReturn(List.of());

        List<Book> result = bookService.findAll();

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(bookRepository).findAll();
    }


    @Test
    void shouldFailToFindAllBooksWhenRepositoryThrowsException() {

        when(bookRepository.findAll())
                .thenThrow(new RuntimeException("Database unavailable"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookService.findAll()
        );

        assertEquals(
                "Database unavailable",
                exception.getMessage()
        );

        verify(bookRepository).findAll();
    }

    @Test
    void shouldGetBookById() {

        Book book = new Book(
                "Godan",
                "Munshi Premchand",
                new BigDecimal("40.00")
        );

        when(bookRepository.findById(4L))
                .thenReturn(Optional.of(book));

        Book result = bookService.findById(4L);

        assertNotNull(result);
        assertEquals("Godan", result.getTitle());
        assertEquals("Munshi Premchand", result.getAuthor());
        assertEquals(
                new BigDecimal("40.00"),
                result.getPrice()
        );

        verify(bookRepository).findById(4L);
    }


    @Test
    void shouldThrowResourceNotFoundExceptionWhenBookDoesNotExist() {

        when(bookRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.findById(99L)
        );

        assertEquals(
                "Book not found: 99",
                exception.getMessage()
        );

        verify(bookRepository).findById(99L);
    }


    @Test
    void shouldFailToFindBookWhenRepositoryThrowsException() {

        when(bookRepository.findById(1L))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookService.findById(1L)
        );

        assertEquals(
                "Database error",
                exception.getMessage()
        );

        verify(bookRepository).findById(1L);
    }

    @Test
    void shouldUpdateBook() {

        Book existingBook = new Book(
                "Old Title",
                "Old Author",
                new BigDecimal("30.00")
        );

        BookRequest request = new BookRequest(
                "New Title",
                "New Author",
                new BigDecimal("50.00")
        );

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(existingBook))
                .thenReturn(existingBook);

        Book result = bookService.update(1L, request);

        assertNotNull(result);
        assertEquals("New Title", result.getTitle());
        assertEquals("New Author", result.getAuthor());
        assertEquals(
                new BigDecimal("50.00"),
                result.getPrice()
        );

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(existingBook);
    }


    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingBook() {

        BookRequest request = new BookRequest(
                "New Title",
                "New Author",
                new BigDecimal("50.00")
        );

        when(bookRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.update(99L, request)
        );

        assertEquals(
                "Book not found: 99",
                exception.getMessage()
        );

        verify(bookRepository).findById(99L);

        verify(bookRepository, never()).save(any(Book.class));
    }


    @Test
    void shouldFailToUpdateBookWhenRepositorySaveThrowsException() {

        Book existingBook = new Book(
                "Old Title",
                "Old Author",
                new BigDecimal("30.00")
        );

        BookRequest request = new BookRequest(
                "New Title",
                "New Author",
                new BigDecimal("50.00")
        );

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(existingBook));

        when(bookRepository.save(existingBook))
                .thenThrow(new RuntimeException("Database error"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookService.update(1L, request)
        );

        assertEquals(
                "Database error",
                exception.getMessage()
        );

        verify(bookRepository).findById(1L);
        verify(bookRepository).save(existingBook);
    }

    @Test
    void shouldDeleteBook() {

        Book book = new Book(
                "Godan",
                "Munshi Premchand",
                new BigDecimal("40.00")
        );

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        bookService.delete(1L);

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(book);
    }


    @Test
    void shouldThrowExceptionWhenDeletingNonExistingBook() {

        when(bookRepository.findById(99L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> bookService.delete(99L)
        );

        assertEquals(
                "Book not found: 99",
                exception.getMessage()
        );

        verify(bookRepository).findById(99L);

        verify(bookRepository, never()).delete(any(Book.class));
    }


    @Test
    void shouldFailToDeleteBookWhenRepositoryThrowsException() {

        Book book = new Book(
                "Godan",
                "Munshi Premchand",
                new BigDecimal("40.00")
        );

        when(bookRepository.findById(1L))
                .thenReturn(Optional.of(book));

        doThrow(new RuntimeException("Database error"))
                .when(bookRepository)
                .delete(book);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> bookService.delete(1L)
        );

        assertEquals(
                "Database error",
                exception.getMessage()
        );

        verify(bookRepository).findById(1L);
        verify(bookRepository).delete(book);
    }
}
