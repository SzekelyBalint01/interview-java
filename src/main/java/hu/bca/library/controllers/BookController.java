package hu.bca.library.controllers;

import hu.bca.library.models.Book;
import hu.bca.library.services.BookService;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.util.List;

@RepositoryRestController("books")
public class BookController {

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @ResponseStatus(HttpStatus.CREATED)

    @RequestMapping("/{bookId}/add_author/{authorId}")
    @ResponseBody Book addAuthor(@PathVariable Long bookId, @PathVariable Long authorId) {
        return this.bookService.addAuthor(bookId, authorId);
    }

    @RequestMapping("/update-all-with-year")
    public ResponseEntity<Book> bookUpdate(){
        bookService.updateBook();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/query/{country}")
    public ResponseEntity<List<Book>> booksList(@PathVariable(required = true) String country, @RequestParam(required = false) Integer from) throws FileNotFoundException {

        List<Book> books = bookService.getBookByParams(country, from);

        return new ResponseEntity<>(books, HttpStatus.OK);
    }

}
