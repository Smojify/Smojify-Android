package com.smojify.smojify;

public class Reaction {
    private String inputText;
    private String trackUri;

    public Reaction(String inputText, String trackUri) {
        this.inputText = inputText;
        this.trackUri = trackUri;
    }

    public String getInputText() {
        return inputText;
    }

    public String getTrackUri() {
        return trackUri;
    }
}
