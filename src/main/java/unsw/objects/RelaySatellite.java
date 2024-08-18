package unsw.objects;

import unsw.utils.Angle;

public class RelaySatellite extends Satellite {
    public RelaySatellite(String id, String type, double height, Angle position) {
        super(id, type, height, position);
        setVelocity(1500.0);
        addValidDevice("HandheldDevice");
        addValidDevice("LaptopDevice");
        addValidDevice("DesktopDevice");
        setRange(300000.0);
        setStorage(0.0);
    }

    @Override
    public void positionAfterTick() {
        Double currentPos = getPosition().toDegrees();
        if (currentPos < 190.00 && currentPos > 140.00) {
            Double degreePos = getPosition().toDegrees() - convertAngularVelocity();
            if (degreePos > 190.00 || degreePos < 140.00) {
                // Velocity becomes negative as the satellite now changes its orbiting direction
                // after exceeding the region bounds ([140, 190]).
                Double newVelocity = getVelocity() * -1.00;
                setVelocity(newVelocity);
            }
            Angle newPos = Angle.fromDegrees(degreePos);
            setPosition(newPos);
        } else {
            if (currentPos >= 345.00) {
                Double degreePos = getPosition().toDegrees() + convertAngularVelocity();
                Angle newPos = Angle.fromDegrees(degreePos);
                setPosition(newPos);
            }
            if (currentPos < 345.00) {
                Double degreePos = getPosition().toDegrees() - convertAngularVelocity();
                Angle newPos = Angle.fromDegrees(degreePos);
                setPosition(newPos);
            }
        }
    }
}
