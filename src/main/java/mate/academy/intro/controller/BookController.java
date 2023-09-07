package mate.academy.intro.controller;

import java.math.BigDecimal;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.dto.BookDto;
import mate.academy.intro.dto.BookSearchParametersDto;
import mate.academy.intro.dto.CreateBookRequestDto;
import mate.academy.intro.service.BookService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Book management", description = "Endpoints for managing books")
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/books")
public class BookController {
    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Get all books", description = "Get a list of all available books")
    public List<BookDto> findAll(Pageable pageable) {
        return bookService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get book by id", description = "Get a book by id")
    public BookDto getBookById(@PathVariable Long id) {
        return bookService.getBookById(id);
    }

    @GetMapping("/author")
    @Operation(summary = "Get all books by author", description = "Get a list of all books by author")
    public List<BookDto> getAllByAuthor(@RequestParam String author) {
        return bookService.getAllByAuthor(author);
    }

    @GetMapping("/price")
    @Operation(summary = "Get all books by price, pagination and sorting")
    public List<BookDto> findAllByPrice(@RequestParam BigDecimal from,
                                           @RequestParam BigDecimal to, Pageable pageable) {
        return bookService.getAllByPriceBetween(from, to, pageable);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        bookService.deleteById(id);
    }

    @GetMapping("/search")
    public List<BookDto> search(BookSearchParametersDto searchParameters) {
        return bookService.search(searchParameters);
    }

    @PutMapping(value = "/{id}")
    public BookDto updateBook(@PathVariable Long id,
                              @RequestBody CreateBookRequestDto requestDto) {
        return bookService.update(id, requestDto);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new book", description = "Create a new book")
    public BookDto save(@RequestBody @Valid CreateBookRequestDto requestDto) {
        return bookService.save(requestDto);
    }
}
