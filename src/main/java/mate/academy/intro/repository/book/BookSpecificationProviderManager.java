package mate.academy.intro.repository.book;

import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.intro.model.Book;
import mate.academy.intro.repository.specification.SpecificationProvider;
import mate.academy.intro.repository.specification.SpecificationProviderManager;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BookSpecificationProviderManager implements SpecificationProviderManager<Book> {

    private final List<SpecificationProvider<Book>> bookSpecificationProviders;

    @Override
    public SpecificationProvider<Book> getSpecificationProvider(String key) {
        return bookSpecificationProviders.stream()
                .filter(b -> b.getKey().equals(key))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Can't find correct "
                        + "specification provider for key " + key));
    }
}
