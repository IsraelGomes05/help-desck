package br.com.devisrael.helpdesk.repository;

import br.com.devisrael.helpdesk.api.entity.Change;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChangeStatusRepository extends MongoRepository<Change, String> {

    Iterable<Change> findByTicketIdOrderByDateChangeStatusDesc(String ticketId);
}
