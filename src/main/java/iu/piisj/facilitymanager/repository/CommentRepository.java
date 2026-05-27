package iu.piisj.facilitymanager.repository;

import iu.piisj.facilitymanager.comment.TicketComment;
import iu.piisj.facilitymanager.ticket.Ticket;
import iu.piisj.facilitymanager.user.User;
import iu.piisj.facilitymanager.util.PersistenceProvider;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

@ApplicationScoped
public class CommentRepository {

    @Inject
    private PersistenceProvider persistenceProvider;

    private EntityManager getEntityManager() {
        return persistenceProvider.getEmf().createEntityManager();
    }

    public void save(String content, Long ticketId, Long authorId) {
        EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Ticket ticket = em.find(Ticket.class, ticketId);
            User author   = em.find(User.class, authorId);
            em.persist(new TicketComment(content, ticket, author));
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
