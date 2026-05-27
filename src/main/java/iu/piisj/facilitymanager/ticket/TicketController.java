package iu.piisj.facilitymanager.ticket;

import iu.piisj.facilitymanager.auth.AuthController;
import iu.piisj.facilitymanager.auth.SessionUser;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

@Named
@ViewScoped
public class TicketController implements Serializable {

    @Inject
    private TicketService ticketService;

    @Inject
    private AuthController authController;

    private List<Ticket> allTickets;
    private List<Ticket> filteredTickets;

    private TicketStatus filterStatus;
    private TicketPriority filterPriority;

    @PostConstruct
    public void init() {
        loadTickets();
    }

    private void loadTickets() {
        SessionUser user = authController.getCurrentUser();
        if (authController.isAdminOrTechnician()) {
            allTickets = ticketService.getAllTickets();
        } else {
            allTickets = ticketService.getTicketsByReporter(user.getId());
        }
        applyFilter();
    }

    public void applyFilter() {
        filteredTickets = allTickets.stream()
                .filter(t -> filterStatus == null   || t.getStatus()   == filterStatus)
                .filter(t -> filterPriority == null || t.getPriority() == filterPriority)
                .collect(Collectors.toList());
    }

    public void resetFilter() {
        filterStatus = null;
        filterPriority = null;
        applyFilter();
    }

    public void deleteTicket(Long id) {
        ticketService.delete(id);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Ticket gelöscht", null));
        loadTickets();
    }

    public List<Ticket> getFilteredTickets() { return filteredTickets; }

    public List<TicketStatus>   getStatuses()   { return ticketService.getStatuses(); }
    public List<TicketPriority> getPriorities() { return ticketService.getPriorities(); }

    public TicketStatus getFilterStatus() { return filterStatus; }
    public void setFilterStatus(TicketStatus filterStatus) { this.filterStatus = filterStatus; }

    public TicketPriority getFilterPriority() { return filterPriority; }
    public void setFilterPriority(TicketPriority filterPriority) { this.filterPriority = filterPriority; }
}
