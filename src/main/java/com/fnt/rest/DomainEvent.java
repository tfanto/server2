package com.fnt.rest;

public class DomainEvent {

    private final String contents;

    public DomainEvent(String contents) {
        this.contents = contents;
    }

    public String getContents() {
        return contents;
    }

}

