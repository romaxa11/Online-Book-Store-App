package mate.academy.intro.service.impl;

import java.util.List;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.BookRepository;
import mate.academy.intro.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public Book save(Book product) {
        return bookRepository.save(product);
    }

    @Override
    public List<Book> findAll() {
        return bookRepository.findAll();
    }
}