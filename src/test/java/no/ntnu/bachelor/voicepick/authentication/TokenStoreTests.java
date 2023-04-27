package no.ntnu.bachelor.voicepick.authentication;

import no.ntnu.bachelor.voicepick.pojos.TokenObject;
import no.ntnu.bachelor.voicepick.services.TokenStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TokenStoreTests {

    private TokenStore<String, TokenObject> myStore;

    @BeforeEach
    void setup() {
        this.myStore = new TokenStore<>(10, 10);
    }

    @Test
    @DisplayName("Try to generate code")
    void generateCode() {
        var token = this.myStore.generateCode();
        assertNotNull(token);
    }

    @Test
    @DisplayName("Add token")
    void addToken() {
        this.myStore.addToken("myKey", new TokenObject("myToken"));
        var value = this.myStore.getToken("myKey");
        assertNotNull(value);
        assertEquals("myToken", value.getToken());
    }

    @Test
    @DisplayName("Get token that does not exists")
    void getNonExistingToken() {
        var token = this.myStore.getToken("myToken");

        assertNull(token);
    }

    @Test
    @DisplayName("Get token")
    void getToken() {
        var myKey = "myKey";
        var myToken = this.myStore.generateCode();
        this.myStore.addToken(myKey, new TokenObject(myToken));

        var value = this.myStore.getToken(myKey);
        assertNotNull(value);
        assertEquals(myToken, value.getToken());
    }

    @Test
    @DisplayName("Try to validate token that does not exists")
    void validateNonExistingToken() {
        assertFalse(this.myStore.isValidToken("myKey", "myToken"));
    }

    @Test
    @DisplayName("Try to validate invalid token")
    void validateInvalidToken() {
        var myKey = "myKey";
        var myToken = this.myStore.generateCode();
        this.myStore.addToken(myKey, new TokenObject(myToken));

        assertFalse(this.myStore.isValidToken(myKey, "randomToken"));
    }

    @Test
    @DisplayName("Try to validate token")
    void validateToken() {
        var myKey = "myKey";
        var myToken = this.myStore.generateCode();
        this.myStore.addToken(myKey, new TokenObject(myToken));

        assertTrue(this.myStore.isValidToken(myKey, myToken));
    }

    @Test
    @DisplayName("Remove token that does not exists")
    void deleteNonExistingToken() {
        var myKey = "myKey";
        this.myStore.removeToken(myKey);

        assertNull(this.myStore.getToken(myKey));
    }

    @Test
    @DisplayName("Remove token")
    void removeToken() {
        var myKey = "myKey";
        var myToken = this.myStore.generateCode();
        this.myStore.addToken(myKey, new TokenObject(myToken));

        this.myStore.removeToken(myKey);

        assertNull(this.myStore.getToken(myKey));
    }

}
