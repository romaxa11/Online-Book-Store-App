package mate.academy.intro.service.book;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.intro.dto.book.BookDto;
import mate.academy.intro.dto.book.BookSearchParametersDto;
import mate.academy.intro.dto.book.CreateBookRequestDto;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto save(CreateBookRequestDto requestDto);

    List<BookDto> getAll(Pageable pageable);

    BookDto getBookById(Long id);

    List<BookDto> getAllByAuthor(String author);

    List<BookDto> getAllByPriceBetween(BigDecimal from, BigDecimal to, Pageable pageable);

    BookDto update(Long id, CreateBookRequestDto requestDto);

    void deleteById(Long id);

    List<BookDto> search(BookSearchParametersDto parameters);
}
