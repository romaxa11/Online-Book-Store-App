package mate.academy.intro.repository.book;

import java.util.List;
import mate.academy.intro.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    List<Book> findAllByAuthorContainingIgnoreCase(String author);
}