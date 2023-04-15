package com.adatb.bookaround.controllers;

import com.adatb.bookaround.entities.Author;
import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.Genre;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.models.CustomerDetails;
import com.adatb.bookaround.services.AuthService;
import com.adatb.bookaround.services.BookService;
import com.adatb.bookaround.services.CustomerService;
import com.adatb.bookaround.services.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class StoreController {

    @Autowired
    private BookService bookService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private StoreService storeService;

    @GetMapping("/index")
    public String showIndex(Model model) {

        model.addAttribute("stockList", storeService.getStockForEachStore());

        return "index";
    }

    @GetMapping("/latest-additions")
    public String showLatestAdditions(Model model) {
        model.addAttribute("bookList", bookService.getLatestBooks());
        model.addAttribute("activePage", "latest-additions");
        return "latest-additions";
    }

    @GetMapping("/bestsellers")
    public String showPopularBooks(Model model) {
        model.addAttribute("bookList", bookService.getPopularBooks());
        model.addAttribute("activePage", "bestsellers");
        return "bestsellers";
    }

    @GetMapping("/notifications/{cid}")
    public String showNotificationsForCustomer(Model model,
                                               @PathVariable Long cid) {
        model.addAttribute("notificationList", customerService.getNotificationsByCustomerId(cid));
        model.addAttribute("activePage", "notifications");
        return "notifications";
    }

    @GetMapping("/admin-panel")
    public String showAdminPanel(Model model) {
        model.addAttribute("customerList", customerService.getCustomers());
        model.addAttribute("activePage", "admin-panel");
        return "admin-panel";
    }

    @GetMapping("/admin-panel-create")
    public String showCreateAdminPanel(Model model) {
        model.addAttribute("newBook", new Book());
        model.addAttribute("activePage", "admin-panel");
        return "admin-panel-create";
    }

    @GetMapping("/book/{bookId}")
    public String showBookById(Model model,
                               @PathVariable Long bookId,
                               @AuthenticationPrincipal CustomerDetails customerDetails) {
        BookWithAuthorsAndGenres curr = bookService.getBookWithAuthorsAndGenresById(bookId);
        Set<Author> authors = curr.getAuthors();
        Set<Genre> genres = curr.getGenres();
        model.addAttribute("bookModel", curr);
        model.addAttribute("authorsAsString",
                BookService.joinStrings(authors.stream().map(author -> author.getAuthorId().getFirstName()
                        + " " + author.getAuthorId().getLastName()).collect(Collectors.toSet())));
        model.addAttribute("genresAsString",
                BookService.joinStrings(genres.stream().map(genre -> genre.getGenreId().getGenreName())
                        .collect(Collectors.toSet())));
        model.addAttribute("recommendationModels", bookService.getRecommendationsByBookId(bookId));
        model.addAttribute("isLoggedIn", AuthService.isAuthenticated());
        if (AuthService.isAuthenticated()) {
            model.addAttribute("wishlistModels",
                    customerService.getWishlistsForCustomer(customerDetails.getCustomerId()));
            model.addAttribute("numberOfWishlists",
                    customerService.getNumberOfWishlistsForCustomer(customerDetails.getCustomerId()));
        }
        return "book";
    }

    @PostMapping("/add-book")
    public String addBookWithAuthorsAndGenres(Book newBook,
                                              @RequestParam(name = "authors") String authors,
                                              @RequestParam(name = "genres") String genres,
                                              RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("entityCreationVerdict",
                bookService.createBookWithAuthorsAndGenres(newBook, authors, genres)
                        ? "Új könyv sikeresen létrehozva!"
                        : "Új könyv létrehozása sikertelen!"
        );
        return "redirect:/admin-panel-create";
    }

    @PostMapping("/delete-book")
    public String deleteBookById(@RequestParam(name = "delete-book-id") Long deleteBookId,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("entityDeletionVerdict",
                bookService.deleteBookById(deleteBookId)
                        ? "Könyv sikeresen törölve!"
                        : "Könyv törlése sikertelen! (id = " + deleteBookId + ")"
        );
        return "redirect:/index";
    }

    @PostMapping("/modify-book")
    public String modifyBookById(@RequestParam Long modifyBookId,
                                 @RequestParam String modifyTitle,
                                 @RequestParam String modifyDescription,
                                 @RequestParam String modifyCover,
                                 @RequestParam Double modifyWeight,
                                 @RequestParam Long modifyPrice,
                                 @RequestParam Integer modifyNumberOfPages,
                                 @RequestParam LocalDate modifyPublishedAt,
                                 @RequestParam String modifyPublisher,
                                 @RequestParam String modifyIsbn,
                                 @RequestParam String modifyLanguage,
                                 // works with string only for some reason
                                 @RequestParam(required = false) String modifyDiscountedPrice,
                                 @RequestParam String modifyAuthors,
                                 @RequestParam String modifyGenres,
                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bookModificationVerdict",
                bookService.modifyBookById(
                        modifyBookId, modifyTitle, modifyDescription,
                        modifyCover, modifyWeight, modifyPrice,
                        modifyNumberOfPages, modifyPublishedAt,
                        modifyPublisher, modifyIsbn, modifyLanguage,
                        modifyDiscountedPrice.isEmpty() ? null : Long.parseLong(modifyDiscountedPrice),
                        modifyAuthors, modifyGenres
                )
                        ? "Könyv sikeresen módosítva!"
                        : "Könyv módosítása sikertelen! (id = " + modifyBookId + ")");
        return "redirect:/book/" + modifyBookId;
    }

}
