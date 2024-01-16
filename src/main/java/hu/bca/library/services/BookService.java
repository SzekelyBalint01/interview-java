package hu.bca.library.services;

import hu.bca.library.models.Book;

import java.io.FileNotFoundException;
import java.util.List;

public interface BookService {
    Book addAuthor(Long bookId, Long authorId);

    void updateBook();

    List<Book> getBookByParams(String country, Integer from, Integer to) throws FileNotFoundException;
}
