package com.yx.util;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;


/**
 * RSA��Կ/˽Կ/ǩ�����߰�
 * �ַ�����ʽ����Կ��δ������˵������¶�ΪBASE64�����ʽ<br/>
 * ���ڷǶԳƼ����ٶȼ��仺����һ���ļ���ʹ���������ܶ���ʹ�öԳƼ��ܣ�<br/>
 * �ǶԳƼ����㷨���������ԶԳƼ��ܵ���Կ���ܣ�������֤��Կ�İ�ȫҲ�ͱ�֤�����ݵİ�ȫ
 *
 * @author 
 */
public class RSAUtil2 {
    /**
     * �����㷨RSA
     */
    private static final String KEY_ALGORITHM = "RSA";
    /**
     * ��ȡ��Կ��key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";
    /**
     * ��ȡ˽Կ��key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    /**
     * ǩ���㷨
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    /**
     * ����0
     */
    private static final int ZERO = 0;
    /**
     * RSA��������������С
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA��������������С
     * ����Կλ��Ϊ1024ʱ,������������� 128
     * ����Կλ��Ϊ2048ʱ��Ҫ��Ϊ 256 ��Ȼ�ᱨ��Decryption error��
     */
    private static final int MAX_DECRYPT_BLOCK = 256;
    /**
     * Ĭ��key��С
     */
    private static final int DEFAULT_KEY_SIZE = 1024;

    
    
    
   

  //������Կ��
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

  //��ȡ��Կ(Base64����)
    public static String getPublicKey(KeyPair keyPair){
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return byte2Base64(bytes);
    }
    
    //��ȡ˽Կ(Base64����)
    public static String getPrivateKey(KeyPair keyPair){
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byte2Base64(bytes);
    }
  //��Base64�����Ĺ�Կת����PublicKey����
    public static PublicKey string2PublicKey(String pubStr) throws Exception{
        byte[] keyBytes = base642Byte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
    
    //��Base64������˽Կת����PrivateKey����
    public static PrivateKey string2PrivateKey(String priStr) throws Exception{
        byte[] keyBytes = base642Byte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    
  //�ֽ�����תBase64����
    public static String byte2Base64(byte[] bytes){
        BASE64Encoder dncoder = new BASE64Encoder();
        return dncoder.encode(bytes);
    }
    
    //Base64����ת�ֽ�����
    public static byte[] base642Byte(String base64Key) throws IOException{
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64Key);
    }
    
    
    /**
     * ��Կ����
     *
     * @param data      Դ����
     * @param publicKey ��Կ(BASE64����)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey)
            throws Exception {
        byte[] keyBytes = base642Byte(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return encrypt(data, KeyFactory.getInstance(KEY_ALGORITHM), keyFactory.generatePublic(x509KeySpec));
    }


    /**
     * ˽Կ����
     *
     * @param data       Դ����
     * @param privateKey ˽Կ(BASE64����)
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey)
            throws Exception {
        byte[] keyBytes = base642Byte(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        return encrypt(data, keyFactory, privateK);
    }


    /**
     * ˽Կ����
     *
     * @param encryptedData �Ѽ�������
     * @param privateKey    ˽Կ(BASE64����)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey)
            throws Exception {
        byte[] keyBytes = base642Byte(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        return decrypt(encryptedData, keyFactory, keyFactory.generatePrivate(pkcs8KeySpec));
    }


    /**
     * ��Կ����
     *
     * @param encryptedData �Ѽ�������
     * @param publicKey     ��Կ(BASE64����)
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey)
            throws Exception {
        byte[] keyBytes = base642Byte(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        return decrypt(encryptedData, keyFactory, publicK);

    }


    /**
     * ��˽Կ����Ϣ��������ǩ��
     *
     * @param data       �Ѽ�������
     * @param privateKey ˽Կ(BASE64����)
     * @return
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = base642Byte(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return byte2Base64(signature.sign());
    }


    /**
     * У������ǩ��
     *
     * @param data      �Ѽ�������
     * @param publicKey ��Կ(BASE64����)
     * @param sign      ����ǩ��
     * @return
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign)
            throws Exception {
        byte[] keyBytes = base642Byte(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initVerify(publicK);
        signature.update(data);
        return signature.verify(base642Byte(sign));
    }


   

    /**
     * ���ܹ�������
     */
    private static byte[] decrypt(byte[] data, KeyFactory keyFactory, Key key) throws Exception {

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        return encryptAndDecrypt(data, cipher, MAX_DECRYPT_BLOCK);
    }

    /**
     * ���ܹ�������
     */
    private static byte[] encrypt(byte[] data, KeyFactory keyFactory, Key key) throws Exception {
        // �����ݼ���
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptAndDecrypt(data, cipher, MAX_ENCRYPT_BLOCK);
    }


    /**
     * ���ܽ��ֶܷδ���������
     * maxSize��󳤶�117
     */
    private static byte[] encryptAndDecrypt(byte[] data, Cipher cipher, int maxSize) throws Exception {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = ZERO;
        byte[] cache;
        int i = ZERO;
        // �����ݷֶμ���
        while (inputLen - offSet > ZERO) {
            if (inputLen - offSet > maxSize) {
                cache = cipher.doFinal(data, offSet, maxSize);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, ZERO, cache.length);
            i++;
            offSet = i * maxSize;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

}
