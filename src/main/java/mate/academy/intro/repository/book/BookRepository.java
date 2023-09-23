package mate.academy.intro.repository.book;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import mate.academy.intro.model.Book;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {
    @EntityGraph(attributePaths = "categories")
    List<Book> findAll(Specification<Book> spec);

    @EntityGraph(attributePaths = "categories")
    List<Book> findAllByAuthorContainingIgnoreCase(String author);

    @EntityGraph(attributePaths = "categories")
    List<Book> findAllByPriceBetween(BigDecimal from, BigDecimal to, Pageable pageable);

    @EntityGraph(attributePaths = "categories")
    @Query("FROM Book b JOIN FETCH b.categories c "
            + "WHERE c.id = :categoryId")
    List<Book> findAllByCategoryId(Long categoryId, Pageable pageable);

    @EntityGraph(attributePaths = "categories")
    @Query("FROM Book b join FETCH b.categories c "
            + "WHERE b.id = :id AND b.isDeleted = false AND  c.isDeleted = false ")
    Optional<Book> getBookById(Long id);
}
