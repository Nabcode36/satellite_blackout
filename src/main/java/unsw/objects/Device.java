package unsw.objects;

import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;
import unsw.utils.MathsHelper;

import java.util.Map;
import java.util.HashMap;

public class Device {
    private String id;
    private String type;
    private Angle position;
    private double range;

    private Map<String, FileInfoResponse> files = new HashMap<>();

    public Device(String id, String type, Angle position) {
        this.id = id;
        this.type = type;
        this.position = position;
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

    public Angle getPosition() {
        return position;
    }

    public double getRange() {
        return range;
    }

    public Map<String, FileInfoResponse> getFiles() {
        return files;
    }

    public void setRange(double range) {
        this.range = range;
    }

    /**
     * Adds file to the device. The device's storage does not need to be
     * updated as it has unlimited storage
     * @param f
     */
    public void addFile(File f) {
        FileInfoResponse r = new FileInfoResponse(f.getName(), f.getContent(), f.getSize(), true);
        files.put(f.getName(), r);
    }

     /**
     * Tells whether a valid satellite is in range
     * @param src
     * @param ref
     * @return
     */
    public boolean satelliteInRange(Satellite ref, Device src) {
        Double distance = MathsHelper.getDistance(ref.getHeight(), ref.getPosition(), src.getPosition());
        Boolean visible = MathsHelper.isVisible(ref.getHeight(), ref.getPosition(), src.getPosition());
        if (distance <= src.getRange() && visible) {
            for (String s : ref.getValidDevices()) {
                if (s.equals(src.getType())) {
                    return true;
                }
            }
        }
        return false;
    }
}

