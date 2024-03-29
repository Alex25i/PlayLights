package Midi;

import GUI.SongCenterController;
import Logic.PlayLights;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;

public class MidiOrganizer {
    public static final int MESSAGE_TYPE_NOTE_ON = (byte) 0x90 & 0xFF;
    public static final int MESSAGE_TYPE_NOTE_OFF = (byte) 0x80 & 0xFF;
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
            int errorType = (midiMessage[0] & 0xFF);
            if (errorType == 247) {
                // device disconnected
                if (!connectionLost) {
                    connectionLost = true;

                    mixTrackController.reconnectRoutine();
                }
                return;
            }
        }
        int midiType = (midiMessage[0] & 0xFF);
        int midiNode = (midiMessage[1] & 0xFF);
        int midiVelocity = (midiMessage[2] & 0xFF);

        if (PlayLights.verbose) {
            System.out.println("Midi message received from " + sourceName + ": " + Integer.toHexString(midiType) + " "
                    + Integer.toHexString(midiNode) + " " + Integer.toHexString(midiVelocity));
        }

        if (sourceName.toLowerCase().contains("Mixtrack".toLowerCase())) {
            // midi message from Mixtrack

            if (PlayLights.getInstance().getSongPlayer() != null) {
                // there is a song loaded or playing currently
                // check if a MixTrackController.PAD was pressed
                MixTrackController.PAD pressedPad = MixTrackController.getPadFromAddress((byte) midiNode);
                if (midiType == MESSAGE_TYPE_NOTE_ON && pressedPad != null && midiVelocity == VELOCITY_FULL) {
                    // the midi message was caused by pressing the pad pressedPad
                    PlayLights.getInstance().getSongPlayer().padPressed(pressedPad);
                } else if (midiType == MESSAGE_TYPE_NOTE_ON && midiNode == MixTrackController.PLAY_B_ADDRESS && midiVelocity == VELOCITY_FULL) {
                    PlayLights.getInstance().getSongPlayer().playPausePressed();
                } else if (midiType == MESSAGE_TYPE_NOTE_ON && midiNode == MixTrackController.CUE_B_LED_ADDRESS && midiVelocity == VELOCITY_FULL) {
                    PlayLights.getInstance().getSongPlayer().resetPressed();
                }

            } else if (SongCenterController.getInstance() != null) {
                // there is songCenter currently displayed, but there is NO song loaded currently
                if (midiType == MESSAGE_TYPE_COMMAND && midiNode == MixTrackController.BROWSE_TURN_ADDRESS) {
                    if (midiVelocity == VELOCITY_FULL) {
                        SongCenterController.getInstance().selectionLeft();
                    } else {
                        // if it is not left, is it always right
                        SongCenterController.getInstance().selectionRight();
                    }
                } else if (midiType == MESSAGE_TYPE_NOTE_ON && midiNode == MixTrackController.BROWSE_PUSH_ADDRESS && midiVelocity == VELOCITY_FULL) {
                    SongCenterController.getInstance().selectionSelectPress();
                } else if (midiType == MESSAGE_TYPE_NOTE_ON && midiNode == MixTrackController.BROWSE_BACK_ADDRESS && midiVelocity == VELOCITY_FULL) {
                    SongCenterController.getInstance().selectionBackPress();
                }

            }

            // independent weather a song is loaded or not
            if (midiType == MESSAGE_TYPE_COMMAND && midiNode == MixTrackController.SPEED_A_FADER_ADDRESS) {
                // front intensity
                sendMpcMidiMessage(MESSAGE_TYPE_COMMAND, 0, 0, midiVelocity);
            } else if (midiType == MESSAGE_TYPE_COMMAND && midiNode == MixTrackController.GAIN_A_FADER_ADDRESS) {
                // back intensity
                sendMpcMidiMessage(MESSAGE_TYPE_COMMAND, 0, 1, midiVelocity);
            } else if (midiType == MESSAGE_TYPE_COMMAND && midiNode == MixTrackController.GAIN_MASTER_FADER_ADDRESS) {
                // unused
            } else if (midiType == MESSAGE_TYPE_COMMAND && midiNode == MixTrackController.GAIN_B_FADER_ADDRESS) {
                // side intensity
                sendMpcMidiMessage(MESSAGE_TYPE_COMMAND, 0, 2, midiVelocity);
            } else if (midiType == MESSAGE_TYPE_COMMAND && midiNode == MixTrackController.SPEED_B_FADER_ADDRESS) {
                // blinder intensity
                sendMpcMidiMessage(MESSAGE_TYPE_COMMAND, 0, 3, midiVelocity);
            } else if (midiType == MESSAGE_TYPE_NOTE_ON
                    //front intensity overwrite
                    && (midiNode == MixTrackController.PITCH_BEND_MINUS_A_ADDRESS
                    || midiNode == MixTrackController.SCRATCH_A_ADDRESS)
                    && midiVelocity == VELOCITY_FULL) {
                mixTrackController.scratchAPressed();
            } else if (midiType == MESSAGE_TYPE_NOTE_ON && midiNode == MixTrackController.CUE_HEADPHONE_A_ADDRESS && midiVelocity == VELOCITY_FULL) {
                // back intensity overwrite
                mixTrackController.cueASelectPressed();
            } else if (midiType == MESSAGE_TYPE_NOTE_ON && midiNode == MixTrackController.CUE_HEADPHONE_B_ADDRESS && midiVelocity == VELOCITY_FULL) {
                // side intensity overwrite
                mixTrackController.cueBSelectPressed();
            } else if (midiType == MESSAGE_TYPE_NOTE_ON
                && (midiNode == MixTrackController.PITCH_BEND_PLUS_B_ADDRESS
                || midiNode == MixTrackController.SCRATCH_B_ADDRESS)
                && midiVelocity == VELOCITY_FULL) {
                // blinder intensity overwrite
                mixTrackController.scratchBPressed();
            }

        } else if (sourceName.contains("LoopBe")) {
            // no talking back from Onyx possible (yet?)
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
        ShortMessage message = createMidiMessage(messageType, channel, note, velocity);
        deviceConnector.getReceiver().send(message, -1);
    }

    public void sendMpcMidiMessage(int messageType, int channel, int note, int velocity) {
        sendMidiMessage(messageType, channel, note, velocity, mpcDeviceConnector);
    }

    public void sendMidiMessage(ShortMessage message, MidiDeviceConnector deviceConnector) {
        deviceConnector.getReceiver().send(message, -1);
    }

    public void sendMpcMidiMessage(ShortMessage message) {
        sendMidiMessage(message, mpcDeviceConnector);
    }


    public static ShortMessage createMidiMessage(int messageType, int channel, int note, int velocity) {
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
