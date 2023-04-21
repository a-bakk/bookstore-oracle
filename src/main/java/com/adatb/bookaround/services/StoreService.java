package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.BusinessHours;
import com.adatb.bookaround.entities.Order;
import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.models.StoreWithBusinessHours;
import com.adatb.bookaround.repositories.BookDao;
import com.adatb.bookaround.repositories.OrderDao;
import com.adatb.bookaround.repositories.StockDao;
import com.adatb.bookaround.repositories.StoreDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

}
