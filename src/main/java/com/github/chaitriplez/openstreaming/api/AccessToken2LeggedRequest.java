package com.github.chaitriplez.openstreaming.api;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPrivateKeySpec;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessToken2LeggedRequest {
  String apiKey;
  String params;
  @Builder.Default Long timestamp = null;
  @Builder.Default String signature = null;

  public void sign(String apiSecret) {
    timestamp = System.currentTimeMillis();
    String content = String.format("%s.%s.%s", apiKey, params, timestamp);

    try {
      PrivateKey privateKey = getPrivateKey(apiSecret);

      Signature dsa = Signature.getInstance("SHA256withECDSA");
      dsa.initSign(privateKey);
      dsa.update(content.getBytes(StandardCharsets.UTF_8));
      signature = bytesToHex(dsa.sign());
    } catch (GeneralSecurityException e) {
      throw new RuntimeException("Cannot sign request", e);
    }
  }

  private PrivateKey getPrivateKey(String apiSecret) throws GeneralSecurityException {
    BigInteger privateKeyInt = new BigInteger(Base64.getDecoder().decode(apiSecret));

    AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
    parameters.init(new ECGenParameterSpec("secp256r1"));

    ECParameterSpec ecParameterSpec = parameters.getParameterSpec(ECParameterSpec.class);
    ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(privateKeyInt, ecParameterSpec);

    return KeyFactory.getInstance("EC").generatePrivate(ecPrivateKeySpec);
  }

  private String bytesToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes) {
      sb.append(String.format("%02x", b));
    }
    return sb.toString();
  }
}
