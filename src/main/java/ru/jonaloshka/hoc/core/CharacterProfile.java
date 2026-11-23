package ru.jonaloshka.hoc.core;

public record CharacterProfile(
        String id,
        String displayName,
        String title,
        String personality,
        String greetingLine,
        boolean isMainAntagonist
) {}
