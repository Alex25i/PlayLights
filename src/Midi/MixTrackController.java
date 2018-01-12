package Midi;

import Data.Song;
import Logic.PlayLights;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.*;

import static Midi.MixTrackController.PAD.*;

public class MixTrackController {

    public enum PAD {PAD_0X0, PAD_1X0, PAD_2X0, PAD_3X0, PAD_4X0, PAD_5X0, PAD_6X0, PAD_7X0, PAD_0X1, PAD_1X1, PAD_2X1, PAD_3X1, PAD_4X1, PAD_5X1, PAD_6X1, PAD_7X1,}

    public enum BLINK_DURATION {BARS4, BARS2, BARS1}

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


    // pitch bend LEDs (top outer corner)
    public static final byte PITCH_BEND_MID_A_LED_ADDRESS = 0x28;
    public static final byte PITCH_BEND_MID_B_LED_ADDRESS = 0x29;

    private Map<Byte, Timer> blinkingLEDs;
    private int activeBank;

    MixTrackController() {
        blinkingLEDs = new HashMap<>();
        activeBank = 0;
    }

    public void prepareSong(Song currentSong) {
        startBlinkLed(PLAY_B_LED_ADDRESS, 1500);
    }

    public void reconnectRoutine() {
        Platform.runLater(() -> {
            new MissingResourceException("The connection to the Mixtrack controller is lost!",
                    MidiDeviceConnector.class.toString(), "-1").printStackTrace();
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error: Connection Lost");
//            alert.setHeaderText("Device unplugged!");
//            alert.setContentText("The connection to the Mixtrack controller is lost!\n" +
//                    "Please check the connection and click reconnect");
//            ButtonType buttonTypeReconnect = new ButtonType("Reconnect");
//            alert.getButtonTypes().setAll(buttonTypeReconnect);
//            alert.showAndWait();
//            PlayLights.getPlayLights().getMidiOrganizer().setMixTrackDeviceConnector(new MidiDeviceConnector("Mixtrack"));
//            // reconnected now, otherwise application was closed in MidiDeviceConnector.searchForDevice(String)
//            blackoutStartLEDs();
//            //TODO: Illuminate right LEDs
//            if (PlayLights.getPlayLights().getSongPlayer() != null) {
//                // there is a song active
//                prepareSong(PlayLights.getPlayLights().getSongPlayer().getCurrentSong());
//                //SongPlayerController.getSongPlayerController().setBlackBackgroundColor();
//            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error: Connection Lost");
            alert.setHeaderText("Device unplugged!");
            alert.setContentText("The connection to the Mixtrack controller is lost!\n" +
                    "Please check the connection and restart the application");
            ButtonType buttonTypeReconnect = new ButtonType("Exit");
            alert.getButtonTypes().setAll(buttonTypeReconnect);
            alert.showAndWait();
            System.exit(2);
        });
    }

    public void blinkPad(PAD pad, BLINK_DURATION blinkDuration, int songTempo) {
        // TODO: Implement method

    }

    public void startBlinkLed(final byte LED_ADDRESS, int blinkPeriod) {
        if (blinkingLEDs.containsKey(LED_ADDRESS)) {
            // stop old timer and remove it from map
            blinkingLEDs.remove(LED_ADDRESS).cancel();
        }
        blinkingLEDs.put(LED_ADDRESS, new Timer("Blink of " + LED_ADDRESS));
        // use boolean inside a List so that the list object van be final
        TimerTask blink = new TimerTask() {
            boolean on = false;

            @Override
            public void run() {
                on = !on;
                setLedIllumination(LED_ADDRESS, on);
            }
        };
        blinkingLEDs.get(LED_ADDRESS).schedule(blink, 0, blinkPeriod / 2);
    }

    public void stopBlinkLed(final byte LED_ADDRESS) {
        if (blinkingLEDs.containsKey(LED_ADDRESS)) {
            // remove timer from map an cancel it task
            blinkingLEDs.remove(LED_ADDRESS).cancel();
        }
    }


    /**
     * turns one specific led on or off
     *
     * @param ledAddress the LED which to be turned on or off
     * @param on         defines weather the LED should be on or off
     */
    //TODO: Refactor: Use @MidiOrganizer.createMidiMessage()
    //TODO: Be robust against not connected device
    public void setLedIllumination(byte ledAddress, boolean on) {
        int[] midiMessage = new int[3];
        midiMessage[0] = MidiOrganizer.MESSAGE_TYPE_NODE;
        midiMessage[1] = ledAddress;
        if (on) {
            midiMessage[2] = MidiOrganizer.VELOCITY_FULL;
        } else {
            midiMessage[2] = MidiOrganizer.VELOCITY_NONE;
        }
        PlayLights.getPlayLights().getMidiOrganizer().sendMidiMessage(midiMessage[0], midiMessage[1], midiMessage[2],
                PlayLights.getPlayLights().getMidiOrganizer().getMixTrackDeviceConnector());
    }

    /**
     * @param pad_address the address of the returning {@link PAD}
     * @return the {@link PAD} from the given <code>pad_address</code> or null if the given address doesn't
     * relate to any {@link PAD}
     */
    public static PAD getPadFromAddress(byte pad_address) {
        switch (pad_address) {
            case PAD_0X0_ADDRESS:
                return PAD_0X0;

            case PAD_1X0_ADDRESS:
                return PAD_1X0;

            case PAD_2X0_ADDRESS:
                return PAD_2X0;

            case PAD_3X0_ADDRESS:
                return PAD_3X0;

            case PAD_4X0_ADDRESS:
                return PAD_4X0;

            case PAD_5X0_ADDRESS:
                return PAD_5X0;

            case PAD_6X0_ADDRESS:
                return PAD_6X0;

            case PAD_7X0_ADDRESS:
                return PAD_7X0;

            default:

                if (PAD_0X1_ADDRESS.contains(pad_address)) {
                    return PAD_0X1;
                }
                if (PAD_1X1_ADDRESS.contains(pad_address)) {
                    return PAD_1X1;
                }
                if (PAD_2X1_ADDRESS.contains(pad_address)) {
                    return PAD_2X1;
                }
                if (PAD_3X1_ADDRESS.contains(pad_address)) {
                    return PAD_3X1;
                }
                if (PAD_4X1_ADDRESS.contains(pad_address)) {
                    return PAD_4X1;
                }
                if (PAD_5X1_ADDRESS.contains(pad_address)) {
                    return PAD_5X1;
                }
                if (PAD_6X1_ADDRESS.contains(pad_address)) {
                    return PAD_6X1;
                }
                if (PAD_7X1_ADDRESS.contains(pad_address)) {
                    return PAD_7X1;
                }
                // if the given address is not a pad address return null
                return null;
        }
    }

    /**
     * checks if given pad is a bank root pad
     * this is the case if it is a pad from the bottom line (os its name is PAD_?X1)
     *
     * @param pad the {@link PAD} that's get checked
     * @return true, if <code>pad</code> is a bank root
     */
    public static boolean padIsBankRoot(PAD pad) {
        // the bottom line pads are the root pads.
        // They have in common that they all have multiple addresses, so you can just check their number of LED addresses
        return getPadLedAddresses(pad).size() > 1;
    }

    /**
     * turns of all default illuminated LEDs of the controller
     */
    public void blackoutStartLEDs() {
        setLedIllumination(SYNC_A_LED_ADDRESS, false);
        setLedIllumination(CUE_A_LED_ADDRESS, false);
        setLedIllumination(PLAY_A_LED_ADDRESS, false);
        setLedIllumination(STUTTER_A_LED_ADDRESS, false);
        setLedIllumination(SYNC_B_LED_ADDRESS, false);
        setLedIllumination(CUE_B_LED_ADDRESS, false);
        setLedIllumination(PLAY_B_LED_ADDRESS, false);
        setLedIllumination(STUTTER_B_LED_ADDRESS, false);

        setLedIllumination(LOOP_MODE_A_LED, false);
        setLedIllumination(SAMPLE_MODE_A_LED, false);
        setLedIllumination(CUE_MODE_A_LED, false);
        setLedIllumination(LOOP_MODE_B_LED, false);
        setLedIllumination(SAMPLE_MODE_B_LED, false);
        setLedIllumination(CUE_MODE_B_LED, false);
    }

    private static List<Byte> getPadLedAddresses(PAD pad) {
        ArrayList<Byte> padAddresses = new ArrayList<>();
        switch (pad) {
            case PAD_0X0:
                padAddresses.add(PAD_0X0_LED_ADDRESS);
                return padAddresses;
            case PAD_1X0:
                padAddresses.add(PAD_1X0_LED_ADDRESS);
                return padAddresses;
            case PAD_2X0:
                padAddresses.add(PAD_2X0_LED_ADDRESS);
                return padAddresses;
            case PAD_3X0:
                padAddresses.add(PAD_3X0_LED_ADDRESS);
                return padAddresses;
            case PAD_4X0:
                padAddresses.add(PAD_4X0_LED_ADDRESS);
                return padAddresses;
            case PAD_5X0:
                padAddresses.add(PAD_5X0_LED_ADDRESS);
                return padAddresses;
            case PAD_6X0:
                padAddresses.add(PAD_6X0_LED_ADDRESS);
                return padAddresses;
            case PAD_7X0:
                padAddresses.add(PAD_7X0_LED_ADDRESS);
                return padAddresses;
            case PAD_0X1:
                return PAD_0X1_LED_ADDRESS;
            case PAD_1X1:
                return PAD_1X1_LED_ADDRESS;
            case PAD_2X1:
                return PAD_2X1_LED_ADDRESS;
            case PAD_3X1:
                return PAD_3X1_LED_ADDRESS;
            case PAD_4X1:
                return PAD_4X1_LED_ADDRESS;
            case PAD_5X1:
                return PAD_5X1_LED_ADDRESS;
            case PAD_6X1:
                return PAD_6X1_LED_ADDRESS;
            case PAD_7X1:
                return PAD_7X1_LED_ADDRESS;
            default:
                return null;
        }
    }

    private Thread createBlinkRunnable(PAD pad, BLINK_DURATION blink_duration, int tempo) {
        return new Thread(() -> {
            // TODO.contains(pad_address) { implement Method
            // TODO: HOW about interrupting the thread
        });
    }

    public int getActiveBank() {
        return activeBank;
    }

    public void updateActiveBank(PAD pad, Song currentSong) {
        switch (pad) {
            case PAD_0X1:
                activeBank = 0;
                setLedIllumination(LOOP_MODE_A_LED, true);
                setLedIllumination(SAMPLE_MODE_A_LED, false);
                setLedIllumination(CUE_MODE_A_LED, false);
                setLedIllumination(LOOP_MODE_B_LED, false);
                setLedIllumination(SAMPLE_MODE_B_LED, false);
                setLedIllumination(CUE_MODE_B_LED, false);
                break;
            case PAD_1X1:
                activeBank = 1;
                setLedIllumination(LOOP_MODE_A_LED, false);
                setLedIllumination(SAMPLE_MODE_A_LED, true);
                setLedIllumination(CUE_MODE_A_LED, false);
                setLedIllumination(LOOP_MODE_B_LED, false);
                setLedIllumination(SAMPLE_MODE_B_LED, false);
                setLedIllumination(CUE_MODE_B_LED, false);
                break;
            case PAD_2X1:
                activeBank = 2;
                setLedIllumination(LOOP_MODE_A_LED, false);
                setLedIllumination(SAMPLE_MODE_A_LED, false);
                setLedIllumination(CUE_MODE_A_LED, true);
                setLedIllumination(LOOP_MODE_B_LED, false);
                setLedIllumination(SAMPLE_MODE_B_LED, false);
                setLedIllumination(CUE_MODE_B_LED, false);
                break;
            case PAD_4X1:
                activeBank = 3;
                setLedIllumination(LOOP_MODE_A_LED, false);
                setLedIllumination(SAMPLE_MODE_A_LED, false);
                setLedIllumination(CUE_MODE_A_LED, false);
                setLedIllumination(LOOP_MODE_B_LED, true);
                setLedIllumination(SAMPLE_MODE_B_LED, false);
                setLedIllumination(CUE_MODE_B_LED, false);
                break;
            case PAD_5X1:
                activeBank = 4;
                setLedIllumination(LOOP_MODE_A_LED, false);
                setLedIllumination(SAMPLE_MODE_A_LED, false);
                setLedIllumination(CUE_MODE_A_LED, false);
                setLedIllumination(LOOP_MODE_B_LED, false);
                setLedIllumination(SAMPLE_MODE_B_LED, true);
                setLedIllumination(CUE_MODE_B_LED, false);
                break;
            case PAD_6X1:
                activeBank = 5;
                setLedIllumination(LOOP_MODE_A_LED, false);
                setLedIllumination(SAMPLE_MODE_A_LED, false);
                setLedIllumination(CUE_MODE_A_LED, false);
                setLedIllumination(LOOP_MODE_B_LED, false);
                setLedIllumination(SAMPLE_MODE_B_LED, false);
                setLedIllumination(CUE_MODE_B_LED, true);
                break;
            default:
                activeBank = 0;
                if (PlayLights.verbose) {
                    new IllegalArgumentException("There is no bank associated with the pad" + pad.toString()).printStackTrace();
                }
                setLedIllumination(LOOP_MODE_A_LED, true);
                setLedIllumination(SAMPLE_MODE_A_LED, false);
                setLedIllumination(CUE_MODE_A_LED, false);
                setLedIllumination(LOOP_MODE_B_LED, false);
                setLedIllumination(SAMPLE_MODE_B_LED, false);
                setLedIllumination(CUE_MODE_B_LED, false);
        }


        // crete a map which defines all Pad LEDs and their target illumination status
        Map<Byte, Boolean> targetLEDStatus = new HashMap<>(32);

        for (PAD padEnum : PAD.values()) {
            for (Byte padAddress : MixTrackController.getPadLedAddresses(padEnum)) {
                targetLEDStatus.put(padAddress, false);
            }
        }
        for (Song.PadAction padAction : currentSong.getPadActionsFromBank(activeBank)) {
            for (Byte padAddress : getPadLedAddresses(padAction.getTriggerPad())) {
                targetLEDStatus.put(padAddress, true);
            }
        }

        for (PAD padEnum : PAD.values()) {
            for (Byte padAddress : MixTrackController.getPadLedAddresses(padEnum)) {
                setLedIllumination(padAddress, targetLEDStatus.get(padAddress));
            }
        }
    }
}
