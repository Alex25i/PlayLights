package Midi;

import javax.sound.midi.MidiMessage;

public class MidiController {

    private final int MESSAGE_TYPE_NODE = 0x90;
    private final int MESSAGE_TYPE_COMAND = 0xb0;
    private final int VELOCITY_NONE = 0x00;
    private final int VELOCITY_FULL = 0x7f;

    private static MidiController instance;
    private MidiDeviceConnector midiDeviceConnector;
    private MixTrackController mixTrackController;

    private MidiController() {
        midiDeviceConnector = new MidiDeviceConnector("Mixtrack");
        mixTrackController = new MixTrackController();

    }

    public static MidiController getInstance() {
        if (instance == null) {
            instance = new MidiController();
        }
        return instance;
    }

    public void precessMessage(MidiMessage message, long timeStamp) {
        byte[] midiMessage = message.getMessage();
        int midiType = (int) (midiMessage[0] & 0xFF);
        int midiNode = (int) (midiMessage[1] & 0xFF);
        int midiVelocity = (int) (midiMessage[2] & 0xFF);

        System.out.println(Integer.toHexString(midiType) + " " + Integer.toHexString(midiNode) + " " + Integer.toHexString(midiVelocity));

        if (midiType == MESSAGE_TYPE_NODE && midiNode == MixTrackController.SCRATCH_A_ADDRESS) {
            sendTestMessage();
        }
    }

    private void sendTestMessage() {
        mixTrackController.sendTestLedCommands();
    }

    public MidiDeviceConnector getMidiDeviceConnector() {
        return midiDeviceConnector;
    }

    public MixTrackController getMixTrackController() {
        return mixTrackController;
    }

    public static void main(String[] args) {
        getInstance();
    }
}
