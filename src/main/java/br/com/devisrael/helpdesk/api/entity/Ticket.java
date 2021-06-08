package br.com.devisrael.helpdesk.api.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document
public class Ticket {

    @Id
    private String id;

    @DBRef(lazy = true)
    private User user;

    private Date date;

    private String title;

    private Integer number;

    @DBRef(lazy = true)
    private User assignedUser;

    private String description;

    private String image;

    private Priority priority;

    private Status status;

    private List<Change> changes;
}
