package jp.kshoji.javax.sound.midi.spi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import jp.kshoji.javax.sound.midi.MidiDevice;

/**
 * Abstract class for MIDI Device Provider
 *
 * @author K.Shoji
 */
public abstract class MidiDeviceProvider {

    /**
     * Constructor
     */
    public MidiDeviceProvider() {
    }

    /**
     * Get the {@link MidiDevice} from the specified information
     *
     * @param info the information
     * @return the MidiDevice
     * @throws IllegalArgumentException
     */
    @Nullable
    public abstract MidiDevice getDevice(@NonNull MidiDevice.Info info) throws IllegalArgumentException;

    /**
     * Get the all of {@link MidiDevice.Info}
     *
     * @return the array of {@link MidiDevice.Info}
     */
    @NonNull
    public abstract MidiDevice.Info[] getDeviceInfo();

    /**
     * Check if the specified Device is supported
     *
     * @param info the information
     * @return true if the Device is supported
     */
    public boolean isDeviceSupported(@NonNull MidiDevice.Info info) {
        MidiDevice.Info[] informationArray = getDeviceInfo();

        for (MidiDevice.Info information : informationArray) {
            if (info.equals(information)) {
                return true;
            }
        }
        
        return false;
    }
}
