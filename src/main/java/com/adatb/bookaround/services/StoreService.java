package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.*;
import com.adatb.bookaround.entities.compositepk.StockId;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.models.StoreWithBusinessHours;
import com.adatb.bookaround.repositories.*;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.metamodel.model.domain.MappedSuperclassDomainType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StoreService {

    private static final Logger logger = LogManager.getLogger(StoreService.class);
    @Autowired
    private StoreDao storeDao;
    @Autowired
    private StockDao stockDao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private BookDao bookDao;
    @Autowired
    private BusinessHoursDao businessHoursDao;
    @Autowired
    private NotificationDao notificationDao;

    public static boolean checkForEmptyString(String[] args) {
        for (String str : args) {
            if (str.isEmpty())
                return true;
        }
        return false;
    }

    public ArrayList<StoreWithBusinessHours> getAllStores() {
        ArrayList<StoreWithBusinessHours> stores = storeDao.findAllStoresWithBusinessHours();
        if (stores == null || stores.isEmpty()) {
            logger.warn("Stores could not be loaded!");
            return new ArrayList<>();
        }
        return stores;
    }

    public StoreWithBusinessHours getStoreById(Long storeId) {
        StoreWithBusinessHours storeWithBusinessHours = storeDao.findStoreWithBusinessHoursById(storeId);
        if (storeWithBusinessHours.getBusinessHours() == null) {
            logger.warn("Business hours could not be loaded!");
            return new StoreWithBusinessHours(storeWithBusinessHours.getStore(), new ArrayList<>());
        }
        if (storeWithBusinessHours.getStore() == null) {
            logger.warn("Store could not be loaded!");
            return new StoreWithBusinessHours(new Store(), new ArrayList<>());
        }
        return storeWithBusinessHours;
    }

    public List<Stock> getStockForStoreById(Long storeId) {
        List<Stock> stockForStore = stockDao.findStockForStoreById(storeId);
        if (stockForStore.isEmpty()) {
            logger.warn("Stock for store could not be loaded!");
            return new ArrayList<>();
        }
        return stockForStore;
    }

    @SuppressWarnings("unused")
    public List<Order> getAllOrders() {
        List<Order> orders = orderDao.findAll();
        if (orders == null || orders.isEmpty()) {
            logger.warn("Orders could not be loaded!");
            return new ArrayList<>();
        }
        return orders;
    }

    public Long getNumberOfUnsoldBooks() {
        return bookDao.findNumberOfUnsoldBooks();
    }

    public Map<Store, List<BookWithAuthorsAndGenres>> getStockForEachStore() {
        Map<Store, List<BookWithAuthorsAndGenres>> resultStock = stockDao.findStockForEachStore();

        if (resultStock == null || resultStock.isEmpty()) {
            logger.warn("Stock could not be loaded");
            return new HashMap<>();
        }

        return resultStock;
    }

    /*modifyStoreId, modifyName, modifyCountry,
                modifyStateOrRegion, modifyPostcode, modifyCity, modifyStreet,
                modifyOpeningTimes, modifyClosingTimes, deleteBusinessHours*/
    public boolean modifyStoreById(Long modifiedStoreId, String modifiedName, String modifiedCountry,
                                   String modifiedStateOrRegion, String modifiedPostcode, String modifiedCity,
                                   String modifiedStreet, String[] modifiedOpeningTimes, String[] modifiedClosingTimes) {

        Store store = storeDao.find(modifiedStoreId);
        if (store == null) {
            logger.warn("Store could not be loaded with the following id: " + modifiedStoreId + " (modification)");
            return false;
        }


        if (!modifiedName.isEmpty()) {
            store.setName(modifiedName);
        }
        if (!modifiedCountry.isEmpty()) {
            store.setCountry(modifiedCountry);
        }
        if (!modifiedStateOrRegion.isEmpty()) {
            store.setStateOrRegion(modifiedStateOrRegion);
        }
        if (!modifiedPostcode.isEmpty()) {
            store.setPostcode(modifiedPostcode);
        }
        if (!modifiedCity.isEmpty()) {
            store.setCity(modifiedCity);
        }
        if (!modifiedStreet.isEmpty()) {
            store.setStreet(modifiedStreet);
        }

        storeDao.update(store);



        ArrayList<BusinessHours> businessHoursList = storeDao.findStoreWithBusinessHoursById(store.getStoreId()).getBusinessHours();
        for (short i = 0; i < 7; i++) {
            String modifiedOpeningTime = "";
            String modifiedClosingTime = "";
            boolean operation = false;

            if (!modifiedOpeningTimes[i].isBlank() && !modifiedClosingTimes[i].isBlank()) {
                modifiedOpeningTime = convertStringTimeFrom24HTo12HFormat(modifiedOpeningTimes[i]);
                modifiedClosingTime = convertStringTimeFrom24HTo12HFormat(modifiedClosingTimes[i]);
            }

            for (BusinessHours businessHours : businessHoursList) {
                if (businessHours.getDayOfWeek() == i + 1) {
                    businessHours.setStore(store);

                    if (modifiedOpeningTime.isBlank() || modifiedClosingTime.isBlank()) {
                        businessHoursDao.delete(businessHours.getHoursId());
                        operation = true;
                    } else if (businessHoursDao.find(businessHours.getHoursId()) != null) {
                        businessHours.setOpeningTime(modifiedOpeningTime);
                        businessHours.setClosingTime(modifiedClosingTime);

                        businessHoursDao.update(businessHours);
                        operation = true;
                    }
                }

            }

            if (!operation && !modifiedOpeningTime.isBlank() && !modifiedClosingTime.isBlank()) {
                BusinessHours newBusinessHours = new BusinessHours(null, (short) (i+1), modifiedOpeningTime, modifiedClosingTime, store);
                businessHoursDao.create(newBusinessHours);
            }
        }
        return true;
    }

    public boolean deleteStoreById(Long storeId) {
        Store store = storeDao.find(storeId);
        if (store != null) {
            storeDao.delete(storeId);
            return true;
        } else {
            logger.warn("Something went wrong in deleting store: " + storeId);
            return false;
        }
    }

    public boolean createStore(String createName, String createCountry, String createStateOrRegion,
                               String createPostcode, String createCity, String createStreet) {

        if (createName.isBlank()) {
            logger.warn("Store creation error: Name empty");
            return false;
        }
        if (createCountry.isBlank()) {
            logger.warn("Store creation error: Country empty");
            return false;
        }
        if (createStateOrRegion.isBlank()) {
            logger.warn("Store creation error: StateOrRegion empty");
            return false;
        }
        if (createPostcode.isBlank()) {
            logger.warn("Store creation error: Postcode empty");
            return false;
        }
        if (createCity.isBlank()) {
            logger.warn("Store creation error: City empty");
            return false;
        }
        if (createStreet.isBlank()) {
            logger.warn("Store creation error: Street empty");
            return false;
        }
        Store store = new Store(null, createName, createStreet, createCity, createStateOrRegion, createPostcode,
               createCountry);

        if (storeDao.create(store) == null) {
            logger.warn("Store creation error!");
            return false;
        }

        return true;
    }

    // utility/auxiliary function
    public static String convertStringTimeFrom12HTo24HFormat(String time) {
        String returnTime = "00:00";

        if (Objects.equals(time.split(" ")[1], "PM")) {
            String hoursAndMinutes = time.split(" ")[0];
            int hours = Integer.parseInt(hoursAndMinutes.split(":")[0]);
            int minutes = Integer.parseInt(hoursAndMinutes.split(":")[1]);

            if (hours < 12) {
                hours += 12;
            }
            hoursAndMinutes = hours + ":";
            if (minutes < 10) {
                hoursAndMinutes += "0";
            }
            hoursAndMinutes += minutes;
            returnTime = hoursAndMinutes;
        } else if (Objects.equals(time.split(" ")[1], "AM")) {
            String hoursAndMinutes = time.split(" ")[0];
            int hours = Integer.parseInt(hoursAndMinutes.split(":")[0]);
            int minutes = Integer.parseInt(hoursAndMinutes.split(":")[1]);

            if (hours == 12) {
                hours -= 12;
            }
            hoursAndMinutes = "";
            if (hours < 10) {
                hoursAndMinutes = "0";
            }
            hoursAndMinutes += hours + ":";
            if (minutes < 10) {
                hoursAndMinutes += "0";
            }
            hoursAndMinutes += minutes;
            returnTime = hoursAndMinutes;
        }

        return returnTime;
    }

    // utility/auxiliary function
    public static String convertStringTimeFrom24HTo12HFormat(String time) {
        String returnTime = "00:00 AM";

        int hours = Integer.parseInt(time.split(":")[0]);
        int minutes = Integer.parseInt(time.split(":")[1]);

        if (hours >= 0 && hours < 24 && minutes >= 0 && minutes < 60) {
            if (hours >= 12) {
                if (hours != 12) {
                    hours -= 12;
                }
                returnTime = " PM";
            } else {
                if (hours == 0) {
                    hours = 12;
                }
                returnTime = " AM";
            }

            if (minutes < 10) {
                returnTime = ":0" + minutes + returnTime;
            } else {
                returnTime = ":" + minutes + returnTime;
            }
            if (hours < 10) {
                returnTime = "0" + hours + returnTime;
            } else {
                returnTime = hours + returnTime;
            }
        }

        return returnTime;
    }

    public boolean isStoreContainsBook(Long storeId, Long bookId) {
        List<Stock> stockList = stockDao.findStocksByBookId(bookId);
        for (Stock stock : stockList) {
            if (Objects.equals(stock.getStockId().getStore().getStoreId(), storeId)) {
                return true;
            }
        }
        return false;
    }

    public int addStock(Long storeId, String bookTitleFragment, Integer bookCount) {
        List<Long> completeBookListIds = new ArrayList<>();
        for (BookWithAuthorsAndGenres book : bookDao.filterBooksWithAuthorsAndGenres(bookTitleFragment)) {
            completeBookListIds.add(book.getBook().getBookId());
        }

        if (completeBookListIds.isEmpty()) {
            logger.warn("Book(s) not find with title fragment: " + bookTitleFragment);
            return 0;
        }

        for (Long bookId : completeBookListIds) {
            if (isStoreContainsBook(storeId, bookId)) {
                stockDao.updateByStockId(stockDao.findStockId(bookId, storeId),  bookCount);
            } else {
                stockDao.create(new Stock(new StockId(bookDao.find(bookId), storeDao.find(storeId)), bookCount));
            }
        }

        return completeBookListIds.size();
    }

    public boolean deleteStock(Long storeId, Long bookId) {
        if (!isStoreContainsBook(storeId, bookId)) {
            logger.warn("Store "+ storeDao.find(storeId).getName() +" not found with book " + bookDao.find(bookId) + ".");
            return false;
        }

        return stockDao.deleteStockFromStore(storeId, bookId);
    }

    public void deleteNotificationById(Long notificationId) {
        notificationDao.delete(notificationId);
    }

}
