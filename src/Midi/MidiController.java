package Midi;

import javax.sound.midi.MidiMessage;

public class MidiController {

    public static final byte MESSAGE_TYPE_NODE = (byte) 0x90;
    public static final byte MESSAGE_TYPE_COMAND = (byte) 0xb0;
    public static final byte VELOCITY_NONE = 0x00;
    public static final byte VELOCITY_FULL = 0x7f;

    private static MidiController midiController;
    private MidiDeviceConnector midiDeviceConnector;
    private MixTrackController mixTrackController;

    private MidiController() {
        midiDeviceConnector = new MidiDeviceConnector("Mixtrack");
        mixTrackController = new MixTrackController();
    }

    public void precessMessage(MidiMessage message, long timeStamp) {
        byte[] midiMessage = message.getMessage();
        int midiType = (int) (midiMessage[0] & 0xFF);
        int midiNode = (int) (midiMessage[1] & 0xFF);
        int midiVelocity = (int) (midiMessage[2] & 0xFF);

        System.out.println(Integer.toHexString(midiType) + " " + Integer.toHexString(midiNode) + " " + Integer.toHexString(midiVelocity));

        
    }



    public static MidiController getMidiController() {
        return midiController;
    }

    public MidiDeviceConnector getMidiDeviceConnector() {
        return midiDeviceConnector;
    }

    public MixTrackController getMixTrackController() {
        return mixTrackController;
    }

    public static void main(String[] args) {
        MidiController.midiController = new MidiController();
        MidiController.getMidiController().getMixTrackController().blackoutLEDs();
    }
}
