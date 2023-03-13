package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Genre;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class GenreDao extends AbstractJpaDao<Genre> {
    public GenreDao() { this.setEntityClass(Genre.class); }

    /**
     * Feladat: Műfajok mellé kigyűjteni, hogy hány, az adott műfajba tartozó könyv található az
     * adatbázisban (triviális lekérdezéssel).
     * @return Műfaj neve és a hozzá tartozó könyvek száma.
     */
    public Map<String, Long> getNumberOfBooksByGenre() {
        String jpql = "SELECT genre.genreId.genreName, COUNT(genre.genreId.genreName) as numberOfBooks " +
                "FROM Genre genre " +
                "GROUP BY genre.genreId.genreName";

        List<Object[]> resultList = entityManager.createQuery(jpql, Object[].class)
                .getResultList();

        Map<String, Long> resultsConverted = new HashMap<>();

        for (Object[] result : resultList) {
            String genreName = (String) result[0];
            Long numberOfBooks = (Long) result[1];
            resultsConverted.put(genreName, numberOfBooks);
        }

        return resultsConverted;
    }

}
