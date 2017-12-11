package Midi;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.ShortMessage;
import java.util.ArrayList;
import java.util.List;

public class MixTrackController {

    // ------ midi note (Message type 0x90) ------

    // pads
    public static final byte PAD_0X0_ADDRESS = 0x59;
    public static final byte PAD_1X0_ADDRESS = 0x5a;
    public static final byte PAD_2X0_ADDRESS = 0x5b;
    public static final byte PAD_3X0_ADDRESS = 0x5c;
    public static final byte PAD_4X0_ADDRESS = 0x5d;
    public static final byte PAD_5X0_ADDRESS = 0x5e;
    public static final byte PAD_6X0_ADDRESS = 0x5f;
    public static final byte PAD_7X0_ADDRESS = 0x60;

    public static final List<Byte> PAD_0X1_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_1X1_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_2X1_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_3X1_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_4X1_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_5X1_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_6X1_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_7X1_ADDRESS = new ArrayList<>(3);

    static {
        PAD_0X1_ADDRESS.add((byte) 0x53);
        PAD_0X1_ADDRESS.add((byte) 0x65);
        PAD_0X1_ADDRESS.add((byte) 0x6d);

        PAD_1X1_ADDRESS.add((byte) 0x54);
        PAD_1X1_ADDRESS.add((byte) 0x66);
        PAD_1X1_ADDRESS.add((byte) 0x6e);

        PAD_2X1_ADDRESS.add((byte) 0x55);
        PAD_2X1_ADDRESS.add((byte) 0x67);
        PAD_2X1_ADDRESS.add((byte) 0x6f);

        PAD_3X1_ADDRESS.add((byte) 0x63);
        PAD_3X1_ADDRESS.add((byte) 0x68);
        PAD_3X1_ADDRESS.add((byte) 0x70);


        PAD_4X1_ADDRESS.add((byte) 0x56);
        PAD_4X1_ADDRESS.add((byte) 0x69);
        PAD_4X1_ADDRESS.add((byte) 0x71);

        PAD_5X1_ADDRESS.add((byte) 0x57);
        PAD_5X1_ADDRESS.add((byte) 0x6a);
        PAD_5X1_ADDRESS.add((byte) 0x72);

        PAD_6X1_ADDRESS.add((byte) 0x58);
        PAD_6X1_ADDRESS.add((byte) 0x6b);
        PAD_6X1_ADDRESS.add((byte) 0x73);

        PAD_7X1_ADDRESS.add((byte) 0x64);
        PAD_7X1_ADDRESS.add((byte) 0x6c);
        PAD_7X1_ADDRESS.add((byte) 0x74);
    }


    // deck control button (at the bottom)
    public static final byte SYNC_A_ADDRESS = 0x40;
    public static final byte CUE_A_ADDRESS = 0x33;
    public static final byte PLAY_A_ADDRESS = 0x3b;
    public static final byte STUTTER_A_ADDRESS = 0x4a;
    public static final byte SCRATCH_A_ADDRESS = 0x48;

    public static final byte SYNC_B_ADDRESS = 0x47;
    public static final byte CUE_B_ADDRESS = 0x3c;
    public static final byte PLAY_B_ADDRESS = 0x42;
    public static final byte STUTTER_B_ADDRESS = 0x4c;
    public static final byte SCRATCH_B_ADDRESS = 0x50;

    // deck organisation button
    public static final byte SHIFT_A_ADDRESS = 0x61;
    public static final byte LOAD_A_ADDRESS = 0x4b;
    public static final byte CUE_HEADPHONE_A_ADDRESS = 0x51;

    public static final byte BROWSE_PUSH_NAVIGATION_ADDRESS = 0x76;
    public static final byte BROWSE_BACK_ADDRESS = 0x77;

    public static final byte SHIFT_B_ADDRESS = 0x62;
    public static final byte LOAD_B_ADDRESS = 0x34;
    public static final byte CUE_HEADPHONE_B_ADDRESS = 0x52;

    // pitch bend button (top outer corner)
    public static final byte PITHC_BEND_MINUS_A_ADDRESS = 0x43;
    public static final byte PITHC_BEND_PLUS_A_ADDRESS = 0x44;
    public static final byte PITHC_BEND_PLUS_B_ADDRESS = 0x45;
    public static final byte PITHC_BEND_MINUS_B_ADDRESS = 0x46;

    // midi command (Message type 0xb0)

    // fader
    public static final byte GAIN_A_FADER_ADDRESS = 0x16;
    public static final byte GAIN_MASTER_FADER_ADDRESS = 0x17;
    public static final byte GAIN_B_FADER_ADDRESS = 0x7;
    public static final byte SPEED_A_FADER_ADDRESS = 0xd;
    public static final byte SPEED_B_FADER_ADDRESS = 0xe;
    public static final byte CORSS_FADER_ADDRESS = 0xa;

    // LED Midi Out Commands
    // pads
    public static final byte PAD_0X0_LED_ADDRESS = 0x59;
    public static final byte PAD_1X0_LED_ADDRESS = 0x5a;
    public static final byte PAD_2X0_LED_ADDRESS = 0x5b;
    public static final byte PAD_3X0_LED_ADDRESS = 0x5c;
    public static final byte PAD_4X0_LED_ADDRESS = 0x5d;
    public static final byte PAD_5X0_LED_ADDRESS = 0x5e;
    public static final byte PAD_6X0_LED_ADDRESS = 0x5f;
    public static final byte PAD_7X0_LED_ADDRESS = 0x60;

    public static final List<Byte> PAD_0X1_LED_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_1X1_LED_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_2X1_LED_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_3X1_LED_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_4X1_LED_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_5X1_LED_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_6X1_LED_ADDRESS = new ArrayList<>(3);
    public static final List<Byte> PAD_7X1_LED_ADDRESS = new ArrayList<>(3);

    static {
        PAD_0X1_LED_ADDRESS.add((byte) 0x53);
        PAD_0X1_LED_ADDRESS.add((byte) 0x65);
        PAD_0X1_LED_ADDRESS.add((byte) 0x6d);

        PAD_1X1_LED_ADDRESS.add((byte) 0x54);
        PAD_1X1_LED_ADDRESS.add((byte) 0x66);
        PAD_1X1_LED_ADDRESS.add((byte) 0x6e);

        PAD_2X1_LED_ADDRESS.add((byte) 0x55);
        PAD_2X1_LED_ADDRESS.add((byte) 0x67);
        PAD_2X1_LED_ADDRESS.add((byte) 0x6f);

        PAD_3X1_LED_ADDRESS.add((byte) 0x63);
        PAD_3X1_LED_ADDRESS.add((byte) 0x68);
        PAD_3X1_LED_ADDRESS.add((byte) 0x70);


        PAD_4X1_LED_ADDRESS.add((byte) 0x56);
        PAD_4X1_LED_ADDRESS.add((byte) 0x69);
        PAD_4X1_LED_ADDRESS.add((byte) 0x71);

        PAD_5X1_LED_ADDRESS.add((byte) 0x57);
        PAD_5X1_LED_ADDRESS.add((byte) 0x6a);
        PAD_5X1_LED_ADDRESS.add((byte) 0x72);

        PAD_6X1_LED_ADDRESS.add((byte) 0x58);
        PAD_6X1_LED_ADDRESS.add((byte) 0x6b);
        PAD_6X1_LED_ADDRESS.add((byte) 0x73);

        PAD_7X1_LED_ADDRESS.add((byte) 0x64);
        PAD_7X1_LED_ADDRESS.add((byte) 0x6c);
        PAD_7X1_LED_ADDRESS.add((byte) 0x74);
    }

    public static final byte LOOP_MODE_A_LED = 0x1E;
    public static final byte SAMPLE_MODE_A_LED = 0x1F;
    public static final byte CUE_MODE_A_LED = 0x20;

    public static final byte LOOP_MODE_B_LED = 0x21;
    public static final byte SAMPLE_MODE_B_LED = 0x22;
    public static final byte CUE_MODE_B_LED = 0x23;


    // deck control button (at the bottom)
    public static final byte SYNC_A_LED_ADDRESS = 0x40;
    public static final byte CUE_A_LED_ADDRESS = 0x33;
    public static final byte PLAY_A_LED_ADDRESS = 0x3b;
    public static final byte STUTTER_A_LED_ADDRESS = 0x4a;
    public static final byte SCRATCH_A_LED_ADDRESS = 0x48;

    public static final byte SYNC_B_LED_ADDRESS = 0x47;
    public static final byte CUE_B_LED_ADDRESS = 0x3c;
    public static final byte PLAY_B_LED_ADDRESS = 0x42;
    public static final byte STUTTER_B_LED_ADDRESS = 0x4c;
    public static final byte SCRATCH_B_LED_ADDRESS = 0x50;

    // deck organisation button
    public static final byte CUE_HEADPHONE_A_LED_ADDRESS = 0x51;
    public static final byte CUE_HEADPHONE_B_LED_ADDRESS = 0x52;

    public static final byte FILE_LED_ADDRESS = 0x4B;
    public static final byte FOLDER_LED_ADDRESS = 0x34;


    // pitch bend button (top outer corner)
    public static final byte PITHC_BEND_MID_A_LED_ADDRESS = 0x48;
    public static final byte PITHC_BEND_MID_B_LED_ADDRESS = 0x52;


    MixTrackController() {
    }

    public void sendTestLedCommands() {
        final byte messageTypeNode = (byte) 0x90;
        final byte messageTypeComand = (byte) 0xb0;
        final byte velocityNone = (byte) 0x00;
        final byte velocityFull = (byte) 0x7f;

        byte[] midiMessage = new byte[3];
        midiMessage[0] = messageTypeNode;
        midiMessage[1] = CUE_MODE_A_LED;
        midiMessage[2] = velocityFull;

        ShortMessage testMessage = new ShortMessage();
        try {
            testMessage.setMessage(midiMessage[0], midiMessage[1], midiMessage[2]);
            MidiController.getInstance().getMidiDeviceConnector().getReceiver().send(testMessage, -1);
        } catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }

    }


}
