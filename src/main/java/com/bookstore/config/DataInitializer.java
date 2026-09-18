package com.bookstore.config;

import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeBooks(
            BookRepository repository
    ) {

        return args -> {

            if (repository.count() > 0) {
                return;
            }

            repository.save(
                    new Book(
                            "Five Point Someone",
                            "Chetan Bhagat",
                            new BigDecimal("45.00")
                    )
            );

            repository.save(
                    new Book(
                            "The White Tiger",
                            "Aravind Adiga",
                            new BigDecimal("55.00")
                    )
            );

            repository.save(
                    new Book(
                            "The Blue Umbrella",
                            "Amit Singh",
                            new BigDecimal("60.00")
                    )
            );

            repository.save(
                    new Book(
                            "Godan",
                            "Munshi Premchand",
                            new BigDecimal("40.00")
                    )
            );
        };
    }
}
