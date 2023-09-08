package mate.academy.intro.service.impl;

import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookSearchParametersDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.exception.EntityNotFoundException;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.book.BookRepository;
import mate.academy.intro.repository.book.BookSpecificationBuilder;
import mate.academy.intro.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    private final BookSpecificationBuilder bookSpecificationBuilder;

    @Override
    public BookDto save(CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public List<BookDto> getAll(Pageable pageable) {
        return bookRepository.findAll(pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Can't find employee by id " + id));
        return bookMapper.toDto(book);
    }

    @Override
    public List<BookDto> getAllByAuthor(String author) {
        return bookRepository.findAllByAuthorContainingIgnoreCase(author).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public List<BookDto> getAllByPriceBetween(BigDecimal from, BigDecimal to, Pageable pageable) {
        return bookRepository.findAllByPriceBetween(from, to, pageable).stream()
                .map(bookMapper::toDto)
                .toList();
    }

    @Override
    public BookDto update(Long id, CreateBookRequestDto requestDto) {
        Book book = bookMapper.toModel(requestDto);
        book.setId(id);
        return bookMapper.toDto(bookRepository.save(book));
    }

    @Override
    public void deleteById(Long id) {
        bookRepository.deleteById(id);
    }

    @Override
    public List<BookDto> search(BookSearchParametersDto parameters) {
        Specification<Book> specification = bookSpecificationBuilder.build(parameters);
        return bookRepository.findAll(specification).stream()
                .map(bookMapper::toDto)
                .toList();
    }
}
