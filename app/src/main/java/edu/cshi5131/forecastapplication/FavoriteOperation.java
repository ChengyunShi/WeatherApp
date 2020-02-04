package edu.cshi5131.forecastapplication;

import android.app.Application;
import android.os.Bundle;

public class FavoriteOperation extends Application {
    private int flag;
    private Bundle data;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }
}
