package mate.academy.intro.repository;

import java.util.List;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.book.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("""
            Find all books in empty DB
            """)
    void findAll_ReturnsEmptyList() {
        List<Book> actual = bookRepository.findAll();
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("""
            Find all books in DB
            """)
    @Sql(scripts = {
        "classpath:database/books/add-kobzar-book-to-books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = {
        "classpath:database/books/remove-kobzar-book-from-books-table.sql"
    }, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAll_ReturnsList() {
        List<Book> actual = bookRepository.findAll();
        Assertions.assertEquals(1, actual.size());
        Assertions.assertEquals("Kobzar", actual.get(0).getTitle());
    }
}
