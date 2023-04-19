package com.adatb.bookaround.services;

import com.adatb.bookaround.entities.Order;
import com.adatb.bookaround.entities.Store;
import com.adatb.bookaround.models.BookWithAuthorsAndGenres;
import com.adatb.bookaround.models.StoreWithBusinessHours;
import com.adatb.bookaround.repositories.BookDao;
import com.adatb.bookaround.repositories.OrderDao;
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
        Map<Store, List<BookWithAuthorsAndGenres>> resultStock = storeDao.findStockForEachStore();

        if (resultStock == null || resultStock.isEmpty()) {
            logger.warn("Stock could not be loaded");
            return new HashMap<>();
        }

        return resultStock;
    }

}
