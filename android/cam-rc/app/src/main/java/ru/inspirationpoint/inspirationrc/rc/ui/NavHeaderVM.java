package ru.inspirationpoint.inspirationrc.rc.ui;

import android.databinding.ObservableField;

public class NavHeaderVM {

    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> email = new ObservableField<>();

    public NavHeaderVM(String name, String email) {
        this.name.set(name);
        this.email.set(email);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setEmail(String email) {
        this.email.set(email);
    }
}
