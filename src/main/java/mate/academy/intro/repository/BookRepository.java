package mate.academy.intro.repository;

import java.util.List;
import java.util.Optional;

import mate.academy.intro.model.Book;

public interface BookRepository {
    Book save(Book product);

    List<Book> findAll();

    Optional<Book> findById(Long id);

    List<Book> findAllByAuthor(String author);
}