package be.appfoundry.promtius;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import java.util.Map;

/**
 * @author Mike Seghers
 */
public class PushPayload {

    public static final String DEFAULT_SOUND_VALUE = "default";
    public static final String DEFAULT_DISCRIMINATOR_VALUE = "discriminator";
    public static final PushPriority DEFAULT_PUSHPRIORITY_VALUE = PushPriority.NORMAL;
    private String message;
    private String sound;
    private String discriminator;
    private Optional<Map<String, ?>> customFields;
    private Optional<Integer> timeToLive;
    private PushPriority pushPriority;

    /**
     * Push priority.
     * GCM documentation => https://developers.google.com/cloud-messaging/http-server-ref
     * APNS documentation => https://developer.apple.com/library/ios/documentation/NetworkingInternet/Conceptual/RemoteNotificationsPG/Appendixes/BinaryProviderAPI.html#//apple_ref/doc/uid/TP40008194-CH106-SW5
     */
    public enum PushPriority {
        NORMAL,
        HIGH
    }

    private PushPayload() {
    }

    /**
     * The main message to be pushed
     */
    public String getMessage() {
        return message;
    }

    /**
     * The sound to be played with the push notification
     */
    public String getSound() {
        return sound;
    }

    /**
     * The optional time-to-live of the message, in minutes.
     */
    public Optional<Integer> getTimeToLive() {
        return timeToLive;
    }

    /**
     * Custom fields that are added to the payload.
     */
    public Optional<Map<String, ?>> getCustomFields() {
        return customFields;
    }

    /**
     * A filter/collapse value to be used to group messages together. This is not supported by all providers.
     */
    public String getDiscriminator() {
        return discriminator;
    }

    /**
     * The priority to be used for this message by the providers.
     */
    public PushPriority getPushPriority() {
        return pushPriority;
    }

    public static class Builder {
        private String message;
        private Optional<String> sound = Optional.absent();
        private Optional<Integer> timeToLive = Optional.absent();
        private Optional<Map<String, ?>> customFields = Optional.absent();
        private Optional<String> discriminator = Optional.absent();
        private Optional<PushPriority> pushPriority = Optional.absent();

        public Builder withMessage(final String message) {
            this.message = message;
            return this;
        }

        public Builder withSound(final String sound) {
            this.sound = Optional.of(sound);
            return this;
        }

        public Builder withTimeToLive(final int timeToLive) {
            this.timeToLive = Optional.of(timeToLive);
            return this;
        }

        public Builder withCustomFields(final Map<String, ?> customFields) {
            this.customFields = Optional.<Map<String, ?>>of(customFields);
            return this;
        }


        public Builder withDiscriminator(final String discriminator) {
            this.discriminator = Optional.of(discriminator);
            return this;
        }

        public Builder withPushPriority(final PushPriority pushPriority) {
            this.pushPriority = Optional.of(pushPriority);
            return this;
        }

        public PushPayload build() {
            Preconditions.checkState(message != null);
            PushPayload pushPayload = new PushPayload();
            pushPayload.message = this.message;
            pushPayload.sound = this.sound.or(DEFAULT_SOUND_VALUE);
            pushPayload.timeToLive = this.timeToLive;
            pushPayload.customFields = this.customFields;
            pushPayload.discriminator = this.discriminator.or(DEFAULT_DISCRIMINATOR_VALUE);
            pushPayload.pushPriority = this.pushPriority.or(DEFAULT_PUSHPRIORITY_VALUE);
            return pushPayload;
        }
    }
}
