package br.com.devisrael.helpdesk.service;

import br.com.devisrael.helpdesk.api.entity.Change;
import br.com.devisrael.helpdesk.api.entity.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

@Component
public interface TicketService {

    Ticket createOrUpdate(Ticket ticket);

    Ticket findById(String id);

    void delete(String id);

    Page<Ticket> listTickets(int page, int count);

    Change createChangeStatus(Change change);

    Iterable<Change> listChangeStatus(String ticketId);

    Page<Ticket> findByCurrentUser(int page, int count, String userId);

    Page<Ticket> findByParameter(int page, int count, String title, String status, String priority);

    Page<Ticket> findByParameterCurrentUser(int page, int count, String title, String status, String priority);

    Page<Ticket> findByNumber(int page, int count, Integer number);

    Iterable<Ticket> findAll();

    Page<Ticket> findByParameterAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUser);
}
