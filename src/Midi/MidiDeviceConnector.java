package Midi;

import javax.sound.midi.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Alex on 06.01.17.
 */
public class MidiDeviceConnector {

    private Transmitter transmitter = null;
    private Receiver receiver = null;

    public MidiDeviceConnector(String devNamePart) {


        // Obtain information about all the installed devices.
        MidiDevice device = null;
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        for (MidiDevice.Info info : infos) {
            try {
                device = MidiSystem.getMidiDevice(info);
                System.out.println(device.getDeviceInfo().getName());
            } catch (MidiUnavailableException e) {
                // Handle or throw exception...
            }
            if (device.getDeviceInfo().getName().contains(devNamePart)) {
                try {
                    device.open();
                } catch (MidiUnavailableException e) {
                    e.printStackTrace();
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

        System.out.println("Finished!");
    }

    public static Receiver getMidiReceiver(MidiDevice ownerOfReceiver) {
        return new MidiDeviceReceiver() {
            private boolean open = true;

            @Override
            public MidiDevice getMidiDevice() {
                return ownerOfReceiver;
            }

            @Override
            public void send(MidiMessage message, long timeStamp) {
                if (open) {
                    MidiPro.messageReceived(message, timeStamp);
                }
            }

            @Override
            public void close() {
                open = false;
            }
        };
    }

    public static MidiDeviceTransmitter getTransMidiTransmitter(MidiDevice ownerOfTransmitter) {
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

    public static MidiDevice getMidiDevice() {
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

    public Transmitter getTransmitter() {
        return transmitter;
    }

    public Receiver getReceiver() {
        return receiver;
    }
}