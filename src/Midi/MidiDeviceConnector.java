package Midi;

import Logic.PlayLights;
import javafx.scene.control.Alert;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 06.01.17.
 */
public class MidiDeviceConnector {

    private Transmitter transmitter = null;
    private Receiver receiver = null;
    private String deviceName = "";

    public MidiDeviceConnector(String devNamePart) {
        connectDevice(devNamePart);

        MidiDevice md = getMidiDevice();
        Receiver mdR = null;
        Transmitter mdT = null;
        try {
            mdR = md.getReceiver();
        } catch (MidiUnavailableException e) {
            // do nothing
        }
        try {
            mdT = md.getTransmitter();
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        transmitter.setReceiver(mdR);
        mdT.setReceiver(receiver);
    }

    private void connectDevice(String devNamePart) {
        // Obtain information about all the installed devices.
        MidiDevice device = null;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        boolean found = false;

        for (MidiDevice.Info info : infos) {
            try {
                device = MidiSystem.getMidiDevice(info);
                if (PlayLights.verbose) {
                    System.out.println("Available device: " + device.getDeviceInfo().getName());
                }
            } catch (MidiUnavailableException e) {
                // Handle or throw exception...
                // e.printStackTrace();
            }
            if (device.getDeviceInfo().getName().toLowerCase().contains(devNamePart.toLowerCase())) {
                found = true;
                deviceName = device.getDeviceInfo().getName();
                try {
                    device.open();
                } catch (MidiUnavailableException e) {
                    e.printStackTrace();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Device is busy");
                    alert.setContentText("The " + devNamePart + " midi device is already used in an other application.\n" +
                            "Close the other application before you retry.");
                    alert.showAndWait();
                    System.exit(2);
                }

                try {
                    receiver = device.getReceiver();
                } catch (MidiUnavailableException e) {
                    // do nothing
                }
                try {
                    transmitter = device.getTransmitter();
                } catch (MidiUnavailableException e) {
                    // do nothing
                }
            }
        }

        if (found && PlayLights.verbose) {
            System.out.println("Connection with " + deviceName + " established.");
        }

        if (!found) {
            if (PlayLights.verbose) {
                new IllegalStateException("ERROR: No Device which contains " + devNamePart + " in its name found!").printStackTrace();
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Device not found");
            alert.setContentText("The " + devNamePart + " midi device was not found!\n" +
                    "Connect the device and restart the application");
            alert.showAndWait();
            System.exit(2);
            return;
        }
    }

    private Receiver getMidiReceiver(MidiDevice ownerOfReceiver) {
        return new MidiDeviceReceiver() {
            private boolean open = true;

            @Override
            public MidiDevice getMidiDevice() {
                return ownerOfReceiver;
            }

            @Override
            public void send(MidiMessage message, long timeStamp) {
                if (open) {
                    PlayLights.getInstance().getMidiOrganizer().processMidiMessage(message, timeStamp, deviceName);
                }
            }

            @Override
            public void close() {
                open = false;
            }
        };
    }

    private MidiDeviceTransmitter getTransMidiTransmitter(MidiDevice ownerOfTransmitter) {
        return new MidiDeviceTransmitter() {
            private Receiver receiver;

            @Override
            public MidiDevice getMidiDevice() {
                return ownerOfTransmitter;
            }

            @Override
            public void setReceiver(Receiver receiver) {
                this.receiver = receiver;
            }

            @Override
            public Receiver getReceiver() {
                return receiver;
            }

            @Override
            public void close() {

            }
        };
    }

    private MidiDevice getMidiDevice() {
        return new MidiDevice() {
            boolean isOpen = false;
            long openTime = System.currentTimeMillis();

            Receiver receiver = getMidiReceiver(this);
            Transmitter transmitter = getTransMidiTransmitter(this);

            @Override
            public Info getDeviceInfo() {
                return null;
            }

            @Override
            public void open() throws MidiUnavailableException {
                isOpen = true;
            }

            @Override
            public void close() {
                isOpen = false;
            }

            @Override
            public boolean isOpen() {
                return isOpen;
            }

            @Override
            public long getMicrosecondPosition() {
                return System.currentTimeMillis() - openTime;
            }

            @Override
            public int getMaxReceivers() {
                return 1;
            }

            @Override
            public int getMaxTransmitters() {
                return 1;
            }

            @Override
            public Receiver getReceiver() throws MidiUnavailableException {
                return receiver;
            }

            @Override
            public List<Receiver> getReceivers() {
                List<Receiver> receivers = new ArrayList<>();
                receivers.add(receiver);
                return receivers;
            }

            @Override
            public Transmitter getTransmitter() throws MidiUnavailableException {
                return transmitter;
            }

            @Override
            public List<Transmitter> getTransmitters() {
                List<Transmitter> transmitters = new ArrayList<>();
                transmitters.add(transmitter);
                return transmitters;
            }
        };
    }

    public Receiver getReceiver() {
        return receiver;
    }
}
