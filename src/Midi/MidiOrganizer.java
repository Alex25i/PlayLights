package Midi;

import Logic.PlayLights;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class MidiOrganizer {
    public static final int MESSAGE_TYPE_NODE_ON = (byte) 0x90 & 0xFF;
    public static final int MESSAGE_TYPE_NODE_OFF = (byte) 0x80 & 0xFF;
    public static final int MESSAGE_TYPE_COMMAND = (byte) 0xB0 & 0xFF;
    public static final int MESSAGE_TYPE_PITCH_BEND = (byte) 0xE0 & 0xFF;
    public static final int VELOCITY_NONE = 0;
    public static final int VELOCITY_FULL = (byte) 0x7f & 0xFF;

    private MidiDeviceConnector mixTrackDeviceConnector;
    private MixTrackController mixTrackController;
    private MidiDeviceConnector mpcDeviceConnector;
    private boolean connectionLost;

    public MidiOrganizer() {
        mixTrackDeviceConnector = new MidiDeviceConnector("Mixtrack");
        mixTrackController = new MixTrackController();
        mpcDeviceConnector = new MidiDeviceConnector("LoopBe");
        connectionLost = false;
    }

    public void processMidiMessage(MidiMessage message, long timeStamp, String sourceName) {
        byte[] midiMessage = message.getMessage();

        if (midiMessage.length < 3) {
            int errorType = (int) (midiMessage[0] & 0xFF);
            if (errorType == 247) {
                // device disconnected
                if (!connectionLost) {
                    connectionLost = true;

                    mixTrackController.reconnectRoutine();
                }
                return;
            }
        }
        int midiType = (int) (midiMessage[0] & 0xFF);
        int midiNode = (int) (midiMessage[1] & 0xFF);
        int midiVelocity = (int) (midiMessage[2] & 0xFF);

        if (PlayLights.verbose) {
            System.out.println("Midi message received from " + sourceName + ": " + Integer.toHexString(midiType) + " "
                    + Integer.toHexString(midiNode) + " " + Integer.toHexString(midiVelocity));
        }

        if (sourceName.toLowerCase().contains("Mixtrack".toLowerCase())) {
            // midi message from Mixtrack

            if (PlayLights.getPlayLights().getSongPlayer() != null) {
                // there is a song loaded or playing currently
                // check if a MixTrackController.PAD was pressed
                MixTrackController.PAD pressedPad = MixTrackController.getPadFromAddress((byte) midiNode);
                if (midiType == MESSAGE_TYPE_NODE_ON && pressedPad != null && midiVelocity == VELOCITY_FULL) {
                    // the midi message was caused by pressing the pad pressedPad
                    PlayLights.getPlayLights().getSongPlayer().padPressed(pressedPad);
                } else if (midiType == MESSAGE_TYPE_NODE_ON && midiNode == MixTrackController.PLAY_B_ADDRESS && midiVelocity == VELOCITY_FULL) {
                    PlayLights.getPlayLights().getSongPlayer().playPausePressed();
                } else if (midiType == MESSAGE_TYPE_NODE_ON && midiNode == MixTrackController.CUE_B_LED_ADDRESS && midiVelocity == VELOCITY_FULL) {
                    PlayLights.getPlayLights().getSongPlayer().resetPressed();
                }

            } else {
                //there is NO song loaded or playing currently
            }

        } else if (sourceName.contains("LoopBe")) {

        }
    }

    public void sendMidiMessage(int messageType, int note, int velocity, MidiDeviceConnector deviceConnector) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(messageType & 0xFF, note & 0xFF, velocity);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        deviceConnector.getReceiver().send(message, -1);
    }

    public void sendMidiMessage(int messageType, int channel, int note, int velocity, MidiDeviceConnector deviceConnector) {
        ShortMessage message = new ShortMessage();
        try {
            message.setMessage(messageType & 0xff, channel & 0xff, note & 0xFF, velocity & 0xFF);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        deviceConnector.getReceiver().send(message, -1);
    }

    public void sendMidiMessage(ShortMessage message, MidiDeviceConnector deviceConnector) {
        deviceConnector.getReceiver().send(message, -1);
    }

    public ShortMessage createMidiMessage(int messageType, int channel, int note, int velocity) {
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

    public void setMixTrackDeviceConnector(MidiDeviceConnector mixTrackDeviceConnector) {
        this.mixTrackDeviceConnector = mixTrackDeviceConnector;
    }

    public MixTrackController getMixTrackController() {
        return mixTrackController;
    }

    public MidiDeviceConnector getMpcDeviceConnector() {
        return mpcDeviceConnector;
    }
}
