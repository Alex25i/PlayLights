package Midi;

import javax.sound.midi.MidiMessage;

public class MidiOrganizer {
    public static boolean verbose = false;

    public static final byte MESSAGE_TYPE_NODE = (byte) 0x90;
    public static final byte MESSAGE_TYPE_COMAND = (byte) 0xb0;
    public static final byte VELOCITY_NONE = 0x00;
    public static final byte VELOCITY_FULL = 0x7f;

    private static MidiOrganizer midiOrganizer;
    private MidiDeviceConnector midiDeviceConnector;
    private MixTrackController mixTrackController;

    private MidiOrganizer() {
        midiDeviceConnector = new MidiDeviceConnector("Mixtrack");
        mixTrackController = new MixTrackController();
    }

    public void processMessage(MidiMessage message, long timeStamp) {
        byte[] midiMessage = message.getMessage();
        int midiType = (int) (midiMessage[0] & 0xFF);
        int midiNode = (int) (midiMessage[1] & 0xFF);
        int midiVelocity = (int) (midiMessage[2] & 0xFF);

        if (MidiOrganizer.verbose) {
            System.out.println(Integer.toHexString(midiType) + " " + Integer.toHexString(midiNode) + " " + Integer.toHexString(midiVelocity));
        }
    }


    public static MidiOrganizer getMidiOrganizer() {
        return midiOrganizer;
    }

    public MidiDeviceConnector getMidiDeviceConnector() {
        return midiDeviceConnector;
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
