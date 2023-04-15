package com.adatb.bookaround.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractJpaDao<T extends Serializable> {

    @PersistenceContext
    EntityManager entityManager;
    private Class<T> entityClass;

    protected final void setEntityClass(Class<T> classToSet) {
        this.entityClass = classToSet;
    }

    public T find(long id) {
        return entityManager.find(entityClass, id);
    }

    public List<T> findAll() {
        return entityManager.createQuery("FROM " + entityClass.getName()).getResultList();
    }

    @Transactional
    public T create(T entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Transactional
    public T update(T entity) {
        entityManager.merge(entity);
        return entity;
    }

    @Transactional
    public void delete(long id) {
        T entity = find(id);
        if (entity != null) {
            entityManager.remove(entity);
        }
    }

}
