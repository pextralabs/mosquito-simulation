package br.ufes.inf.lprm.context.scenario;

import br.ufes.inf.lprm.context.model.Entity;

public class Person extends Entity {
    private Location location;
    private Speed speed;

    public Person(String id) {
        super(id);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Speed getSpeed() {
        return speed;
    }

    public void setSpeed(Speed speed) {
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "Person: " + UID;
    }

    static public LatLng walk(Person person, double x, double y) {
        double earthRadius = (6.37814) * Math.pow(10, 6);
        LatLng latLng = person.getLocation().getValue();
        double nextLatitude = latLng.getLatitude() + (y / earthRadius) * (180 / Math.PI);
        double nextLongitude = latLng.getLongitude() + (x / earthRadius) * (180 / Math.PI) / Math.cos(latLng.getLatitude() * Math.PI / 180);
        return new LatLng(nextLatitude, nextLongitude);
    }
}
