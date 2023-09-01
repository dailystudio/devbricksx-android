package jp.kshoji.javax.sound.midi;

import androidx.annotation.NonNull;

/**
 * Interface for {@link MidiDevice} receiver.
 *
 * @author K.Shoji
 */
public interface MidiDeviceReceiver extends Receiver {

    /**
     * Get the {@link MidiDevice} associated with this instance.
     *
     * @return the {@link MidiDevice} associated with this instance.
     */
    @NonNull
    MidiDevice getMidiDevice();
}
