package unsw.objects;

import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.ArrayList;
import java.util.List;

public abstract class Satellite {
    private String id;
    private String type;
    private double height;
    private Angle position;
    private double velocity;
    private double range;
    private Integer noFiles;
    private Double storage;

    private List<String> validDevices = new ArrayList<String>();
    private List<File> files = new ArrayList<File>();

    public Satellite(String id, String type, double height, Angle position) {
        this.id = id;
        this.type = type;
        this.height = height;
        this.position = position;
        this.noFiles = 0;
    }

    /*
     * Getters and Setters
     */
    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getHeight() {
        return height;
    }

    public Angle getPosition() {
        return position;
    }

    public Double getVelocity() {
        return velocity;
    }

    protected void setVelocity(Double velocity) {
        this.velocity = velocity;
    }

    public void setPosition(Angle position) {
        this.position = position;
    }

    public Double getRange() {
        return range;
    }

    protected void setRange(Double range) {
        this.range = range;
    }

    protected void setStorage(Double storage) {
        this.storage = storage;
    }

    public Integer getNoFiles() {
        return noFiles;
    }

    public Double getStorage() {
        return storage;
    }

    public List<String> getValidDevices() {
        return validDevices;
    }

    public List<File> getFiles() {
        return files;
    }

    /**
     * Add a device to the list of valid devices for a satellite
     * @param validDevices
     * @param device
     */
    public void addValidDevice(String device) {
        this.validDevices.add(device);
    }
    /**
     * Adds file to the storage and updates the remaining storage in a satellite whenever
     * it receives a file
     * @param f
     */
    public void addFile(File f) {
        files.add(f);
        this.storage = this.storage - f.getSize();
        this.noFiles++;
    }

    /**
     * Converts angular velocity in radians per minute to degrees per minute
     * for easily updating its angular position
     * @return
     */
    public double convertAngularVelocity() {
        Angle angularVelocity = Angle.fromRadians(velocity / height);
        return angularVelocity.toDegrees();
    }

    /**
     * Gets the angular position of the satellite after one tick (1 minute)
     */
    public abstract void positionAfterTick();

    /**
     * Tells whether two satellites are in range of each other
     * @param src
     * @param ref
     * @return
     */
    public boolean satellitesAreInRange(Satellite src, Satellite ref) {
        Double distance = MathsHelper.getDistance(src.getHeight(), src.getPosition(),
                                                  ref.getHeight(), ref.getPosition());
        Boolean visible = MathsHelper.isVisible(src.getHeight(), src.getPosition(),
                                                ref.getHeight(), ref.getPosition());
        if (distance <= src.getRange() && visible) {
            return true;
        }
        return false;
    }

    /**
     * Tells whether a valid device is in range
     * @param src
     * @param ref
     * @return
     */
    public boolean deviceInRange(Satellite src, Device ref) {
        Double distance = MathsHelper.getDistance(src.getHeight(), src.getPosition(), ref.getPosition());
        Boolean visible = MathsHelper.isVisible(src.getHeight(), src.getPosition(), ref.getPosition());
        if (distance <= src.getRange() && visible) {
            for (String s : validDevices) {
                if (s.equals(ref.getType())) {
                    return true;
                }
            }
        }
        return false;
    }
}

