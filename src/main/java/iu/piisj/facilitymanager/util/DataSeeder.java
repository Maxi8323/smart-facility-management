package iu.piisj.facilitymanager.util;

import iu.piisj.facilitymanager.auth.AuthService;
import iu.piisj.facilitymanager.repository.CommentRepository;
import iu.piisj.facilitymanager.repository.TicketRepository;
import iu.piisj.facilitymanager.repository.UserRepository;
import iu.piisj.facilitymanager.ticket.*;
import iu.piisj.facilitymanager.user.User;
import iu.piisj.facilitymanager.user.UserRole;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebListener
public class DataSeeder implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        UserRepository    userRepo    = CDI.current().select(UserRepository.class).get();
        AuthService       authService = CDI.current().select(AuthService.class).get();
        TicketRepository  ticketRepo  = CDI.current().select(TicketRepository.class).get();
        CommentRepository commentRepo = CDI.current().select(CommentRepository.class).get();

        if (!userRepo.findAll().isEmpty()) {
            return; // Bereits geseedet
        }

        // ===== Benutzer =====
        User admin = new User("admin",
                authService.hashPassword("admin123"),
                "admin@facility.local",
                "Administrator",
                UserRole.ADMIN);
        userRepo.save(admin);

        User tech = new User("tech1",
                authService.hashPassword("tech123"),
                "tech1@facility.local",
                "Techniker Max Müller",
                UserRole.TECHNICIAN);
        userRepo.save(tech);

        User reporter = new User("reporter1",
                authService.hashPassword("rep123"),
                "reporter1@facility.local",
                "Melderin Anna Schmidt",
                UserRole.REPORTER);
        userRepo.save(reporter);

        // ===== Tickets =====

        // T1 – OFFEN, KRITISCH, HEIZUNG
        Ticket t1 = new Ticket(
                "Heizungsausfall Erdgeschoss",
                "Die Hauptheizung im Erdgeschoss ist seit heute Morgen komplett ausgefallen. "
                + "Alle Räume sind unbeheizt, Außentemperatur unter 5 °C.",
                TicketCategory.HEIZUNG,
                TicketPriority.KRITISCH,
                "EG – Hauptkorridor",
                null);
        ticketRepo.saveNew(t1, reporter.getId(), null);

        // T2 – ERLEDIGT, NIEDRIG, ELEKTRO
        Ticket t2 = new Ticket(
                "Lichtschalter Konferenzraum 2 defekt",
                "Der Lichtschalter lässt sich nicht mehr betätigen. Licht dauerhaft an.",
                TicketCategory.ELEKTRO,
                TicketPriority.NIEDRIG,
                "1. OG – Konferenzraum 2",
                null);
        t2.setStatus(TicketStatus.ERLEDIGT);
        t2.setEstimatedCost(new BigDecimal("45.00"));
        t2.setUpdatedAt(LocalDateTime.now().minusDays(2));
        t2.setResolvedAt(LocalDateTime.now().minusDays(2));
        ticketRepo.saveNew(t2, reporter.getId(), tech.getId());

        // T3 – IN_BEARBEITUNG, MITTEL, SANITAER
        Ticket t3 = new Ticket(
                "Wasserhahn in Gemeinschaftsküche tropft",
                "Im Gemeinschaftsraum im 2. OG tropft der Küchenhahn kontinuierlich. Wasserverschwendung.",
                TicketCategory.SANITAER,
                TicketPriority.MITTEL,
                "2. OG – Gemeinschaftsküche",
                null);
        t3.setStatus(TicketStatus.IN_BEARBEITUNG);
        t3.setUpdatedAt(LocalDateTime.now().minusDays(1));
        ticketRepo.saveNew(t3, admin.getId(), tech.getId());

        // T4 – OFFEN, HOCH, IT
        Ticket t4 = new Ticket(
                "Netzwerk-Switch im Serverraum ausgefallen",
                "Switch für VLAN 10 nicht mehr erreichbar. Ca. 15 Arbeitsplätze ohne Netzwerk.",
                TicketCategory.IT,
                TicketPriority.HOCH,
                "Keller – Serverraum",
                null);
        ticketRepo.saveNew(t4, tech.getId(), null);

        // T5 – ABGELEHNT, NIEDRIG, SONSTIGES
        Ticket t5 = new Ticket(
                "Fenster in Büro 305 klemmt leicht",
                "Fenster lässt sich nur schwer öffnen. Kein akuter Handlungsbedarf laut Hausmeister.",
                TicketCategory.SONSTIGES,
                TicketPriority.NIEDRIG,
                "3. OG – Büro 305",
                null);
        t5.setStatus(TicketStatus.ABGELEHNT);
        t5.setUpdatedAt(LocalDateTime.now().minusDays(5));
        t5.setResolvedAt(LocalDateTime.now().minusDays(5));
        ticketRepo.saveNew(t5, reporter.getId(), null);

        // T6 – IN_BEARBEITUNG, HOCH, SONSTIGES
        Ticket t6 = new Ticket(
                "Aufzug macht laute Geräusche",
                "Aufzug in Gebäude B gibt beim Anfahren der 3. Etage ein lautes Quietschen von sich. "
                + "Sicherheitscheck erforderlich.",
                TicketCategory.SONSTIGES,
                TicketPriority.HOCH,
                "Gebäude B – Aufzug",
                null);
        t6.setStatus(TicketStatus.IN_BEARBEITUNG);
        t6.setEstimatedCost(new BigDecimal("350.00"));
        t6.setUpdatedAt(LocalDateTime.now());
        ticketRepo.saveNew(t6, admin.getId(), tech.getId());

        // ===== Kommentare =====
        commentRepo.save(
                "Heizung wird heute Nachmittag geprüft. Bitte Kellerraum freigeben.",
                t1.getId(), tech.getId());
        commentRepo.save(
                "Danke für die schnelle Reaktion! Wir frieren hier oben.",
                t1.getId(), reporter.getId());
        commentRepo.save(
                "Ersatzteil für die Dichtung wurde bestellt. Reparatur voraussichtlich nächste Woche.",
                t3.getId(), tech.getId());
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {}
}
