package mate.academy.intro;

import java.math.BigDecimal;
import mate.academy.intro.model.Book;
import mate.academy.intro.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IntroApplication {
    @Autowired
    private BookService bookService;

    public static void main(String[] args) {
        SpringApplication.run(IntroApplication.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner() {
        return args -> {
            Book book = new Book();
            book.setTitle("How to Beat russia.");
            book.setPrice(BigDecimal.valueOf(540));
            book.setIsbn("978-1-940313-09-2");
            book.setAuthor("Valery Zaluzhny");
            book.setCoverImage("Hard Color Cover");
            book.setDescription("The Book About The Victory");
            bookService.save(book);
            System.out.println(bookService.findAll());
        };
    }

}
