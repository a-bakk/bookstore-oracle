package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Genre;
import org.springframework.stereotype.Repository;

@Repository
public class GenreDao extends AbstractJpaDao<Genre> {
    public GenreDao() { this.setEntityClass(Genre.class); }
}
