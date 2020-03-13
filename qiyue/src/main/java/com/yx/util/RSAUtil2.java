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
 * RSA公钥/私钥/签名工具包
 * 字符串格式的密钥在未在特殊说明情况下都为BASE64编码格式<br/>
 * 由于非对称加密速度极其缓慢，一般文件不使用它来加密而是使用对称加密，<br/>
 * 非对称加密算法可以用来对对称加密的密钥加密，这样保证密钥的安全也就保证了数据的安全
 *
 * @author 
 */
public class RSAUtil2 {
    /**
     * 加密算法RSA
     */
    private static final String KEY_ALGORITHM = "RSA";
    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RSAPublicKey";
    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RSAPrivateKey";
    /**
     * 签名算法
     */
    private static final String SIGNATURE_ALGORITHM = "MD5withRSA";
    /**
     * 常量0
     */
    private static final int ZERO = 0;
    /**
     * RSA最大加密明文最大大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    /**
     * RSA最大解密密文最大大小
     * 当密钥位数为1024时,解密密文最大是 128
     * 当密钥位数为2048时需要改为 256 不然会报错（Decryption error）
     */
    private static final int MAX_DECRYPT_BLOCK = 256;
    /**
     * 默认key大小
     */
    private static final int DEFAULT_KEY_SIZE = 1024;

    
    
    
   

  //生成秘钥对
    public static KeyPair getKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        KeyPair keyPair = keyPairGenerator.generateKeyPair();
        return keyPair;
    }

  //获取公钥(Base64编码)
    public static String getPublicKey(KeyPair keyPair){
        PublicKey publicKey = keyPair.getPublic();
        byte[] bytes = publicKey.getEncoded();
        return byte2Base64(bytes);
    }
    
    //获取私钥(Base64编码)
    public static String getPrivateKey(KeyPair keyPair){
        PrivateKey privateKey = keyPair.getPrivate();
        byte[] bytes = privateKey.getEncoded();
        return byte2Base64(bytes);
    }
  //将Base64编码后的公钥转换成PublicKey对象
    public static PublicKey string2PublicKey(String pubStr) throws Exception{
        byte[] keyBytes = base642Byte(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        return publicKey;
    }
    
    //将Base64编码后的私钥转换成PrivateKey对象
    public static PrivateKey string2PrivateKey(String priStr) throws Exception{
        byte[] keyBytes = base642Byte(priStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        return privateKey;
    }
    
  //字节数组转Base64编码
    public static String byte2Base64(byte[] bytes){
        BASE64Encoder dncoder = new BASE64Encoder();
        return dncoder.encode(bytes);
    }
    
    //Base64编码转字节数组
    public static byte[] base642Byte(String base64Key) throws IOException{
        BASE64Decoder decoder = new BASE64Decoder();
        return decoder.decodeBuffer(base64Key);
    }
    
    
    /**
     * 公钥加密
     *
     * @param data      源数据
     * @param publicKey 公钥(BASE64编码)
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
     * 私钥加密
     *
     * @param data       源数据
     * @param privateKey 私钥(BASE64编码)
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
     * 私钥解密
     *
     * @param encryptedData 已加密数据
     * @param privateKey    私钥(BASE64编码)
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
     * 公钥解密
     *
     * @param encryptedData 已加密数据
     * @param publicKey     公钥(BASE64编码)
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
     * 用私钥对信息生成数字签名
     *
     * @param data       已加密数据
     * @param privateKey 私钥(BASE64编码)
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
     * 校验数字签名
     *
     * @param data      已加密数据
     * @param publicKey 公钥(BASE64编码)
     * @param sign      数字签名
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
     * 解密公共方法
     */
    private static byte[] decrypt(byte[] data, KeyFactory keyFactory, Key key) throws Exception {

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        return encryptAndDecrypt(data, cipher, MAX_DECRYPT_BLOCK);
    }

    /**
     * 加密公共方法
     */
    private static byte[] encrypt(byte[] data, KeyFactory keyFactory, Key key) throws Exception {
        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return encryptAndDecrypt(data, cipher, MAX_ENCRYPT_BLOCK);
    }


    /**
     * 加密解密分段处理公共方法
     * maxSize最大长度117
     */
    private static byte[] encryptAndDecrypt(byte[] data, Cipher cipher, int maxSize) throws Exception {
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = ZERO;
        byte[] cache;
        int i = ZERO;
        // 对数据分段加密
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
