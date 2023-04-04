package no.ntnu.bachelor.voicepick.authentication;

import com.fasterxml.jackson.core.JsonProcessingException;
import no.ntnu.bachelor.voicepick.features.authentication.utils.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilTests {

  @Autowired
  private JwtUtil jwtUtil;

  @Test
  @DisplayName("Test extract claim from invalid token")
  void extractClaimFromInvalidToken() {
    String token = "Hello";
    try {
      jwtUtil.getEmailVerified(token);
      fail("Exception should have been thrown but wasn't");
    } catch(JsonProcessingException e) {
      fail("JsonProcessingException was thrown when InvalidBearerTokenException should have been thrown instead");
    } catch (InvalidBearerTokenException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Test extract claim from token that cannot be parsed")
  void extractClaimFromNoneParsableToken() {
    String token = "Hello\\.Whatsup";
    try {
      jwtUtil.getEmailVerified(token);
      fail("Exception should have been thrown");
    } catch (JsonProcessingException e) {
      assertTrue(true);
    }
  }

  @Test
  @DisplayName("Extract email verified from valid token")
  void extractEmailFromValidToken() {
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJmdVNBN3V3dzh0SVozTzdsT1RyNTdTNEFRX1h1akp0Z0xxRW1KNzE3cllJIn0.eyJleHAiOjE2ODA2MTk3ODAsImlhdCI6MTY4MDYxOTQ4MCwianRpIjoiODEyMmEyNjYtNWNkMC00OGY2LWE5NDAtMTIwNDkxYjUxOTcxIiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay5iYWNoZWxvci5zZXEucmUvYXV0aC9yZWFsbXMvdm9pY2UtcGljay1kZXYiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZmYzMzNkYjQtYTQ5Zi00M2U2LTliNTAtODE5OGMzNDA1MTFkIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYXBpIiwic2Vzc2lvbl9zdGF0ZSI6Ijk4N2ZiNjNjLTc5MjUtNDEyMy1iMGNhLWFjZWZiMWMwNjBkZCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiTEVBREVSIiwiZGVmYXVsdC1yb2xlcy12b2ljZS1waWNrLWRldiIsIlVTRVIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI5ODdmYjYzYy03OTI1LTQxMjMtYjBjYS1hY2VmYjFjMDYwZGQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInJvbGVzIjpbIkxFQURFUiIsImRlZmF1bHQtcm9sZXMtdm9pY2UtcGljay1kZXYiLCJVU0VSIl0sIm5hbWUiOiJNYXRldXN6IFBpY2hldGEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJtYXRpcEBpY2xvdWQuY29tIiwiZ2l2ZW5fbmFtZSI6Ik1hdGV1c3oiLCJmYW1pbHlfbmFtZSI6IlBpY2hldGEiLCJlbWFpbCI6Im1hdGlwQGljbG91ZC5jb20ifQ.NA-5uQOISsGpjOMVQsY-5Jym1qQIB3LbxXFLTKwE2eavnVOmDqoXpvnm1CcOzohBcsq6oAQDYFVIwomA28nKiaFdSEhJ3zsUgnamnZsf-F4O1rDZ1WimRKe4ORgu2c5vx8jrosmQ8w4MG7OEuL1kVWkHxCXIV87UG1gSURYumtkXk3aRHLFhou9pb7yg9RzPWtENaOOElkkA4fQIq0K3tgM2AiGCirSiALIkhaH4-PuIo5c6OevzwUPxRU65cltTtbm8FG4sMWSJEBos7AUzz4dqhO29jcRn5mBiWl17KOpstfZIBiMPK7gtXm2VK-WssjUBIVV6-TbxAymZHeu1Dg";
    try {
      var isVerified = jwtUtil.getEmailVerified(token);
      assertFalse(isVerified);
    } catch (JsonProcessingException e) {
      fail("JsonProcessingException was thrown when it should not");
    }
  }

  @Test
  @DisplayName("Extract user id from valid token")
  void extractUserIdFromValidToken() {
    String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJmdVNBN3V3dzh0SVozTzdsT1RyNTdTNEFRX1h1akp0Z0xxRW1KNzE3cllJIn0.eyJleHAiOjE2ODA2MTk3ODAsImlhdCI6MTY4MDYxOTQ4MCwianRpIjoiODEyMmEyNjYtNWNkMC00OGY2LWE5NDAtMTIwNDkxYjUxOTcxIiwiaXNzIjoiaHR0cHM6Ly9rZXljbG9hay5iYWNoZWxvci5zZXEucmUvYXV0aC9yZWFsbXMvdm9pY2UtcGljay1kZXYiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiZmYzMzNkYjQtYTQ5Zi00M2U2LTliNTAtODE5OGMzNDA1MTFkIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoiYXBpIiwic2Vzc2lvbl9zdGF0ZSI6Ijk4N2ZiNjNjLTc5MjUtNDEyMy1iMGNhLWFjZWZiMWMwNjBkZCIsImFjciI6IjEiLCJhbGxvd2VkLW9yaWdpbnMiOlsiKiJdLCJyZWFsbV9hY2Nlc3MiOnsicm9sZXMiOlsiTEVBREVSIiwiZGVmYXVsdC1yb2xlcy12b2ljZS1waWNrLWRldiIsIlVTRVIiXX0sInJlc291cmNlX2FjY2VzcyI6eyJhY2NvdW50Ijp7InJvbGVzIjpbIm1hbmFnZS1hY2NvdW50IiwibWFuYWdlLWFjY291bnQtbGlua3MiLCJ2aWV3LXByb2ZpbGUiXX19LCJzY29wZSI6InByb2ZpbGUgZW1haWwiLCJzaWQiOiI5ODdmYjYzYy03OTI1LTQxMjMtYjBjYS1hY2VmYjFjMDYwZGQiLCJlbWFpbF92ZXJpZmllZCI6ZmFsc2UsInJvbGVzIjpbIkxFQURFUiIsImRlZmF1bHQtcm9sZXMtdm9pY2UtcGljay1kZXYiLCJVU0VSIl0sIm5hbWUiOiJNYXRldXN6IFBpY2hldGEiLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJtYXRpcEBpY2xvdWQuY29tIiwiZ2l2ZW5fbmFtZSI6Ik1hdGV1c3oiLCJmYW1pbHlfbmFtZSI6IlBpY2hldGEiLCJlbWFpbCI6Im1hdGlwQGljbG91ZC5jb20ifQ.NA-5uQOISsGpjOMVQsY-5Jym1qQIB3LbxXFLTKwE2eavnVOmDqoXpvnm1CcOzohBcsq6oAQDYFVIwomA28nKiaFdSEhJ3zsUgnamnZsf-F4O1rDZ1WimRKe4ORgu2c5vx8jrosmQ8w4MG7OEuL1kVWkHxCXIV87UG1gSURYumtkXk3aRHLFhou9pb7yg9RzPWtENaOOElkkA4fQIq0K3tgM2AiGCirSiALIkhaH4-PuIo5c6OevzwUPxRU65cltTtbm8FG4sMWSJEBos7AUzz4dqhO29jcRn5mBiWl17KOpstfZIBiMPK7gtXm2VK-WssjUBIVV6-TbxAymZHeu1Dg";
    try {
      var id = jwtUtil.getUid(token);
      assertEquals("ff333db4-a49f-43e6-9b50-8198c340511d", id);
    } catch (JsonProcessingException e) {
      fail("JsonProcessingException was thrown when it should not");
    }
  }
}
