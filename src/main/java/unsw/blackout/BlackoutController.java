package unsw.blackout;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import unsw.objects.Satellite;
import unsw.objects.StandardSatellite;
import unsw.objects.TeleportingSatellite;
import unsw.objects.RelaySatellite;
import unsw.objects.Device;
import unsw.objects.File;


public class BlackoutController {
    private List<Device> devices = new ArrayList<Device>();
    private List<Satellite> satellites = new ArrayList<Satellite>();

    // Task 1
    public void createDevice(String deviceId, String type, Angle position) {
        Device d = new Device(deviceId, type, position);
        if (type.equals("HandheldDevice")) {
            d.setRange(50000.0);
        }
        if (type.equals("LaptopDevice")) {
            d.setRange(100000.0);
        }
        if (type.equals("DesktopDevice")) {
            d.setRange(200000.0);
        }
        devices.add(d);
    }

    public void removeDevice(String deviceId) {
        devices.remove(findDevice(deviceId));
    }

    public void createSatellite(String satelliteId, String type, double height, Angle position) {
        if (type.equals("StandardSatellite")) {
            Satellite s = new StandardSatellite(satelliteId, type, height, position);
            satellites.add(s);
        }
        if (type.equals("TeleportingSatellite")) {
            Satellite s = new TeleportingSatellite(satelliteId, type, height, position);
            satellites.add(s);
        }
        if (type.equals("RelaySatellite")) {
            Satellite s = new RelaySatellite(satelliteId, type, height, position);
            satellites.add(s);
        }
    }

    public void removeSatellite(String satelliteId) {
        satellites.remove(findSatellite(satelliteId));
    }

    public List<String> listDeviceIds() {
        List<String> deviceIds = new ArrayList<String>();
        for (Device d : devices) {
            deviceIds.add(d.getId());
        }
        return deviceIds;
    }

    public List<String> listSatelliteIds() {
        List<String> satelliteIds = new ArrayList<String>();
        for (Satellite s : satellites) {
            satelliteIds.add(s.getId());
        }
        return satelliteIds;
    }

    public void addFileToDevice(String deviceId, String filename, String content) {
        File f = new File(filename, content);
        Device d = findDevice(deviceId);
        d.addFile(f);
    }

    public EntityInfoResponse getInfo(String id) {
        // Check if id refers to a Satellite
        if (findDevice(id) == null) {
            Satellite s = findSatellite(id);
            EntityInfoResponse e = new EntityInfoResponse(id, s.getPosition(), s.getHeight(), s.getType());
            return e;
        }
        // Check if id refers to a Device
        if (findDevice(id) != null) {
            Device d = findDevice(id);
            EntityInfoResponse e = new EntityInfoResponse(id, d.getPosition(), RADIUS_OF_JUPITER,
                                                            d.getType(), d.getFiles());
            return e;
        }
        return null;
    }

    // Additional functions for Task 1
    private Satellite findSatellite(String id) {
        for (Satellite s : satellites) {
            if (s.getId().equals(id)) {
                return s;
            }
        }
        return null;
    }

    private Device findDevice(String id) {
        for (Device d : devices) {
            if (d.getId().equals(id)) {
                return d;
            }
        }
        return null;
    }

    // Task 2
    public void simulate() {
        for (Satellite s : satellites) {
            s.positionAfterTick();
        }
    }

    /**
     * Simulate for the specified number of minutes.
     * You shouldn't need to modify this function.
     */
    public void simulate(int numberOfMinutes) {
        for (int i = 0; i < numberOfMinutes; i++) {
            simulate();
        }
    }

    public List<String> communicableEntitiesInRange(String id) {
        List<String> entities = new ArrayList<String>();
        // THis function acts as a depth first search for when relay satellites are involved
        findEntitiesInRange(id, entities);

        // An entity can't be in range with itself
        entities.remove(entities.indexOf(id));

        // This is done to remove duplicate entities from relay satellites from findEntitiesInRange
        Set<String> set = new HashSet<>(entities);
        entities.clear();
        entities.addAll(set);

        // Standard satellites cannot communicate to Desktop Devices even via relay satellites
        if (findDevice(id) == null) {
            Satellite s = findSatellite(id);
            if (s.getType().equals("StandardSatellite")) {
                for (String e : entities) {
                    if (findDevice(e) != null) {
                        Device d = findDevice(e);
                        if (d.getType().equals("DesktopDevice")) {
                            entities.remove(entities.indexOf(e));
                        }
                    }
                }
            }
        }

        // Desktop Devices cannot communicate to Standard Satellites even via relay satellites
        if (findDevice(id) != null) {
            Device d = findDevice(id);
            if (d.getType().equals("DesktopDevice")) {
                for (String e : entities) {
                    if (findDevice(e) == null) {
                        Satellite s = findSatellite(e);
                        if (s.getType().equals("StandardSatellite")) {
                            entities.remove(entities.indexOf(e));
                        }
                    }
                }
            }
        }

        return entities;
    }

    public void sendFile(String fileName, String fromId, String toId) throws FileTransferException {
        // TODO: Task 2 c)
    }

    // Additional functions for task 2

    public void findEntitiesInRange(String id, List<String> entities) {
        entities.add(id);
        if (findDevice(id) == null) {
            Satellite src = findSatellite(id);
            for (Satellite ref : satellites) {
                if (src.satellitesAreInRange(src, ref)) {
                    if (!entities.contains(ref.getId())) {
                        entities.add(ref.getId());
                        if (ref.getType().equals("RelaySatellite")) {
                            // Recursive call for when a relay satellite is in range
                            findEntitiesInRange(ref.getId(), entities);
                        }
                    }
                }
            }
            for (Device d : devices) {
                if (src.deviceInRange(src, d)) {
                    entities.add(d.getId());
                }
            }
        }
        if (findDevice(id) != null) {
            Device src = findDevice(id);
            for (Satellite ref : satellites) {
                if (src.satelliteInRange(ref, src) && !entities.contains(ref.getId())) {
                    entities.add(ref.getId());
                    if (ref.getType().equals("RelaySatellite")) {
                        // Recursive call for when a relay satellite is in range
                        findEntitiesInRange(ref.getId(), entities);
                    }
                }
            }
        }
    }

    // Task 3
    public void createDevice(String deviceId, String type, Angle position, boolean isMoving) {
        createDevice(deviceId, type, position);
        // TODO: Task 3
    }

    public void createSlope(int startAngle, int endAngle, int gradient) {
        // TODO: Task 3
        // If you are not completing Task 3 you can leave this method blank :)
    }
}
