package iu.piisj.facilitymanager.repository;

import iu.piisj.facilitymanager.ticket.Ticket;
import iu.piisj.facilitymanager.user.User;
import iu.piisj.facilitymanager.util.PersistenceProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import java.util.List;

@ApplicationScoped
public class TicketRepository {

    @Inject
    private PersistenceProvider persistenceProvider;

    private EntityManager getEntityManager() {
        return persistenceProvider.getEmf().createEntityManager();
    }

    public List<Ticket> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Ticket t LEFT JOIN FETCH t.reporter LEFT JOIN FETCH t.assignee ORDER BY t.createdAt DESC",
                    Ticket.class).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Ticket> findByReporterId(Long reporterId) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Ticket t LEFT JOIN FETCH t.reporter LEFT JOIN FETCH t.assignee" +
                    " WHERE t.reporter.id = :reporterId ORDER BY t.createdAt DESC",
                    Ticket.class).setParameter("reporterId", reporterId).getResultList();
        } finally {
            em.close();
        }
    }

    public Ticket findById(Long id) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                    "SELECT t FROM Ticket t" +
                    " LEFT JOIN FETCH t.reporter" +
                    " LEFT JOIN FETCH t.assignee" +
                    " LEFT JOIN FETCH t.comments c" +
                    " LEFT JOIN FETCH c.author" +
                    " WHERE t.id = :id",
                    Ticket.class).setParameter("id", id).getResultStream().findFirst().orElse(null);
        } finally {
            em.close();
        }
    }

    public void saveNew(Ticket ticket, Long reporterId, Long assigneeId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            ticket.setReporter(em.find(User.class, reporterId));
            if (assigneeId != null) {
                ticket.setAssignee(em.find(User.class, assigneeId));
            }
            em.persist(ticket);
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

    public void update(Ticket ticket, Long assigneeId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Ticket managed = em.find(Ticket.class, ticket.getId());
            managed.setTitle(ticket.getTitle());
            managed.setDescription(ticket.getDescription());
            managed.setCategory(ticket.getCategory());
            managed.setPriority(ticket.getPriority());
            managed.setStatus(ticket.getStatus());
            managed.setLocation(ticket.getLocation());
            managed.setEstimatedCost(ticket.getEstimatedCost());
            managed.setUpdatedAt(ticket.getUpdatedAt());
            managed.setResolvedAt(ticket.getResolvedAt());
            managed.setAssignee(assigneeId != null ? em.find(User.class, assigneeId) : null);
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

    public void delete(Long id) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Ticket ticket = em.find(Ticket.class, id);
            if (ticket != null) {
                em.remove(ticket);
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
