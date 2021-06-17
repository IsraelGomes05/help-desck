package br.com.devisrael.helpdesk.controller;

import br.com.devisrael.helpdesk.api.dto.Summary;
import br.com.devisrael.helpdesk.api.entity.*;
import br.com.devisrael.helpdesk.security.JwtTokenUtil;
import br.com.devisrael.helpdesk.service.TicketService;
import br.com.devisrael.helpdesk.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;


@RestController
@RequestMapping("/api/ticket")
@CrossOrigin(origins = "*")
public class TicketController {

    @Autowired
    private TicketService ticketService;

    @Autowired
    protected JwtTokenUtil jwtTokenUtil;

    @Autowired
    private UserService userService;

    @PostMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> create(HttpServletRequest request, @RequestBody Ticket ticket,
                                                   BindingResult result) {
        var response = new Response<Ticket>();
        try {
            validateCreateTicket(ticket, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            ticket.setStatus(Status.getStatus("New"));
            ticket.setUser(userFromRequest(request));
            ticket.setDate(new Date());
            ticket.setNumber(generateNumber());
            var ticketPersisted = ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
        } catch (Exception e) {
            response.getErros().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateCreateTicket(Ticket ticket, BindingResult result) {
        if (ticket.getTitle() == null) {
            result.addError(new ObjectError("Ticket", "Title no information"));
        }
    }

    public User userFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        String email = jwtTokenUtil.getUsernameFromToken(token);
        return userService.findByEmail(email);
    }

    private Integer generateNumber() {
        var random = new Random();
        return random.nextInt(9999);
    }

    @PutMapping()
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<Ticket>> update(HttpServletRequest request, @RequestBody Ticket ticket,
                                                   BindingResult result) {
        var response = new Response<Ticket>();
        try {
            validateUpdateTicket(ticket, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            var ticketCurrent = ticketService.findById(ticket.getId());
            ticket.setStatus(ticketCurrent.getStatus());
            ticket.setUser(ticketCurrent.getUser());
            ticket.setDate(ticketCurrent.getDate());
            ticket.setNumber(ticketCurrent.getNumber());
            if (ticketCurrent.getAssignedUser() != null) {
                ticket.setAssignedUser(ticketCurrent.getAssignedUser());
            }
            var ticketPersisted = ticketService.createOrUpdate(ticket);
            response.setData(ticketPersisted);
        } catch (Exception e) {
            response.getErros().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateUpdateTicket(Ticket ticket, BindingResult result) {
        if (ticket.getId() == null) {
            result.addError(new ObjectError("Ticket", "Id no information"));
            return;
        }
        if (ticket.getTitle() == null) {
            result.addError(new ObjectError("Ticket", "Title no information"));
        }
    }


    @GetMapping(value = "{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> findById(@PathVariable("id") String id) {
        var response = new Response<Ticket>();
        var ticket = ticketService.findById(id);
        if (ticket == null) {
            response.getErros().add("Register not found id:" + id);
            return ResponseEntity.badRequest().body(response);
        }
        var changes = new ArrayList<Change>();
        Iterable<Change> changesCurrent = ticketService.listChangeStatus(ticket.getId());
        for (Change change : changesCurrent) {
            change.setTicket(null);
            changes.add(change);
        }
        ticket.setChanges(changes);
        response.setData(ticket);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER')")
    public ResponseEntity<Response<String>> delete(@PathVariable("id") String id) {
        var response = new Response<String>();
        var ticket = ticketService.findById(id);
        if (ticket == null) {
            response.getErros().add("Register not found id:" + id);
            return ResponseEntity.badRequest().body(response);
        }
        ticketService.delete(id);
        return ResponseEntity.ok(new Response<String>());
    }


    @GetMapping(value = "{page}/{count}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findAll(HttpServletRequest request, @PathVariable int page, @PathVariable int count) {

        var response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        var userRequest = userFromRequest(request);
        if (userRequest.getProfile().equals(Profile.ROLE_TECHNICIAN)) {
            tickets = ticketService.listTickets(page, count);
        } else if (userRequest.getProfile().equals(Profile.ROLE_COSTUMER)) {
            tickets = ticketService.findByCurrentUser(page, count, userRequest.getId());
        }
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    @GetMapping(value = "{page}/{count}/{number}/{title}/{status}/{priority}/{assigned}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Page<Ticket>>> findByParams(HttpServletRequest request,
                                                               @PathVariable int page,
                                                               @PathVariable int count,
                                                               @PathVariable Integer number,
                                                               @PathVariable String title,
                                                               @PathVariable String status,
                                                               @PathVariable String priority,
                                                               @PathVariable boolean assigned) {

        title = title.equals("uninformed") ? "" : title;
        status = status.equals("uninformed") ? "" : status;
        priority = priority.equals("uninformed") ? "" : priority;

        var response = new Response<Page<Ticket>>();
        Page<Ticket> tickets = null;
        if (number > 0) {
            tickets = ticketService.findByNumber(page, count, number);
        } else {
            var userRequest = userFromRequest(request);
            if (userRequest.getProfile().equals(Profile.ROLE_TECHNICIAN)) {
                if (assigned) {
                    tickets = ticketService.findByParameterAndAssignedUser(page, count, title, status, priority, userRequest.getId());
                } else {
                    tickets = ticketService.findByParameter(page, count, title, status, priority);
                }
            } else if (userRequest.getProfile().equals(Profile.ROLE_COSTUMER)) {
                tickets = ticketService.findByParameterAndAssignedUser(page, count, title, status, priority, userRequest.getId());
            }
        }
        response.setData(tickets);
        return ResponseEntity.ok(response);
    }

    @PutMapping(value = "/{id}/{status}")
    @PreAuthorize("hasAnyRole('CUSTOMER','TECHNICIAN')")
    public ResponseEntity<Response<Ticket>> Change(
            @PathVariable("id") String id,
            @PathVariable("status") String status,
            HttpServletRequest request,
            @RequestBody Ticket ticket,
            BindingResult result) {

        var response = new Response<Ticket>();
        try {
            validateChangeStatus(id, status, result);
            if (result.hasErrors()) {
                result.getAllErrors().forEach(error -> response.getErros().add(error.getDefaultMessage()));
                return ResponseEntity.badRequest().body(response);
            }
            var ticketCurrent = ticketService.findById(id);
            ticketCurrent.setStatus(Status.getStatus(status));
            if (status.equals("Assigned")) {
                ticketCurrent.setAssignedUser(userFromRequest(request));
            }
            var ticketPersisted = ticketService.createOrUpdate(ticketCurrent);
            var change = new Change();
            change.setUserChange(userFromRequest(request));
            change.setDateChangeStatus(new Date());
            change.setStatus(Status.getStatus(status));
            change.setTicket(ticketPersisted);
            ticketService.createChangeStatus(change);
            response.setData(ticketPersisted);
        } catch (Exception e) {
            response.getErros().add(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    private void validateChangeStatus(String id, String status, BindingResult result) {
        if (id == null || id.equals("")) {
            result.addError(new ObjectError("Ticket", "Id no information"));
            return;
        }
        if (status == null || status.equals("")) {
            result.addError(new ObjectError("Ticket", "Status no information"));
        }
    }

    @GetMapping(value = "/summary")
    public ResponseEntity<Response<Summary>> findChart() {
        var response = new Response<Summary>();
        Summary chart = new Summary();
        var amountNew = 0;
        var amountResolved = 0;
        var amountApproved = 0;
        var amountDisapproved = 0;
        var amountAssigned = 0;
        var amountClosed = 0;
        Iterable<Ticket> tickets = ticketService.findAll();
        if (tickets != null) {
            for (Ticket ticket : tickets) {
                if (ticket.getStatus().equals(Status.NEW)) {
                    amountNew++;
                }
                if (ticket.getStatus().equals(Status.RESOLVED)) {
                    amountResolved++;
                }
                if (ticket.getStatus().equals(Status.APPROVED)) {
                    amountApproved++;
                }
                if (ticket.getStatus().equals(Status.DISAPPROVED)) {
                    amountDisapproved++;
                }
                if (ticket.getStatus().equals(Status.ASSIGNED)) {
                    amountAssigned++;
                }
                if (ticket.getStatus().equals(Status.CLOSED)) {
                    amountClosed++;
                }
            }
        }
        chart.setAmountNew(amountNew);
        chart.setAmountResolved(amountResolved);
        chart.setAmountApproved(amountApproved);
        chart.setAmountDisapproved(amountDisapproved);
        chart.setAmountAssigned(amountAssigned);
        chart.setAmountClosed(amountClosed);
        response.setData(chart);
        return ResponseEntity.ok(response);
    }

}