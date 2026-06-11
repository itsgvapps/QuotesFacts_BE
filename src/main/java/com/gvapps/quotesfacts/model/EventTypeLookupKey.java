package com.gvapps.quotesfacts.model;


public record EventTypeLookupKey(
        String eventGroup,
        String eventKey
) {
}