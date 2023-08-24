package mate.academy.intro.repository;

import java.util.List;
import mate.academy.intro.model.Book;

public interface BookRepository {
    Book save(Book product);

    List<Book> findAll();
}