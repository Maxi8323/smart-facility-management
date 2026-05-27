package iu.piisj.facilitymanager.ticket;

import iu.piisj.facilitymanager.repository.TicketRepository;
import iu.piisj.facilitymanager.repository.UserRepository;
import iu.piisj.facilitymanager.user.User;
import iu.piisj.facilitymanager.user.UserRole;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TicketService {

    @Inject
    private TicketRepository ticketRepository;

    @Inject
    private UserRepository userRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public List<Ticket> getTicketsByReporter(Long reporterId) {
        return ticketRepository.findByReporterId(reporterId);
    }

    public Ticket getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    public void createTicket(Ticket ticket, Long reporterId) {
        ticketRepository.saveNew(ticket, reporterId, null);
    }

    public void updateTicket(Ticket ticket, Long assigneeId) {
        ticket.setUpdatedAt(LocalDateTime.now());
        if (ticket.getStatus() == TicketStatus.ERLEDIGT || ticket.getStatus() == TicketStatus.ABGELEHNT) {
            if (ticket.getResolvedAt() == null) {
                ticket.setResolvedAt(LocalDateTime.now());
            }
        } else {
            ticket.setResolvedAt(null);
        }
        ticketRepository.update(ticket, assigneeId);
    }

    public void updateStatus(Ticket ticket, TicketStatus newStatus) {
        ticket.setStatus(newStatus);
        ticket.setUpdatedAt(LocalDateTime.now());
        if (newStatus == TicketStatus.ERLEDIGT || newStatus == TicketStatus.ABGELEHNT) {
            ticket.setResolvedAt(LocalDateTime.now());
        }
        Long assigneeId = ticket.getAssignee() != null ? ticket.getAssignee().getId() : null;
        ticketRepository.update(ticket, assigneeId);
    }

    public void delete(Long id) {
        ticketRepository.delete(id);
    }

    public List<User> getAssignableUsers() {
        return userRepository.findAll().stream()
                .filter(u -> u.isActive() && u.getRole() != UserRole.REPORTER)
                .collect(Collectors.toList());
    }

    public List<TicketStatus>   getStatuses()   { return List.of(TicketStatus.values()); }
    public List<TicketPriority> getPriorities() { return List.of(TicketPriority.values()); }
    public List<TicketCategory> getCategories() { return List.of(TicketCategory.values()); }
}
