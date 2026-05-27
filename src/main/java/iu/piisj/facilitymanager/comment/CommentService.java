package iu.piisj.facilitymanager.comment;

import iu.piisj.facilitymanager.repository.CommentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CommentService {

    @Inject
    private CommentRepository commentRepository;

    public void addComment(String content, Long ticketId, Long authorId) {
        commentRepository.save(content, ticketId, authorId);
    }
}
