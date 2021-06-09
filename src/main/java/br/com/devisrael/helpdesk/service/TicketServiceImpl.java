package br.com.devisrael.helpdesk.service;

import br.com.devisrael.helpdesk.api.entity.Change;
import br.com.devisrael.helpdesk.api.entity.Ticket;
import br.com.devisrael.helpdesk.repository.ChangeStatusRepository;
import br.com.devisrael.helpdesk.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private ChangeStatusRepository changeStatusRepository;

    @Override
    public Ticket createOrUpdate(Ticket ticket) {
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket findById(String id) {
        return ticketRepository.findById(id).get();
    }

    @Override
    public void delete(String id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public Page<Ticket> listTickets(int page, int count) {
        var pageRequest = PageRequest.of(page, count);
        return ticketRepository.findAll(pageRequest);
    }

    @Override
    public Change createChangeStatus(Change change) {
        return this.changeStatusRepository.save(change);
    }

    @Override
    public Iterable<Change> listChangeStatus(String ticketId) {
        return this.changeStatusRepository.findByTicketIdOrderByDateChangeStatusDesc(ticketId);
    }

    @Override
    public Page<Ticket> findByCurrentUser(int page, int count, String userId) {
        var pageRequest = PageRequest.of(page, count);
        return ticketRepository.findByUserIdOrderByDate(pageRequest, userId);
    }

    @Override
    public Page<Ticket> findByParameter(int page, int count, String title, String status, String priority) {
        var pageRequest = PageRequest.of(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityOrderByDateDateAsc(title, status, priority, pageRequest);
    }

    @Override
    public Page<Ticket> findByParameterCurrentUser(int page, int count, String title, String status, String priority) {
        var pageRequest = PageRequest.of(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndUserIdOrderByDateDateAsc(title, status, priority, pageRequest);
    }

    @Override
    public Page<Ticket> findByNumber(int page, int count, Integer number) {
        var pageRequest = PageRequest.of(page, count);
        return ticketRepository.findByNumber(number, pageRequest);
    }

    @Override
    public Iterable<Ticket> findAll() {
        return ticketRepository.findAll();
    }

    @Override
    public Page<Ticket> findByParameterAndAssignedUser(int page, int count, String title, String status, String priority, String assignedUser) {
        var pageRequest = PageRequest.of(page, count);
        return ticketRepository.findByTitleIgnoreCaseContainingAndStatusAndPriorityAndAssignedUserIdOrderByDateDateAsc(title, status, priority, pageRequest);
    }
}
