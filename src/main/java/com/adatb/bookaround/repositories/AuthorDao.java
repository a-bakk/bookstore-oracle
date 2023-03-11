package com.adatb.bookaround.repositories;

import com.adatb.bookaround.entities.Author;
import org.springframework.stereotype.Repository;

@Repository
public class AuthorDao extends AbstractJpaDao<Author> {
    public AuthorDao() {this.setEntityClass(Author.class);}
}
