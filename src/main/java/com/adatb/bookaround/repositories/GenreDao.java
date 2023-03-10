package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Genre;

public class GenreDao extends AbstractJpaDao<Genre> {
    public GenreDao() { this.setEntityClass(Genre.class); }
}
