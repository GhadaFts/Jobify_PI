package com.jobify.interview.entity;

public enum InterviewType {
    TECHNICAL("Technical Interview"),
    HR("HR Interview"),
    MANAGERIAL("Managerial Interview"),
    ON_SITE("On-Site Interview"),
    REMOTE("Remote Interview");

    private final String displayName;

    InterviewType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
