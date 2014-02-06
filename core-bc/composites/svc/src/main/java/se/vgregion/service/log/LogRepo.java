package se.vgregion.service.log;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

/**
 * Repository class to access the db.
 */
@Repository
public class LogRepo {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * Creates a new instance.
     */
    public LogRepo() {
    }

    /**
     * getter for the entityManager.
     * @return entityManager.
     */
    public EntityManager getEntityManager() {
        return entityManager;
    }

    /**
     * Find data in the db by ites primary key.
     * @param clazz what type to find.
     * @param id search key to use.
     * @param <T> type that are being returned.
     * @return list with zero or more results.
     */
    public <T> T findByPrimaryKey(Class<T> clazz, Object id) {
        T result = entityManager.find(clazz, id);
        return result;
    }

    /*
    private void refresh(Object entity) {
        entityManager.refresh(entity);
    }
    */

    /**
     * Saves changes in a object into the db.
     * @param entity contains data that previously been created in the db.
     * @param <T> type of the return value and parameter.
     * @return a new version of the updated entity.
     */
    public <T> T merge(T entity) {
        return entityManager.merge(entity);
    }

    /**
     * Creates a new post in the db from the data in the provided entity.
     * @param entity data to create in the db.
     */
    @Transactional
    public void persist(Object entity) {
        entityManager.persist(entity);
        entityManager.flush();
    }

    /**
     * Removes data from the db.
     * @param entity representing the data to remove from the db.
     * @param <T> type of the argument and return value.
     */
    public <T> void delete(T entity) {
        entityManager.remove(entity);
    }

    /**
     * Creates a reference to a tupel in the db.
     * @param clazz type of the result to create.
     * @param primaryKey the key value for this data in the db.
     * @param <T> type of the result.
     * @return a reference to data in the db.
     */
    public <T> T getReference(Class<T> clazz, Object primaryKey) {
//        return entityManager.find(clazz, primaryKey);
        return entityManager.getReference(clazz, primaryKey);
    }

    /**
     * Finds all data from a certain class. Maximum 500000 hits.
     * @param clazz type to find.
     * @param <V> the generic class to find.
     * @return entities from the db.
     */
    public <V> List<V> findAll(Class<V> clazz) {
        return findAll(clazz, 0, 500000);
    }

    private String findIdField(Class<?> type) {
        Field[] declaredFields = type.getDeclaredFields();
        for (Field field : declaredFields) {
            Annotation[] declaredAnnotations = field.getDeclaredAnnotations();
            for (Annotation annotation : declaredAnnotations) {
                if (annotation instanceof Id || annotation instanceof EmbeddedId) {
                    return field.getName();
                }
            }
        }
        throw new RuntimeException("Couldn't find an Id of type " + type.getName());
    }

    /**
     * Finds all items of a certain type.
     * @param clazz type of objects to find.
     * @param offset where in the stream from the db to start collecting results.
     * @param maxResults how many, maximum, items to return.
     * @param <V> type of the entities.
     * @return Datra from the db as entities.
     */
    public <V> List<V> findAll(Class<V> clazz, int offset, int maxResults) {
        String joinClause = "";
        Field[] declaredFields = clazz.getDeclaredFields();
        outer:
        for (Field declaredField : declaredFields) {
            Annotation[] declaredAnnotations = declaredField.getDeclaredAnnotations();
            for (Annotation annotation : declaredAnnotations) {
                if (annotation instanceof Transient) {
                    continue outer;
                }
            }
            for (Annotation annotation : declaredAnnotations) {
                if (annotation instanceof ManyToOne) {
                    Class<?> type = declaredField.getType();
                    String id = findIdField(type);
                    joinClause += " left join fetch t." + declaredField.getName();
                    continue outer;
                }
            }
        }
        String queryString = "select t from " + clazz.getSimpleName() + " t" + joinClause;
        Query query = entityManager.createQuery(queryString);
        query.setFirstResult(offset);
        query.setMaxResults(maxResults);
        return query.getResultList();
    }

}
