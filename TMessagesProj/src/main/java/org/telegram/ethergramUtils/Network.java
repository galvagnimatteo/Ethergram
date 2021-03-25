package org.telegram.ethergramUtils;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class Network {

    String name;
    int imageId;

    public Network(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public int getImageId() {
        return imageId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(int imageSource) {
        this.imageId = imageId;
    }

}
