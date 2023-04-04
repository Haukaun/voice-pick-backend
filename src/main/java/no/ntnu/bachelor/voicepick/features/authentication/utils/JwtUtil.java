package no.ntnu.bachelor.voicepick.features.authentication.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class JwtUtil {
    
    public boolean getEmailVerified(String token) throws JsonProcessingException {
        var result = this.extractClaim(this.parseToken(token), "email_verified");
        return result.equalsIgnoreCase("true");
    }

    public String getUid(String token) throws JsonProcessingException {
        return this.extractClaim(this.parseToken(token), "sub");
    }

    private String parseToken(String token) {
        var chunks = token.split("\\.");
        if (chunks.length < 2) {
            throw new InvalidBearerTokenException("The token provided is invalid and cannot be parsed");
        }

        var decoder = Base64.getUrlDecoder();

        return new String(decoder.decode(chunks[1]));
    }

    private String extractClaim(String jsonString, String claim) throws JsonProcessingException {
        var objectMApper = new ObjectMapper();
        var jsonNode = objectMApper.readTree(jsonString);

        return jsonNode.get(claim).asText();
    }

}