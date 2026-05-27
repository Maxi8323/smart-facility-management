package iu.piisj.facilitymanager.ticket;

import iu.piisj.facilitymanager.auth.AuthController;
import iu.piisj.facilitymanager.user.User;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class TicketFormController implements Serializable {

    @Inject
    private TicketService ticketService;

    @Inject
    private AuthController authController;

    private Long ticketId;
    private Ticket ticket;
    private boolean editMode;

    private Long selectedAssigneeId;

    public void init() {
        if (ticketId != null) {
            ticket = ticketService.getTicketById(ticketId);
            if (ticket == null) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ticket nicht gefunden", null));
                return;
            }
            editMode = true;
            selectedAssigneeId = ticket.getAssignee() != null ? ticket.getAssignee().getId() : null;
        } else {
            ticket = new Ticket();
            editMode = false;
            selectedAssigneeId = null;
        }
    }

    public String save() {
        if (editMode) {
            ticketService.updateTicket(ticket, selectedAssigneeId);
        } else {
            Long reporterId = authController.getCurrentUser().getId();
            ticketService.createTicket(ticket, reporterId);
        }
        return "/tickets.xhtml?faces-redirect=true";
    }

    public List<TicketCategory> getCategories() { return ticketService.getCategories(); }
    public List<TicketPriority> getPriorities() { return ticketService.getPriorities(); }
    public List<TicketStatus>   getStatuses()   { return ticketService.getStatuses(); }
    public List<User>           getAssignableUsers() { return ticketService.getAssignableUsers(); }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Ticket getTicket() { return ticket; }

    public boolean isEditMode() { return editMode; }

    public Long getSelectedAssigneeId() { return selectedAssigneeId; }
    public void setSelectedAssigneeId(Long selectedAssigneeId) { this.selectedAssigneeId = selectedAssigneeId; }
}
