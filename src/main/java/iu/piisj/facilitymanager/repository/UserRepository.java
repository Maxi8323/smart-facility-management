package iu.piisj.facilitymanager.repository;

import iu.piisj.facilitymanager.user.User;
import iu.piisj.facilitymanager.util.PersistenceProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

@ApplicationScoped
public class UserRepository {

    @Inject
    private PersistenceProvider persistenceProvider;

    private EntityManager getEntityManager() {
        return persistenceProvider.getEmf().createEntityManager();
    }

    public List<User> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

    public User findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }

    public User findByUsername(String username) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public User findByEmail(String email) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public void save(User user) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if (user.getId() == null) {
                em.persist(user);
            } else {
                em.merge(user);
            }
            tx.commit();
        } catch (RuntimeException ex) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        } finally {
            em.close();
        }
    }
}
