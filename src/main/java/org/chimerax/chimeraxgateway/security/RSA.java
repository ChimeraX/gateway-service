package org.chimerax.chimeraxgateway.security;

import lombok.SneakyThrows;
import lombok.val;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.UUID;

/**
 * Author: Silviu-Mihnea Cucuiet
 * Date: 11-Jun-20
 * Time: 9:30 AM
 */
@Component
public class RSA {

    private PublicKey publicKey;
    private PrivateKey privateKey;

    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(512);
        KeyPair kp = kpg.generateKeyPair();
        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();

        String outFile = "src/main/resources/static/rsa";
        write(outFile + ".key", privateKey);
        write(outFile + ".pub", publicKey);

        val s = UUID.randomUUID().toString();
        System.out.println(s);
        val e = _encrypt(s.getBytes(), publicKey);
        val cookie = URLEncoder.encode(new String(e), "utf-8");
        val d = _decrypt(URLDecoder.decode(cookie, "utf-8").getBytes(), privateKey);
        System.out.println(new String(d));
    }
    @SneakyThrows
    private static void write(String file, Key key){
        val out = new FileOutputStream(file);
        out.write(key.getEncoded());
        out.close();
    }

    public String encrypt(String data) {
        return new String(_encrypt(data.getBytes(), publicKey));
    }

    public String decrypt(String data) {
        return new String(_decrypt(data.getBytes(), privateKey));
    }

    @SneakyThrows
    private static byte[] _encrypt(final byte[] data, final PublicKey publicKey) {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    @SneakyThrows
    private static byte[] _decrypt(final byte[] data, final PrivateKey privateKey) {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    @PostConstruct
    private void postConstruct() throws Exception {
        publicKey = loadPublic();
        privateKey = loadPrivate();
    }

    private static PrivateKey loadPrivate() throws Exception {
        File file = new File("src/main/resources/static/rsa.key");
        byte[] bytes = Files.readAllBytes(file.toPath());

        PKCS8EncodedKeySpec ks = new PKCS8EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(ks);
    }

    private static PublicKey loadPublic() throws Exception {
        File file = new File("src/main/resources/static/rsa.pub");
        byte[] bytes = Files.readAllBytes(file.toPath());

        X509EncodedKeySpec ks = new X509EncodedKeySpec(bytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(ks);
    }
}
