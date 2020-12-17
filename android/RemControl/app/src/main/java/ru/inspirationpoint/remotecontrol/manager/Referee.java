package ru.inspirationpoint.remotecontrol.manager;

import androidx.databinding.ObservableField;

import java.util.Objects;

public class Referee {

    public ObservableField<String> ip = new ObservableField<>();
    public ObservableField<String> name = new ObservableField<>();

    public Referee() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Referee referee = (Referee) o;
        return Objects.equals(ip, referee.ip) &&
                Objects.equals(name, referee.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(ip, name);
    }
}
