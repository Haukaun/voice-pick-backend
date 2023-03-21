package no.ntnu.bachelor.voicepick.features.authentication.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class JwtUtil {

    public String getUid(String token) throws JsonProcessingException {
        return this.extractClaim(this.parseToken(token), "sub");
    }

    private String parseToken(String token) {
        var chunks = token.split("\\.");
        var decoder = Base64.getUrlDecoder();

        return new String(decoder.decode(chunks[1]));
    }

    private String extractClaim(String jsonString, String claim) throws JsonProcessingException {
        var objectMApper = new ObjectMapper();
        var jsonNode = objectMApper.readTree(jsonString);

        return jsonNode.get(claim).asText();
    }

}