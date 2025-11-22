package com.jobify.interview.entity;

public enum InterviewStatus {
    SCHEDULED("Scheduled"),
    RESCHEDULED("Rescheduled"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled"),
    NO_SHOW("No Show");

    private final String displayName;

    InterviewStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isActive() {
        return this == SCHEDULED || this == RESCHEDULED;
    }

    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED || this == NO_SHOW;
    }
}
