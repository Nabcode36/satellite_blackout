package unsw.objects;

import unsw.utils.Angle;

public class StandardSatellite extends Satellite {
    public StandardSatellite(String id, String type, double height, Angle position) {
        super(id, type, height, position);
        setVelocity(2500.0);
        addValidDevice("HandheldDevice");
        addValidDevice("LaptopDevice");
        setRange(150000.0);
        setStorage(80.0);
    }

    @Override
    public void positionAfterTick() {
        Double degreePos = getPosition().toDegrees() - convertAngularVelocity();
        Angle newPos = Angle.fromDegrees(degreePos);
        setPosition(newPos);
    }
}
