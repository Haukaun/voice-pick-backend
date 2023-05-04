package no.ntnu.bachelor.voicepick.services;

import no.ntnu.bachelor.voicepick.pojos.TokenObject;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * A store that keeps track of tokens
 *
 * @param <T> Tokens are stored in a map with a key value pair where the value is the token.
 *           T defines the type of the keys used to access the tokens.
 *           E.g.:
 *           // Create a new store
 *           TokenStore<String> myStore = new TokenStore<>()
 *           // Create a token
 *           String myKey = "myEmail@mail.com"
 *           myStore.generateToken(myKey)
 *           // Validate the token for a given key
 *           String myToken = "pojwephjaephjeh"
 *           boolean result = myStore.isValidToken(myKey, myToken)
 *           // Note: the key has to be of the same type defined when creating the store. In this example `String`
 */
public class TokenStore<T, U extends TokenObject> {
    private final HashMap<T, U> tokens = new HashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final int expirationDelay;
    private final int tokenLength;

    /**
     * Creates an instance of a token store
     *
     * @param expirationDelay how long before the token expires
     */
    public TokenStore(int tokenLength, int expirationDelay) {
        this.tokenLength = tokenLength;
        this.expirationDelay = expirationDelay;
    }

    /**
     * A helper function for creating a randomly generated code
     *
     * @return the randomly generated code
     */
    public String generateCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder token = new StringBuilder(this.tokenLength);
        for (int i = 0; i < this.tokenLength; i++) {
            token.append(chars.charAt(random.nextInt(chars.length())));
        }
        return token.toString();
    }

    /**
     * Returns the value stored at a key
     *
     * @param key of the value to return
     * @return the value stored at a key
     */
    public U getToken(T key) {
        return this.tokens.get(key);
    }

    /**
     * Adds a token to the store
     *
     * @param key of where to store the token
     * @param value object containing the token
     */
    public void addToken(T key, U value) {
        this.tokens.put(key, value);

        // Schedule to remove the token x minutes after its added
        this.scheduler.schedule(() -> this.removeToken(key), this.expirationDelay, TimeUnit.MINUTES);
    }

    /**
     * Validates a token
     *
     * @param key where the token is stored
     * @param token the token to validate
     * @return {@code true} if token is valid, {@code false} if not
     */
    public boolean isValidToken(T key, String token) {
        var value = this.tokens.get(key);
        if (value == null) {
            return false;
        }
        return this.tokens.get(key).getToken().equals(token);
    }

    /**
     * Removes a token from the token store
     *
     * @param key of the token to remove
     */
    public void removeToken(T key) {
        this.tokens.remove(key);
    }

}
