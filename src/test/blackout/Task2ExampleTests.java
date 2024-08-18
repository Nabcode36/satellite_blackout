package blackout;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2ExampleTests {
    @Test
    public void testEntitiesInRange() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));

        System.out.println(controller.communicableEntitiesInRange("Satellite1"));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC", "Satellite2"),
                controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"),
                controller.communicableEntitiesInRange("Satellite2"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"), controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceD"), controller.communicableEntitiesInRange("Satellite3"));
    }

    @Test
    public void testEntitiesInRangeViaRelays() {
        BlackoutController controller = new BlackoutController();

        // Creates 3 satellites and 1 device
        // Initially Device A should be in range of Satellite 1 via Satellite 2 (relay)
        // But after Satellite 2 is removed Satellite 1 should only have satellite 3 in range
        controller.createSatellite("Satellite1", "StandardSatellite", 4162 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        controller.createSatellite("Satellite2", "RelaySatellite", 18095 + RADIUS_OF_JUPITER, Angle.fromDegrees(290));
        controller.createSatellite("Satellite3", "TeleportingSatellite", 12218 + RADIUS_OF_JUPITER,
                                   Angle.fromDegrees(18));
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(258));

        System.out.println(controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "Satellite2", "Satellite3"),
        controller.communicableEntitiesInRange("Satellite1"));

        controller.removeSatellite("Satellite2");
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite3"),
        controller.communicableEntitiesInRange("Satellite1"));

        // Adding back Satellite 2 and adding more devices and satellites
        controller.createSatellite("Satellite2", "RelaySatellite", 18095 + RADIUS_OF_JUPITER, Angle.fromDegrees(290));
        controller.createSatellite("Satellite4", "RelaySatellite", 17955 + RADIUS_OF_JUPITER, Angle.fromDegrees(244));
        controller.createSatellite("Satellite5", "RelaySatellite", 18890 + RADIUS_OF_JUPITER, Angle.fromDegrees(205));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(194));

        // Satellite1 should be in range of every entity in the space and specifically for Device B
        // it is in range via multiple relay satellites
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceA", "DeviceB", "Satellite2", "Satellite3",
                                                      "Satellite4", "Satellite5"),
                controller.communicableEntitiesInRange("Satellite1"));
    }

    @Test
    public void notSupportedEntitiesInRange() {
        BlackoutController controller = new BlackoutController();

        // Initially create 2 Satellites and (Standard and Teleporting) and
        // 1 Desktop Device
        controller.createSatellite("Satellite1", "StandardSatellite", 12259 + RADIUS_OF_JUPITER,
                                   Angle.fromDegrees(55));
        controller.createSatellite("Satellite2", "TeleportingSatellite", 11620 + RADIUS_OF_JUPITER,
                                   Angle.fromDegrees(100));
        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(110));

        // Only Satellite 2 would be in range of SatelliteA and not DeviceA, as it is a DesktopDevice
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"),
                controller.communicableEntitiesInRange("Satellite1"));

        // Since Satellite 2 is a Teleporting Satellite, Device A is in range
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "DeviceA"),
                controller.communicableEntitiesInRange("Satellite2"));

        // Standard Satellites still shouldn't be able to access Desktop Devices via relay satellites
        controller.createSatellite("Satellite3", "RelaySatellite", 12756 + RADIUS_OF_JUPITER, Angle.fromDegrees(77));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2", "Satellite3"),
                controller.communicableEntitiesInRange("Satellite1"));

        // Checking that vice versa is true. Satellite3 is not in range of Device A according to the simulation
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"),
                controller.communicableEntitiesInRange("DeviceA"));
    }

    @Test
    public void testSomeExceptionsForSend() {
        // just some of them... you'll have to test the rest
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertThrows(FileTransferException.VirtualFileNotFoundException.class,
                () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate(msg.length() * 2);
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
                () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    }

    @Test
    public void testMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(337.95), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testExample() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(msg.length() * 2);
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate(msg.length());
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        // Hints for further testing:
        // - What about checking about the progress of the message half way through?
        // - Device/s get out of range of satellite
        // ... and so on.
    }

    @Test
    public void testRelayMovement() {
        // Task 2
        // Example from the specification
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));

        // moves in negative direction
        assertEquals(
                new EntityInfoResponse("Satellite1", Angle.fromDegrees(180), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
                controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(178.77), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(177.54), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(176.31), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));

        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(170.18), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(24);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        // edge case
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(139.49), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        // coming back
        controller.simulate(1);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(146.85), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testTeleportingMovement() {
        // Test for expected teleportation movement behaviour
        BlackoutController controller = new BlackoutController();

        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(0));

        controller.simulate();
        Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == 1);

        // It should take 250 simulations to reach theta = 180.
        // Simulate until Satellite1 reaches theta=180
        controller.simulate(250);

        // Verify that Satellite1 is now at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);
    }
}
