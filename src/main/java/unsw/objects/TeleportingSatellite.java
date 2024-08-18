package unsw.objects;

import unsw.utils.Angle;

public class TeleportingSatellite extends Satellite {
    public TeleportingSatellite(String id, String type, double height, Angle position) {
        super(id, type, height, position);
        setVelocity(1000.0);
        addValidDevice("HandheldDevice");
        addValidDevice("LaptopDevice");
        addValidDevice("DesktopDevice");
        setRange(200000.0);
        setStorage(200.0);
    }

    @Override
    public void positionAfterTick() {
        Double degreePos = getPosition().toDegrees() + convertAngularVelocity();
        if (degreePos >= 180.00) {
            degreePos = 0.00;
            // Velocity becomes negative as the satellite now changes its orbiting direction
            Double newVelocity = getVelocity() * -1.00;
            setVelocity(newVelocity);
        }
        Angle newPos = Angle.fromDegrees(degreePos);
        setPosition(newPos);
    }
}
