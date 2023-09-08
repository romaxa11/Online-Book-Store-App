package mate.academy.intro.service;

import java.math.BigDecimal;
import java.util.List;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookSearchParametersDto;
import mate.academy.intro.dto.CreateBookRequestDto;
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
