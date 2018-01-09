package Midi;

import Logic.PlayLights;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class MidiOrganizer {
    public static final int MESSAGE_TYPE_NODE = (byte) 0x90 & 0xFF;
    public static final int MESSAGE_TYPE_COMAND = (byte) 0xb0 & 0xFF;
    public static final int VELOCITY_NONE = 0;
    public static final int VELOCITY_FULL = (byte) 0x7f & 0xFF;

    private MidiDeviceConnector mixTrackDeviceConnector;
    private MixTrackController mixTrackController;
    private MidiDeviceConnector mpcDeviceConnector;

    public MidiOrganizer() {
        mixTrackDeviceConnector = new MidiDeviceConnector("Mixtrack");
        mixTrackController = new MixTrackController();
        mpcDeviceConnector = new MidiDeviceConnector("LoopBe");
    }

    public void processMidiMessage(MidiMessage message, long timeStamp, String sourceName) {
        byte[] midiMessage = message.getMessage();
        int midiType = (int) (midiMessage[0] & 0xFF);
        int midiNode = (int) (midiMessage[1] & 0xFF);
        int midiVelocity = (int) (midiMessage[2] & 0xFF);

        if (PlayLights.verbose) {
            System.out.println("Midi message received from " + sourceName + ": " + Integer.toHexString(midiType) + " " + Integer.toHexString(midiNode) + " " + Integer.toHexString(midiVelocity));
        }

        if (sourceName.toLowerCase().contains("Mixtrack".toLowerCase())) {
            // midi message from Mixtrack

            if (PlayLights.getPlayLights().getSongPlayer() != null) {
                // there is a song loaded or playing currently
                // check if a MixTrackController.PAD was pressed
                MixTrackController.PAD pressedPad = mixTrackController.getPadFromAddress((byte) midiNode);
                if (midiType == MESSAGE_TYPE_NODE && pressedPad != null && midiVelocity == VELOCITY_FULL) {
                    // the midi message was caused by pressing the pad pressedPad
                    PlayLights.getPlayLights().getSongPlayer().padPressed(pressedPad);
                } else if (midiType == MESSAGE_TYPE_NODE && midiNode == MixTrackController.PLAY_A_ADDRESS && midiVelocity == VELOCITY_FULL) {
                    PlayLights.getPlayLights().getSongPlayer().playPausePressed();
                } else if (midiType == MESSAGE_TYPE_NODE && midiNode == MixTrackController.SYNC_A_ADDRESS && midiVelocity == VELOCITY_FULL) {
                    PlayLights.getPlayLights().getSongPlayer().getTimeCode().stop();
                }

            } else {
                //there is NO song loaded or playing currently
            }

        } else if (sourceName.contains("LoopBe")) {

        }
    }

    public MidiMessage createMidiMessage(int messageType, int note, int velocity) {
        ShortMessage testMessage = new ShortMessage();
        try {
            testMessage.setMessage(messageType & 0xFF, note & 0xFF, velocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        return testMessage;
    }

    public MidiMessage createMidiMessage(int messageType, int channel, int note, int velocity) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(messageType & 0xff, channel & 0xff, note & 0xFF, velocity & 0xFF);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

        return message;
    }

    public MidiDeviceConnector getMixTrackDeviceConnector() {
        return mixTrackDeviceConnector;
    }

    public MixTrackController getMixTrackController() {
        return mixTrackController;
    }

    public static void main(String[] args) {
        PlayLights.verbose = true;
        new MidiOrganizer();
    }
}
