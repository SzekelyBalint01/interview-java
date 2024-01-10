package hu.bca.library.services.impl;

import hu.bca.library.models.Author;
import hu.bca.library.models.Book;
import hu.bca.library.repositories.AuthorRepository;
import hu.bca.library.repositories.BookRepository;
import hu.bca.library.services.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.lang.module.FindException;
import java.util.List;
import java.util.Optional;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
    }

    @Override
    public Book addAuthor(Long bookId, Long authorId) {
        Optional<Book> book = this.bookRepository.findById(bookId);
        if (book.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Book with id %s not found", bookId));
        }
        Optional<Author> author = this.authorRepository.findById(authorId);
        if (author.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format("Author with id %s not found", authorId));
        }

        List<Author> authors = book.get().getAuthors();
        authors.add(author.get());

        book.get().setAuthors(authors);
        return this.bookRepository.save(book.get());
    }

    /**
     *
     * This function search for a book in the OpenLibrary online system.
     *
     * You have to give the book "Works id", it usually starts with "OL"
     *
     * @param workId - OpenLibrary works id
     * @return - returns with the year of the book (type int)
     */

    public Integer OpenLibraryApi(String workId){
        try {

            String bookUrl = "https://openlibrary.org/works/"+workId+".json";

            // Az Open Library API URL
            String apiUrl = bookUrl;

            // HTTP GET kérés elküldése
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Válasz olvasása
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            try {
                // JSON válasz feldolgozása
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(String.valueOf(response));

                // A first_publish_date értékének kinyerése
                String firstPublishDate = jsonNode.path("first_publish_date").asText();



                if (firstPublishDate.length() >= 4) {
                    return Integer.parseInt(firstPublishDate.substring(firstPublishDate.length() - 4));
                }


            } catch (Exception e) {
                e.printStackTrace();
            }

            connection.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void updateBook() {
       Iterable<Book> books = bookRepository.findAll();

        for (Book book: books) {
            Integer year = OpenLibraryApi(book.getWorkId());
            if(year != null){
                book.setYear(year);
                bookRepository.save(book);
            }
        }
    }


    /**

     * @param country REQUIRED
     * @param from OPTIONAL
     * @return - returns with books, where the author is from "country" param and the year of the book is not earlier than "from" param. If "from" is null than returns all books, where the author is from "country" param.
     * @throws FileNotFoundException if the database is empty
     */
    @Override
    public List<Book> getBookByParams(String country, Integer from) throws FileNotFoundException {

        if (from != null){
            return bookRepository.findBooksByParams(country, from).orElseThrow(()-> new FileNotFoundException());
        }

        return bookRepository.findBooksByParams(country).orElseThrow(()->new FindException());
    }
}
