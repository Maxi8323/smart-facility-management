package iu.piisj.facilitymanager.util;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

@ApplicationScoped
public class PersistenceProvider {

    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("facilityPU");

    public EntityManagerFactory getEmf() {
        return emf;
    }

    @PreDestroy
    public void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}