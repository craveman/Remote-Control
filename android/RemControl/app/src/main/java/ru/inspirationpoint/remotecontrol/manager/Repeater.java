package ru.inspirationpoint.remotecontrol.manager;

import android.databinding.ObservableField;

import java.io.Serializable;
import java.util.Objects;

public class Repeater implements Serializable {

    public ObservableField<String> ip = new ObservableField<>();
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> connectedToIp = new ObservableField<>();
    public ObservableField<String> connectedToName = new ObservableField<>();

    public Repeater() {
    }

    @Override
    public boolean equals(Object obj) {
        if (getClass() != obj.getClass())
            return false;
        Repeater temp = (Repeater) obj;
        return Objects.equals(ip.get(), temp.ip.get()) && Objects.equals(name.get(), temp.name.get());
    }

    @Override
    public int hashCode() {
        return Objects.requireNonNull(ip.get()).hashCode() + Objects.requireNonNull(name.get()).hashCode();
    }
}
