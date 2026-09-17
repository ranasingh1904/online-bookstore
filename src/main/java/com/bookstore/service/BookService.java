package com.bookstore.service;

import com.bookstore.dto.BookRequest;
import com.bookstore.entity.Book;
import com.bookstore.exception.ResourceNotFoundException;
import com.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {

        return bookRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Book not found: " + id
                        )
                );
    }

    public Book create(BookRequest request) {

        Book book = new Book(
                request.title(),
                request.author(),
                request.price()
        );

        return bookRepository.save(book);
    }

    public Book update(Long id, BookRequest request) {

        Book book = findById(id);

        book.setTitle(request.title());
        book.setAuthor(request.author());
        book.setPrice(request.price());

        return bookRepository.save(book);
    }

    public void delete(Long id) {

        Book book = findById(id);

        bookRepository.delete(book);
    }
}
