package Midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class MidiOrganizer {
    public static boolean verbose = false;

    public static final byte MESSAGE_TYPE_NODE = (byte) 0x90;
    public static final byte MESSAGE_TYPE_COMAND = (byte) 0xb0;
    public static final byte VELOCITY_NONE = (byte) 0x00;
    public static final byte VELOCITY_FULL = (byte) 0x7f;

    private static MidiOrganizer midiOrganizer;
    private MidiDeviceConnector mixTrackDeviceConnector;
    private MixTrackController mixTrackController;
    private MidiDeviceConnector mpcDeviceConnector;

    private MidiOrganizer() {
        mixTrackDeviceConnector = new MidiDeviceConnector("Mixtrack");
        mixTrackController = new MixTrackController();
        mpcDeviceConnector = new MidiDeviceConnector("LoopBe");

        // testing
        for (int j = 6; j <= 0x06; j++) {
            for (int i = 0x5; i <= 0x5; i++) {
                mpcDeviceConnector.getReceiver().send(createMidiMessage(MidiOrganizer.MESSAGE_TYPE_NODE,
                        0x01, i, MidiOrganizer.VELOCITY_FULL), -1);
            }
        }


    }

    public void processMixTrackMessage(MidiMessage message, long timeStamp, String sourceName) {
        byte[] midiMessage = message.getMessage();
        int midiType = (int) (midiMessage[0] & 0xFF);
        int midiNode = (int) (midiMessage[1] & 0xFF);
        int midiVelocity = (int) (midiMessage[2] & 0xFF);

        if (MidiOrganizer.verbose) {
            System.out.println("Midi message received from " + sourceName + ": " + Integer.toHexString(midiType) + " " + Integer.toHexString(midiNode) + " " + Integer.toHexString(midiVelocity));
        }
    }

    public MidiMessage createMidiMessage(byte messageType, byte note, byte velocity) {
        ShortMessage testMessage = new ShortMessage();
        try {
            testMessage.setMessage(messageType & 0xFF, note & 0xFF, velocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        return testMessage;
    }

    public MidiMessage createMidiMessage(int messageType, int channel, int note, int velocity) {
        ShortMessage testMessage = new ShortMessage();
        try {
            testMessage.setMessage(messageType & 0xff, channel & 0xff, note & 0xFF, velocity & 0xFF);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        return testMessage;
    }


    public static MidiOrganizer getMidiOrganizer() {
        return midiOrganizer;
    }

    public MidiDeviceConnector getMixTrackDeviceConnector() {
        return mixTrackDeviceConnector;
    }

    public MixTrackController getMixTrackController() {
        return mixTrackController;
    }

    public static void main(String[] args) {
        MidiOrganizer.verbose = true;
        MidiOrganizer.midiOrganizer = new MidiOrganizer();
        MidiOrganizer.getMidiOrganizer().getMixTrackController().blackoutStartLEDs();
    }
}
