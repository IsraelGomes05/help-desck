package br.com.devisrael.helpdesk.api.entity;

public enum Status {

    NEW,
    ASSIGNED,
    RESOLVED,
    APPROVED,
    DISAPPROVED,
    CLOSED;

    public static Status getStatus(String status) {
        return switch (status) {
            case "Resolved" -> RESOLVED;
            case "Approved" -> APPROVED;
            case "Disapproved" -> DISAPPROVED;
            case "Assigned" -> ASSIGNED;
            case "Closed" -> CLOSED;
            default -> NEW;
        };
    }

}
