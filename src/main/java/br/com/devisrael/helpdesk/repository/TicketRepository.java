package br.com.devisrael.helpdesk.repository;

import br.com.devisrael.helpdesk.api.entity.Status;
import br.com.devisrael.helpdesk.api.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TicketRepository extends MongoRepository<Ticket, String> {

    Page<Ticket> findByUserIdOrderByDate(Pageable pages, String userId);

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusIgnoreCaseContainingAndPriorityIgnoreCaseContainingOrderByDateDateAsc(
            String title, Status status, String priority, Pageable pageable
    );

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusIgnoreCaseContainingAndPriorityIgnoreCaseContainingAndUserIdOrderByDateDateAsc(
            String title, Status status, String priority, Pageable pageable
    );

    Page<Ticket> findByTitleIgnoreCaseContainingAndStatusIgnoreCaseContainingAndPriorityIgnoreCaseContainingAndAssignedUserIdOrderByDateDateAsc(
            String title, Status status, String priority, Pageable pageable
    );

    Page<Ticket> findByNumber(Integer number, Pageable pageable);
}
