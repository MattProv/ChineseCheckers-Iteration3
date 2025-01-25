package org.example;

import javafx.scene.paint.Color;

import java.io.Serializable;

public class SerializableColor implements Serializable {

    public int red;
    public int green;
    public int blue;

    public SerializableColor(int red, int green, int blue) {
        this.red = red;
        this.green = green;
        this.blue = blue;

    }

    public Color getColor() {
        return Color.rgb(red, green, blue);
    }
}
