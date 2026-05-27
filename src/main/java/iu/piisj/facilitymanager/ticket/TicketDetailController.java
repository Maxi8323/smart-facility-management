package iu.piisj.facilitymanager.ticket;

import iu.piisj.facilitymanager.auth.AuthController;
import iu.piisj.facilitymanager.comment.CommentService;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;

@Named
@ViewScoped
public class TicketDetailController implements Serializable {

    @Inject
    private TicketService ticketService;

    @Inject
    private CommentService commentService;

    @Inject
    private AuthController authController;

    private Long ticketId;
    private Ticket ticket;
    private String newComment;

    public void init() throws IOException {
        ticket = ticketService.getTicketById(ticketId);

        if (ticket == null) {
            FacesContext.getCurrentInstance().getExternalContext()
                    .responseSendError(404, "Ticket nicht gefunden");
            FacesContext.getCurrentInstance().responseComplete();
            return;
        }

        if (authController.isReporter()) {
            Long currentUserId = authController.getCurrentUser().getId();
            if (!ticket.getReporter().getId().equals(currentUserId)) {
                FacesContext.getCurrentInstance().getExternalContext()
                        .responseSendError(403, "Kein Zugriff");
                FacesContext.getCurrentInstance().responseComplete();
            }
        }
    }

    public void addComment() {
        if (newComment == null || newComment.isBlank()) {
            FacesContext.getCurrentInstance().addMessage("commentForm:commentText",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Kommentar darf nicht leer sein.", null));
            return;
        }

        Long authorId = authController.getCurrentUser().getId();
        commentService.addComment(newComment.trim(), ticket.getId(), authorId);
        newComment = null;

        ticket = ticketService.getTicketById(ticketId);
    }

    public boolean canComment() {
        if (!authController.isLoggedIn() || ticket == null) {
            return false;
        }
        if (authController.isAdminOrTechnician()) {
            return true;
        }
        return authController.isReporter()
                && ticket.getReporter().getId().equals(authController.getCurrentUser().getId());
    }

    public Long getTicketId() { return ticketId; }
    public void setTicketId(Long ticketId) { this.ticketId = ticketId; }

    public Ticket getTicket() { return ticket; }

    public String getNewComment() { return newComment; }
    public void setNewComment(String newComment) { this.newComment = newComment; }
}
