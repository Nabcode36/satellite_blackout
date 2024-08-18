package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task1ExampleTests {
    @Test
    public void testExample() {
        // Task 1
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 3 devices
        // 2 devices are in view of the satellite
        // 1 device is out of view of the satellite
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));

        assertEquals(new EntityInfoResponse("DeviceA", Angle.fromDegrees(30), RADIUS_OF_JUPITER, "HandheldDevice"),
                controller.getInfo("DeviceA"));
        assertEquals(new EntityInfoResponse("DeviceB", Angle.fromDegrees(180), RADIUS_OF_JUPITER, "LaptopDevice"),
                controller.getInfo("DeviceB"));
        assertEquals(new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice"),
                controller.getInfo("DeviceC"));
    }

    @Test
    public void testDelete() {
        // Task 1
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 3 devices and deletes them
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(30));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(180));
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1"), controller.listSatelliteIds());
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "DeviceC"), controller.listDeviceIds());

        controller.removeDevice("DeviceA");
        controller.removeDevice("DeviceB");
        controller.removeDevice("DeviceC");
        controller.removeSatellite("Satellite1");
    }

    @Test
    public void basicFileSupport() {
        // Task 1
        BlackoutController controller = new BlackoutController();

        // Creates 1 device and add some files to it
        controller.createDevice("DeviceC", "DesktopDevice", Angle.fromDegrees(330));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC"), controller.listDeviceIds());
        assertEquals(new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice"),
                controller.getInfo("DeviceC"));

        controller.addFileToDevice("DeviceC", "Hello World", "My first file!");
        Map<String, FileInfoResponse> expected = new HashMap<>();
        expected.put("Hello World",
                new FileInfoResponse("Hello World", "My first file!", "My first file!".length(), true));
        assertEquals(
                new EntityInfoResponse("DeviceC", Angle.fromDegrees(330), RADIUS_OF_JUPITER, "DesktopDevice", expected),
                controller.getInfo("DeviceC"));
    }

    @Test
    public void listSatelliteIds() {
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("SatelliteA", "StandardSatellite",
                                   1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(280));
        controller.createSatellite("SatelliteB", "TeleportingSatellite",
                                   1300 + RADIUS_OF_JUPITER, Angle.fromDegrees(130));
        controller.createSatellite("SatelliteC", "RelaySatellite",
                                   2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(330));

        List<String> expected = new ArrayList<String>();
        expected.add("SatelliteA");
        expected.add("SatelliteB");
        expected.add("SatelliteC");

        assertEquals(controller.listSatelliteIds(), expected);
    }

    @Test
    public void listDeviceIds() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(280));
        controller.createDevice("DeviceB", "DesktopDevice", Angle.fromDegrees(130));
        controller.createDevice("DeviceC", "LaptopDevice", Angle.fromDegrees(330));

        List<String> expected = new ArrayList<String>();
        expected.add("DeviceA");
        expected.add("DeviceB");
        expected.add("DeviceC");

        assertEquals(controller.listDeviceIds(), expected);
    }

    @Test
    public void removeDeviceIds() {
        BlackoutController controller = new BlackoutController();
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(280));
        controller.createDevice("DeviceB", "DesktopDevice", Angle.fromDegrees(130));
        controller.createDevice("DeviceC", "LaptopDevice", Angle.fromDegrees(330));

        controller.removeDevice("DeviceA");
        controller.removeDevice("DeviceC");

        List<String> expected = new ArrayList<String>();
        expected.add("DeviceB");

        assertEquals(controller.listDeviceIds(), expected);
    }

    @Test
    public void removeSatelliteIds() {
        BlackoutController controller = new BlackoutController();
        controller.createSatellite("SatelliteA", "StandardSatellite",
                                   1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(280));
        controller.createSatellite("SatelliteB", "TeleportingSatellite",
                                   1300 + RADIUS_OF_JUPITER, Angle.fromDegrees(130));
        controller.createSatellite("SatelliteC", "RelaySatellite",
                                   2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(330));

        controller.removeSatellite("SatelliteB");

        List<String> expected = new ArrayList<String>();
        expected.add("SatelliteA");
        expected.add("SatelliteC");

        assertEquals(controller.listSatelliteIds(), expected);
    }

    @Test
    public void deviceWithMultipleFiles() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(25));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(70));

        // Device A has 2 files, while Device B has 1
        controller.addFileToDevice("DeviceA", "Welcome!", "Hello everyone!");
        controller.addFileToDevice("DeviceA", "Rules", "Do not eat");
        controller.addFileToDevice("DeviceB", "Version", "16.0.3");

        Map<String, FileInfoResponse> expected1 = new HashMap<>();
        expected1.put("Welcome!",
                new FileInfoResponse("Welcome!", "Hello everyone!", "Hello everyone!".length(), true));
        expected1.put("Rules",
                new FileInfoResponse("Rules", "Do not eat", "Do not eat".length(), true));

        Map<String, FileInfoResponse> expected2 = new HashMap<>();
        expected2.put("Version",
                new FileInfoResponse("Version", "16.0.3", "16.0.3".length(), true));

        assertEquals(
                new EntityInfoResponse("DeviceA", Angle.fromDegrees(25), RADIUS_OF_JUPITER,
                                 "DesktopDevice", expected1), controller.getInfo("DeviceA"));
        assertEquals(
                new EntityInfoResponse("DeviceB", Angle.fromDegrees(70), RADIUS_OF_JUPITER,
                                 "HandheldDevice", expected2), controller.getInfo("DeviceB"));
    }

    @Test
    public void removeFile() {
        BlackoutController controller = new BlackoutController();

        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(25));

        controller.addFileToDevice("DeviceA", "Welcome!", "Hello everyone!");

        // getInfo on Device A before the device removal should contain the map in its response
        Map<String, FileInfoResponse> expected = new HashMap<>();
        expected.put("Welcome!",
                     new FileInfoResponse("Welcome!", "Hello everyone!",
                                          "Hello everyone!".length(), true));
        assertEquals(new EntityInfoResponse("DeviceA", Angle.fromDegrees(25), RADIUS_OF_JUPITER,
                                            "DesktopDevice", expected), controller.getInfo("DeviceA"));

        // getInfo on Device A after the device removal shouldn't contain the map in its response
        controller.removeDevice("DeviceA");
        // Recreating Device A without adding file to replicate file removal from Device A
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(25));

        assertEquals(new EntityInfoResponse("DeviceA", Angle.fromDegrees(25), RADIUS_OF_JUPITER,
                                      "DesktopDevice"), controller.getInfo("DeviceA"));
    }
}
