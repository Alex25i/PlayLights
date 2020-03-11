package Data;

import Logic.PlayLights;
import Midi.MidiOrganizer;
import Midi.MixTrackController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.sound.midi.ShortMessage;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class SongReader {
    private String songPath;

    public SongReader(String songPath) {
        this.songPath = songPath;
    }

    private JSONObject readSongFile() throws IOException {
        String jsonText = Files.lines(FileSystems.getDefault().getPath(songPath))
                .collect(Collectors.joining(""));
        return new JSONObject(jsonText);
    }

    public Song read() throws IOException, JSONException {
        JSONObject songJSON = readSongFile();
        if (!songJSON.getString("type").equalsIgnoreCase("Song")) {
            throw new JSONException("The type filed of the given Song JSON (" + songPath + ") is not equal to \"Song\""
                    + songJSON.getString("type"));
        }
        String songName = songJSON.getString("name");
        String interpret = songJSON.getString("interpret");
        int tempo = songJSON.getInt("tempo");
        int beatsPerBar = songJSON.getInt("beatsPerBar");

        int lastBeatBar = songJSON.getJSONObject("lastBeat").getInt("bar");
        int lastBeatNr = songJSON.getJSONObject("lastBeat").getInt("beatNr");
        BeatStamp lastBeat = new BeatStamp(lastBeatBar, lastBeatNr);

        JSONObject startUpMessage = songJSON.getJSONObject("startUpMessage");
        ShortMessage midiStartUpMessage = parseMidiMessage(startUpMessage);

        Song song = new Song(
                songName, interpret, tempo, beatsPerBar, lastBeat, midiStartUpMessage, MixTrackController.PAD.PAD_0X1);

        // padActions
        for (Object padActionObj : songJSON.getJSONArray("padActions")) {
            JSONObject padActionJSON = (JSONObject) padActionObj;
            MixTrackController.PAD triggerPad = parseTriggerPad(padActionJSON.getString("triggerPad"));
            int bank = padActionJSON.getInt("bank");
            ArrayList<Runnable> padActionActionRunnableList
                    = createPadActionActionRunnables(padActionJSON.getJSONArray("actions"));
            Runnable actionRunnable = () -> {
                for (Runnable padActionActionRunnable : padActionActionRunnableList) {
                    padActionActionRunnable.run();
                }
            };
            Song.PadAction padAction = song.addPadAction(triggerPad, bank, actionRunnable);
            for (Object userEventObj : padActionJSON.getJSONArray("userEvents")) {
                JSONObject userEventJSON = (JSONObject) userEventObj;
                String label = userEventJSON.getString("label");
                int bar = userEventJSON.getInt("bar");
                int beat = userEventJSON.getInt("beatNr");
                BeatStamp eventTime = new BeatStamp(bar, beat);
                Song.UserEvent userEvent = song.addUserEvent(label, eventTime, () -> { // for custom blinking etc.
                });
                padAction.addUserEvent(userEvent);
            }

        }
        return song;
    }


    private int parseMidiMessageType(String midiMessageType) {
        int messageType;
        if (midiMessageType.equalsIgnoreCase("NOTE_ON")) {
            messageType = MidiOrganizer.MESSAGE_TYPE_NOTE_ON;
        } else if (midiMessageType.equalsIgnoreCase("NOTE_OFF")) {
            messageType = MidiOrganizer.MESSAGE_TYPE_NOTE_OFF;
        } else if (midiMessageType.equalsIgnoreCase("COMMAND")) {
            messageType = MidiOrganizer.MESSAGE_TYPE_COMMAND;
        } else if (midiMessageType.equalsIgnoreCase("PITCH_BEND")) {
            messageType = MidiOrganizer.MESSAGE_TYPE_PITCH_BEND;
        } else {
            messageType = MidiOrganizer.VELOCITY_FULL;
            new JSONException("The messageType filed of the startUpMessage is not equal to \"NOTE_ON\", \"NOTE_ON\" " +
                    ", \"COMMAND\" or \"PITCH_BEND\" (" + midiMessageType + ")").printStackTrace();
        }
        return messageType;
    }

    private int parseVelocity(String velocityString) {
        int velocity;
        if (velocityString.equalsIgnoreCase("Full")) {
            velocity = MidiOrganizer.VELOCITY_FULL;
        } else if (velocityString.equalsIgnoreCase("None")) {
            velocity = MidiOrganizer.VELOCITY_NONE;
        } else {
            try {
                velocity = Integer.parseInt(velocityString);
            } catch (NumberFormatException e) {
                velocity = MidiOrganizer.VELOCITY_FULL;
                new JSONException("The velocity filed of the startUpMessage is not equal to \"Full\" or \"None\" " +
                        "or is a number (represented as String) (" + velocityString + ")").printStackTrace();
            }
        }
        return velocity;
    }

    private ArrayList<Runnable> createPadActionActionRunnables(JSONArray padActionActionList) {
        ArrayList<Runnable> padActionActionRunnableList = new ArrayList<>(padActionActionList.length());
        for (Object padActionActionObj : padActionActionList) {
            JSONObject padActionAction = (JSONObject) padActionActionObj;
            String padActionActionType = padActionAction.getString("type");
            if (padActionActionType.equalsIgnoreCase("MidiMessage")) {
                // @padActionAction is a normal MidiMessage
                padActionActionRunnableList.add(createMidiNodeRunnable(parseMidiMessage(padActionAction)));
            } else if (padActionActionType.equalsIgnoreCase("PeriodicJop")) {
                // @padActionAction is a periodic PeriodicJop
                padActionActionRunnableList.add(createPeriodicJopRunnable(padActionAction));
            }
        }
        return padActionActionRunnableList;
    }

    private ShortMessage parseMidiMessage(JSONObject startUpMessageJSON) {
        if (startUpMessageJSON.getString("type").equalsIgnoreCase("MidiMessage")) {
            int messageType = parseMidiMessageType(startUpMessageJSON.getString("messageType"));
            int channel = startUpMessageJSON.getInt("channel");
            int note = startUpMessageJSON.getInt("note");
            int velocity = parseVelocity(startUpMessageJSON.getString("velocity"));
            return MidiOrganizer.createMidiMessage(messageType, channel, note, velocity);
        } else {
            // TODO: add other message types if there are any
            throw new JSONException("The type filed of the startUpMessage is not equal to \"MidiMessage\""
                    + startUpMessageJSON.getString("type"));
        }
    }

    private Runnable createPeriodicJopRunnable(JSONObject padPeriodicJopPActionAction) {
        int startDelay = padPeriodicJopPActionAction.getInt("startDelay");
        int iterations = padPeriodicJopPActionAction.getInt("iterations");
        int interval = padPeriodicJopPActionAction.getInt("interval");
        JSONObject message = padPeriodicJopPActionAction.getJSONObject("message");
        ShortMessage midiMessage = parseMidiMessage(message);
        return () -> {
            PlayLights.getInstance().getSongPlayer().getTriggerJobs().schedulePeriodicJop(
                    startDelay, iterations, interval, midiMessage);
        };
    }


    private MixTrackController.PAD parseTriggerPad(String triggerPadString) {
        MixTrackController.PAD triggerPad;
        if (triggerPadString.equalsIgnoreCase("PAD_0X0")) {
            triggerPad = MixTrackController.PAD.PAD_0X0;
        } else if (triggerPadString.equalsIgnoreCase("PAD_1X0")) {
            triggerPad = MixTrackController.PAD.PAD_1X0;
        } else if (triggerPadString.equalsIgnoreCase("PAD_2X0")) {
            triggerPad = MixTrackController.PAD.PAD_2X0;
        } else if (triggerPadString.equalsIgnoreCase("PAD_3X0")) {
            triggerPad = MixTrackController.PAD.PAD_3X0;
        } else if (triggerPadString.equalsIgnoreCase("PAD_4X0")) {
            triggerPad = MixTrackController.PAD.PAD_4X0;
        } else if (triggerPadString.equalsIgnoreCase("PAD_5X0")) {
            triggerPad = MixTrackController.PAD.PAD_5X0;
        } else if (triggerPadString.equalsIgnoreCase("PAD_6X0")) {
            triggerPad = MixTrackController.PAD.PAD_6X0;
        } else if (triggerPadString.equalsIgnoreCase("PAD_7X0")) {
            triggerPad = MixTrackController.PAD.PAD_7X0;
        } else if (triggerPadString.equalsIgnoreCase("PAD_0X1")) {
            triggerPad = MixTrackController.PAD.PAD_0X1;
        } else if (triggerPadString.equalsIgnoreCase("PAD_1X1")) {
            triggerPad = MixTrackController.PAD.PAD_1X1;
        } else if (triggerPadString.equalsIgnoreCase("PAD_2X1")) {
            triggerPad = MixTrackController.PAD.PAD_2X1;
        } else if (triggerPadString.equalsIgnoreCase("PAD_3X1")) {
            triggerPad = MixTrackController.PAD.PAD_3X1;
        } else if (triggerPadString.equalsIgnoreCase("PAD_4X1")) {
            triggerPad = MixTrackController.PAD.PAD_4X1;
        } else if (triggerPadString.equalsIgnoreCase("PAD_5X1")) {
            triggerPad = MixTrackController.PAD.PAD_5X1;
        } else if (triggerPadString.equalsIgnoreCase("PAD_6X1")) {
            triggerPad = MixTrackController.PAD.PAD_6X1;
        } else if (triggerPadString.equalsIgnoreCase("PAD_7X1")) {
            triggerPad = MixTrackController.PAD.PAD_7X1;
        } else {
            triggerPad = MixTrackController.PAD.PAD_0X0;
            new JSONException("The triggerPad filed of an element in padActions is not equal to a Mixtrack PAD("
                    + triggerPadString + ")").printStackTrace();
        }
        return triggerPad;
    }

    /**
     * @return A runnable which triggers a midiNode event
     */
    private Runnable createMidiNodeRunnable(ShortMessage padActionMidiMessage) {
        return () -> {
            MidiOrganizer mo = PlayLights.getInstance().getMidiOrganizer();
            mo.sendMpcMidiMessage(padActionMidiMessage);
        };
    }

    public String getSongPath() {
        return songPath;
    }

    public static void main(String[] args) throws IOException {
        SongReader songReader = new SongReader("D:\\Daten\\Projekte\\Java\\PlayLights\\Songs\\HYRM.json");
        Song song = songReader.read();
    }
}