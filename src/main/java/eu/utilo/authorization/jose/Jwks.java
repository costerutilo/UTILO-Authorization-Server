package eu.utilo.authorization.jose;

import com.nimbusds.jose.jwk.*;
import org.springframework.util.Assert;

import javax.crypto.SecretKey;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Objects;
import java.util.UUID;

import static java.lang.Thread.currentThread;

/**
 * @author Joe Grandja
 * @since 0.1.0
 */
public final class Jwks {

    private Jwks() {
    }

    public static String readKeyAsString(String keyLocation) throws Exception {
        URI uri = Objects.requireNonNull(currentThread().getContextClassLoader().getResource(keyLocation)).toURI();
        byte[] byteArray = Files.readAllBytes(Paths.get(uri));
        return new String(byteArray);
    }

    private static final String KEY_ID = "kuschzzp";

    public static RSAKey generateRsa() {
        RSAPrivateKey rsaPrivateKey = null;
        RSAPublicKey rsaPublicKey = null;
        try {

            String pemPrK = readKeyAsString("rsa/private_key.pem");
            RSAKey rsaPrKey = (RSAKey) JWK.parseFromPEMEncodedObjects(pemPrK);
            rsaPrivateKey = rsaPrKey.toRSAPrivateKey();

            String pemPuK = readKeyAsString("rsa/public_key.pem");
            RSAKey rsaPuKey = (RSAKey) JWK.parseFromPEMEncodedObjects(pemPuK);
            rsaPublicKey = rsaPuKey.toRSAPublicKey();

        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.isTrue(null != rsaPublicKey);
        return new RSAKey.Builder(rsaPublicKey)
                .privateKey(rsaPrivateKey)
                .keyID(KEY_ID)
                .build();
    }

    //    public static RSAKey generateRsa() {
    //        KeyPair keyPair = KeyGeneratorUtils.generateRsaKey();
    //        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
    //        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    //        // @formatter:off
//		return new RSAKey.Builder(publicKey)
//				.privateKey(privateKey)
//				.keyID(UUID.randomUUID().toString())
//				.build();
//		// @formatter:on
    //    }

    public static ECKey generateEc() {
        KeyPair keyPair = KeyGeneratorUtils.generateEcKey();
        ECPublicKey publicKey = (ECPublicKey) keyPair.getPublic();
        ECPrivateKey privateKey = (ECPrivateKey) keyPair.getPrivate();
        Curve curve = Curve.forECParameterSpec(publicKey.getParams());
        // @formatter:off
		return new ECKey.Builder(curve, publicKey)
				.privateKey(privateKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		// @formatter:on
    }

    public static OctetSequenceKey generateSecret() {
        SecretKey secretKey = KeyGeneratorUtils.generateSecretKey();
        // @formatter:off
		return new OctetSequenceKey.Builder(secretKey)
				.keyID(UUID.randomUUID().toString())
				.build();
		// @formatter:on
    }
}
