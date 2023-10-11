package mate.academy.intro.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import mate.academy.intro.dto.book.BookDto;
import mate.academy.intro.dto.book.BookDtoWithoutCategoryIds;
import mate.academy.intro.dto.book.BookSearchParametersDto;
import mate.academy.intro.dto.book.CreateBookRequestDto;
import mate.academy.intro.mapper.BookMapper;
import mate.academy.intro.model.Book;
import mate.academy.intro.model.Category;
import mate.academy.intro.repository.book.BookRepository;
import mate.academy.intro.repository.book.BookSpecificationBuilder;
import mate.academy.intro.repository.category.CategoryRepository;
import mate.academy.intro.service.book.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {
    @InjectMocks
    private BookServiceImpl bookServiceImpl;
    @Mock
    private BookRepository bookRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private BookMapper bookMapper;
    @Mock
    private BookSpecificationBuilder bookSpecificationBuilder;

    @Test
    @DisplayName("""
            Verify create book method
            """)
    void save_ValidCreateBookRequestDto_ReturnsBookDto() {
        //Given
        CreateBookRequestDto requestDto = getCreateBookRequestDto();
        Book book = getBookByCreateBookRequestDto(requestDto);
        BookDto expected = getBookDtoByBook(book);
        expected.setId(1L);

        Mockito.when(bookMapper.toModel(requestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        // When
        BookDto actual = bookServiceImpl.save(requestDto);
        //Then
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toModel(requestDto);
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify get all books method
            """)
    void getAll_ValidList_ReturnAllBooks() {
        //Given
        Book book = getBook();
        BookDto expected = getBookDtoByBook(book);
        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> bookPage = new PageImpl<>(books, pageable, books.size());

        Mockito.when(bookRepository.findAll(pageable)).thenReturn(bookPage);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        // When
        List<BookDto> actual = bookServiceImpl.getAll(pageable);
        //Then
        assertEquals(1, actual.size());
        assertEquals(expected, actual.get(0));
    }

    @Test
    @DisplayName("""
            Verify get book by id method
            """)
    void getBookById_ValidId_ReturnBookDto_Success() {
        //Given
        Book book = getBook();
        BookDto expected = getBookDtoByBook(book);
        Long bookId = book.getId();

        Mockito.when(bookRepository.getBookById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);
        //When
        BookDto actual = bookServiceImpl.getBookById(bookId);
        //Then
        assertEquals(expected, actual);
        verify(bookRepository, times(1)).getBookById(anyLong());
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify get all books by author method
            """)
    void getAllByAuthor_ValidAuthor_ReturnAllBookDto_Success() {
        //Given
        Book book = getBook();
        List<Book> books = List.of(book);
        BookDto expected = getBookDtoByBook(book);
        String author = "Author 1";

        Mockito.when(bookRepository.findAllByAuthorContainingIgnoreCase(author)).thenReturn(books);
        Mockito.when(bookMapper.toDto(books.get(0))).thenReturn(expected);
        //When
        List<BookDto> actual = bookServiceImpl.getAllByAuthor(author);
        //Then
        assertEquals(expected, actual.get(0));
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify get all books by price method
            """)
    void getAllByPriceBetween_ValidPrice_ReturnAllBooks_Success() {
        //Given
        Book book = getBook();
        List<Book> books = List.of(book);
        BookDto expected = getBookDtoByBook(book);
        BigDecimal from = book.getPrice().subtract(BigDecimal.valueOf(5));
        BigDecimal to = book.getPrice().add(BigDecimal.valueOf(5));
        Pageable pageable = PageRequest.of(0, 10);

        Mockito.when(bookRepository.findAllByPriceBetween(from, to, pageable)).thenReturn(books);
        Mockito.when(bookMapper.toDto(books.get(0))).thenReturn(expected);
        //When
        List<BookDto> actual = bookServiceImpl.getAllByPriceBetween(from, to, pageable);
        //Then
        assertEquals(expected, actual.get(0));
        verify(bookMapper, times(1)).toDto(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify update book method
            """)
    void update_ValidBook_BookUpdated_Success() {
        // Given
        Long bookId = 1L;
        CreateBookRequestDto createBookRequestDto = getCreateBookRequestDto();
        Book book = getBookByCreateBookRequestDto(createBookRequestDto);
        BookDto expected = getBookDtoByBook(book);
        expected.setId(bookId);

        Mockito.when(bookMapper.toModel(createBookRequestDto)).thenReturn(book);
        Mockito.when(bookRepository.save(book)).thenReturn(book);
        Mockito.when(bookMapper.toDto(book)).thenReturn(expected);

        // When
        BookDto actual = bookServiceImpl.update(bookId, createBookRequestDto);

        // Then
        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).save(book);
        verify(bookMapper, times(1)).toDto(book);
        verify(bookMapper, times(1)).toModel(createBookRequestDto);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
            Verify delete by id method
            """)
    void deleteById_ValidId_DoesNotThrowException() {
        // Then
        assertDoesNotThrow(() -> bookServiceImpl.deleteById(anyLong()));
    }

    @Test
    @DisplayName("""
           Verify search method
            """)
    void search() {
        //Given
        Book book = getBook();
        BookDto expected = getBookDtoByBook(book);
        BookSearchParametersDto bookSearchParametersDto =
                new BookSearchParametersDto(
                        new String[]{book.getTitle()},
                        new String[]{book.getAuthor()}
                );
        Specification<Book> spec = bookSpecificationBuilder.build(bookSearchParametersDto);
        List<Book> books = List.of(book);

        Mockito.when(bookRepository.findAll(spec)).thenReturn(books);
        Mockito.when(bookMapper.toDto(books.get(0))).thenReturn(expected);
        // When
        List<BookDto> actual = bookServiceImpl.search(bookSearchParametersDto);
        // Then
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(expected);
        verify(bookRepository, times(1)).findAll(spec);
        verify(bookMapper, times(1)).toDto(books.get(0));
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("""
           Verify find by categoryId method
            """)
    void findAllByCategoryId_ValidCategoryId_ReturnBook_Success() {
        //Given
        Category category = getCategory();
        Book book = getBookWithCategory();
        List<Book> books = List.of(book);
        Pageable pageable = PageRequest.of(0, 10);
        BookDtoWithoutCategoryIds expected =
                getBookDtoWithoutCategoryIdsFromBook(book);
        Mockito.when(bookRepository.findAllByCategoryId(category.getId(), pageable))
                .thenReturn(books);
        Mockito.when(bookMapper.toDtoWithoutCategories(books.get(0))).thenReturn(expected);
        // When
        List<BookDtoWithoutCategoryIds> actual =
                bookServiceImpl.findAllByCategoryId(category.getId(), pageable);
        // Then
        assertThat(actual).hasSize(1);
        assertThat(actual.get(0)).isEqualTo(expected);
        verify(bookMapper, times(1)).toDtoWithoutCategories(book);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    private Book getBook() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Author 1");
        book.setTitle("Title 1");
        book.setIsbn("ISBN-10: 1-596-52068-1");
        book.setPrice(BigDecimal.valueOf(20));
        book.setDescription("Description One");
        book.setCoverImage("https://user@ukr.com/cover1.jpg");
        book.setCategories(new HashSet<>());
        return book;
    }

    private Book getBookWithCategory() {
        Book book = new Book();
        book.setId(1L);
        book.setAuthor("Author 1");
        book.setTitle("Title 1");
        book.setIsbn("ISBN-10: 1-596-52068-1");
        book.setPrice(BigDecimal.valueOf(20));
        book.setDescription("Description One");
        book.setCoverImage("https://user@ukr.com/cover1.jpg");
        book.setCategories(Set.of(getCategory()));
        return book;
    }

    private BookDto getBookDtoByBook(Book book) {
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setTitle(book.getTitle());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());
        bookDto.setDescription(book.getDescription());
        bookDto.setCoverImage(book.getCoverImage());
        bookDto.setCategoriesIds(book.getCategories().stream()
                .map(Category::getId)
                .collect(Collectors.toSet()));
        return bookDto;
    }

    private Book getBookByCreateBookRequestDto(CreateBookRequestDto createBookRequestDto) {
        Book book = new Book();
        book.setAuthor(createBookRequestDto.getAuthor());
        book.setTitle(createBookRequestDto.getTitle());
        book.setIsbn(createBookRequestDto.getIsbn());
        book.setPrice(createBookRequestDto.getPrice());
        book.setDescription(createBookRequestDto.getDescription());
        book.setCoverImage(createBookRequestDto.getCoverImage());
        book.setCategories(createBookRequestDto.getCategoriesIds().stream()
                .map(l -> {
                    Category category = new Category();
                    category.setId(l);
                    return category;
                })
                .collect(Collectors.toSet()));
        return book;
    }

    private CreateBookRequestDto getCreateBookRequestDto() {
        CreateBookRequestDto createBookRequestDto = new CreateBookRequestDto();
        createBookRequestDto.setAuthor("Author 1");
        createBookRequestDto.setTitle("Title 1");
        createBookRequestDto.setIsbn("ISBN-10: 1-596-52068-1");
        createBookRequestDto.setPrice(BigDecimal.valueOf(20));
        createBookRequestDto.setDescription("Description One");
        createBookRequestDto.setCoverImage("https://user@ukr.com/cover1.jpg");
        createBookRequestDto.setCategoriesIds(new HashSet<>());
        return createBookRequestDto;
    }

    private Category getCategory() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Adventure");
        category.setDescription("Adventure description");
        return category;
    }

    private BookDtoWithoutCategoryIds getBookDtoWithoutCategoryIdsFromBook(Book book) {
        BookDtoWithoutCategoryIds bookDtoWithoutCategoryIds = new BookDtoWithoutCategoryIds();
        bookDtoWithoutCategoryIds.setAuthor(book.getAuthor());
        bookDtoWithoutCategoryIds.setTitle(book.getTitle());
        bookDtoWithoutCategoryIds.setIsbn(book.getIsbn());
        bookDtoWithoutCategoryIds.setPrice(book.getPrice());
        bookDtoWithoutCategoryIds.setDescription(book.getDescription());
        bookDtoWithoutCategoryIds.setCoverImage(book.getCoverImage());
        bookDtoWithoutCategoryIds.setId(book.getId());
        return bookDtoWithoutCategoryIds;
    }
}
