package com.adatb.bookaround.controllers;

import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.repositories.BookDao;
import com.adatb.bookaround.repositories.GenreDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StoreController {

    @Autowired
    private BookDao bookDao;

    @Autowired
    private GenreDao genreDao;

    private static final Logger logger = LogManager.getLogger(StoreController.class);

    @GetMapping("/index")
    public String showIndex(Model model) {

        List<BookWithAuthorsAndGenres> list = bookDao.findAllBooksWithAuthorsAndGenres();

        list.forEach(e -> {
            logger.info("begin");
            logger.info(e.getBook().getTitle());
            e.getAuthors().forEach(author -> {
                logger.info(author.getAuthorId().getFirstName());
            });
            e.getGenres().forEach(genre -> {
                logger.info(genre.getGenreId().getGenreName());
            });
            logger.info("end");
        });

        Map<String, Long> NOBByGenre = genreDao.getNumberOfBooksByGenre();

        for (Map.Entry<String, Long> entry : NOBByGenre.entrySet()) {
            logger.info(entry.getKey() + ": " + entry.getValue() + " db könyv");
        }

        return "index";
    }

}
