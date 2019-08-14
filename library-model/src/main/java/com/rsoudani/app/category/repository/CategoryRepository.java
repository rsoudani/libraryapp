package com.rsoudani.app.category.repository;


import com.rsoudani.app.category.model.Category;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

public class CategoryRepository {

    EntityManager em;

    public Category add(final Category category) {
        em.persist(category);
        return category;
    }

    public Category findById(Long id) {
        if (id == null) {
            return null;
        }
        return em.find(Category.class, id);
    }

    public void update(Category category) {
        em.merge(category);
    }

    public List<Category> findAll(String orderField) {
        TypedQuery<Category> query = em.createQuery("SELECT c FROM Category c ORDER BY c." + orderField, Category.class);
        return query.getResultList();

    }

    public boolean alreadyExists(Category category) {
        final StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT 1 FROM Category c where c.name = :name");
        if (category.getId() != null) {
            jpql.append(" AND c.id != :id");
        }

        final Query query = em.createQuery(jpql.toString());
        query.setParameter("name", category.getName());
        if (category.getId() != null) {
            query.setParameter("id", category.getId());
        }
        return query.setMaxResults(1).getResultList().size() > 0;
    }

    public boolean  existById(Long id) {
        final StringBuilder jpql = new StringBuilder();
        jpql.append("SELECT 1 FROM Category c where c.id = :id");

        final Query query = em.createQuery(jpql.toString());
        query.setParameter("id", id);
        return query.setMaxResults(1).getResultList().size() > 0;
    }
}
