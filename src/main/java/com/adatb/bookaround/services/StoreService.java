package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.Book;
import com.adatb.bookaround.entities.BusinessHours;
import com.adatb.bookaround.entities.Order;
import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.models.StoreWithBusinessHours;
import com.adatb.bookaround.repositories.*;
import jakarta.persistence.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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

    public HashMap<BookWithAuthorsAndGenres, Integer> getStockForStoreById(Long storeId) {
        HashMap<BookWithAuthorsAndGenres, Integer> stockForStore = stockDao.findStockForStoreById(storeId);
        if (stockForStore.isEmpty()) {
            logger.warn("Stock for store could not be loaded!");
            return new HashMap<>();
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
                if (Integer.parseInt(modifiedOpeningTimes[i].split(":")[0]) > 12) {
                    modifiedOpeningTime = Integer.parseInt(modifiedOpeningTimes[i].split(":")[0]) - 12 + ":" +
                            modifiedOpeningTimes[i].split(":")[1] + " PM";
                } else {
                    modifiedOpeningTime = modifiedOpeningTimes[i] + " AM";
                }

                if (Integer.parseInt(modifiedClosingTimes[i].split(":")[0]) > 12) {
                    modifiedClosingTime = Integer.parseInt(modifiedClosingTimes[i].split(":")[0]) - 12 + ":" +
                            modifiedClosingTimes[i].split(":")[1] + " PM";
                } else {
                    modifiedClosingTime = modifiedClosingTimes[i] + " AM";
                }
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
                BusinessHours newBusinessHours = new BusinessHours(businessHoursDao.getGreatestBusinessHoursId()+1, (short) (i+1), modifiedOpeningTime, modifiedClosingTime, store);
                logger.warn("Create!");
                businessHoursDao.create(newBusinessHours); //Valami miatt elszall a progi. Oka lehet: Nem jol szurja be alapertelmezetten az adatbazisba a BusinessHours-t
            }
        }
        return true;
    }

}
