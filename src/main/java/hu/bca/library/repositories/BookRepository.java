package hu.bca.library.repositories;

import hu.bca.library.models.Book;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends CrudRepository<Book, Long> {

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.country = :country AND b.year >= :from AND b.year <= :to ORDER BY b.year ASC ")
    Optional<List<Book>> findBooksByParams(@Param("country") String country, @Param("from") Integer from, @Param("to") Integer to);

    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.country = :country AND b.year >= :from ORDER BY b.year ASC ")
    Optional<List<Book>> findBooksByParamsFrom(@Param("country") String country, @Param("from") Integer from);


    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.country = :country ORDER BY b.year ASC ")
    Optional<List<Book>> findBooksByParams(@Param("country") String country);


    @Query("SELECT b FROM Book b JOIN b.authors a WHERE a.country = :country AND b.year <= :to ORDER BY b.year ASC ")
    Optional<List<Book>> findBooksByParamTo(String country, Integer to);
}
