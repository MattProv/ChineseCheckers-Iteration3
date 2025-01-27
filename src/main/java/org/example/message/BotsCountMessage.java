package org.example.message;

public class BotsCountMessage extends Message{

    private final int botsCount;

    public BotsCountMessage(int botsCount) {
        super(MessageType.BOTS_COUNT);
        this.botsCount = botsCount;
    }

    public int getBotsCount() {
        return botsCount;
    }

    @Override
    public String toString() {
        return Integer.toString(botsCount);
    }
}
