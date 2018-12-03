package com.crm.message.core.im.utils;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.json.JSONObject;
import org.wah.doraemon.security.exception.UtilsException;

import java.io.CharArrayReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class SignatureUtils{

    private static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    @Getter
    @Setter
    private static class GenTLSSignatureResult{
        private String errMessage;
        private String urlSig;
        private Integer expireTime;
        private Integer initTime;
    }

    @Getter
    @Setter
    private static class CheckTLSSignatureResult{
        private String errMessage;
        private Boolean verifyResult;
        private Integer expireTime;
        private Integer initTime;
    }

    public static String get(String sdkAppId, String identifier, String privateKey){
        if(StringUtils.isBlank(sdkAppId)){
            throw new UtilsException("IM应用ID不能为空");
        }
        if(StringUtils.isBlank(identifier)){
            throw new UtilsException("IM用户名不能为空");
        }
        if(StringUtils.isBlank(privateKey)){
            throw new UtilsException("密钥不能为空");
        }

        try{
            GenTLSSignatureResult result = genTLSSignatureEx(Long.parseLong(sdkAppId), identifier, privateKey);

            if(StringUtils.isBlank(result.getUrlSig())){
                throw new UtilsException("签名生成失败");
            }

            return result.getUrlSig();
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    public static boolean check(String signature, String sdkAppId, String identifier, String publicKey){
        if(StringUtils.isBlank(signature)){
            throw new UtilsException("签名不能为空");
        }
        if(StringUtils.isBlank(sdkAppId)){
            throw new UtilsException("IM应用ID不能为空");
        }
        if(StringUtils.isBlank(identifier)){
            throw new UtilsException("IM用户名不能为空");
        }
        if(StringUtils.isBlank(publicKey)){
            throw new UtilsException("密钥不能为空");
        }

        try{
            CheckTLSSignatureResult result = checkTLSSignatureEx(signature, Long.parseLong(sdkAppId), identifier, publicKey);

            return result.getVerifyResult();
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    @Deprecated
    private static GenTLSSignatureResult genTLSSignature(long expire, String strAppid3rd, long skdAppid, String identifier, long accountType, String privStr){
        try{
            GenTLSSignatureResult result = new GenTLSSignatureResult();

            Security.addProvider(new BouncyCastleProvider());
            Reader reader = new CharArrayReader(privStr.toCharArray());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PEMParser parser = new PEMParser(reader);
            Object obj = parser.readObject();
            parser.close();
            PrivateKey privKeyStruct = converter.getPrivateKey((PrivateKeyInfo) obj);

            String jsonString = "{"
                              + "\"TLS.account_type\":\"" + accountType +"\","
                              +"\"TLS.identifier\":\"" + identifier +"\","
                              +"\"TLS.appid_at_3rd\":\"" + strAppid3rd +"\","
                              +"\"TLS.sdk_appid\":\"" + skdAppid +"\","
                              +"\"TLS.expire_after\":\"" + expire +"\""
                              +"}";

            String time = String.valueOf(System.currentTimeMillis() / 1000);
            String SerialString = "TLS.appid_at_3rd:" + strAppid3rd + "\n"
                                + "TLS.account_type:" + accountType + "\n"
                                + "TLS.identifier:" + identifier + "\n"
                                + "TLS.sdk_appid:" + skdAppid + "\n"
                                + "TLS.time:" + time + "\n"
                                + "TLS.expire_after:" + expire +"\n";

            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initSign(privKeyStruct);
            signature.update(SerialString.getBytes(DEFAULT_CHARSET));
            byte[] signatureBytes = signature.sign();

            String sigTLS = Base64.encodeBase64String(signatureBytes);

            JSONObject jsonObject= new JSONObject(jsonString);
            jsonObject.put("TLS.sig", (Object) sigTLS);
            jsonObject.put("TLS.time", (Object) time);
            jsonString = jsonObject.toString();

            Deflater compresser = new Deflater();
            compresser.setInput(jsonString.getBytes(DEFAULT_CHARSET));

            compresser.finish();
            byte[] compressBytes = new byte[512];
            int compressBytesLength = compresser.deflate(compressBytes);
            compresser.end();

            String userSig = new String(Base64UrlUtils.base64EncodeUrl(Arrays.copyOfRange(compressBytes, 0, compressBytesLength)));

            result.setUrlSig(userSig);
            return result;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    @Deprecated
    private static CheckTLSSignatureResult checkTLSSignature(String urlSig, String strAppid3rd, long skdAppid, String identifier, long accountType, String publicKey){
        try{
            CheckTLSSignatureResult result = new CheckTLSSignatureResult();
            Security.addProvider(new BouncyCastleProvider());

            Base64 decoder = new Base64();

            byte[] compressBytes = Base64UrlUtils.base64DecodeUrl(urlSig.getBytes(DEFAULT_CHARSET));

            Inflater decompression =  new Inflater();
            decompression.setInput(compressBytes, 0, compressBytes.length);
            byte[] decompressBytes = new byte [1024];
            int decompressLength = decompression.inflate(decompressBytes);
            decompression.end();

            String jsonString = new String(Arrays.copyOfRange(decompressBytes, 0, decompressLength));

            JSONObject jsonObject= new JSONObject(jsonString);
            String sigTLS = jsonObject.getString("TLS.sig");

            byte[] signatureBytes = decoder.decode(sigTLS.getBytes(DEFAULT_CHARSET));

            String sigTime = jsonObject.getString("TLS.time");
            String sigExpire = jsonObject.getString("TLS.expire_after");

            if(System.currentTimeMillis() / 1000 - Long.parseLong(sigTime) > Long.parseLong(sigExpire)){
                result.setErrMessage("TLS sig is out of date ");
                return result;
            }

            String SerialString = "TLS.appid_at_3rd:" + strAppid3rd + "\n"
                                + "TLS.account_type:" + accountType + "\n"
                                + "TLS.identifier:" + identifier + "\n"
                                + "TLS.sdk_appid:" + skdAppid + "\n"
                                + "TLS.time:" + sigTime + "\n"
                                + "TLS.expire_after:" + sigExpire + "\n";

            Reader reader = new CharArrayReader(publicKey.toCharArray());
            PEMParser parser = new PEMParser(reader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object obj = parser.readObject();
            parser.close();
            PublicKey pubKeyStruct = converter.getPublicKey((SubjectPublicKeyInfo) obj);

            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initVerify(pubKeyStruct);
            signature.update(SerialString.getBytes(DEFAULT_CHARSET));

            result.setVerifyResult(signature.verify(signatureBytes));
            return result;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    private static GenTLSSignatureResult genTLSSignatureEx(long skdAppid, String identifier, String privStr){
        return genTLSSignatureEx(skdAppid, identifier, privStr, 3600 * 24 * 180);
    }

    private static GenTLSSignatureResult genTLSSignatureEx(long skdAppid, String identifier, String privStr, long expire){
        try{
            GenTLSSignatureResult result = new GenTLSSignatureResult();

            Security.addProvider(new BouncyCastleProvider());
            Reader reader = new CharArrayReader(privStr.toCharArray());
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PEMParser parser = new PEMParser(reader);
            Object obj = parser.readObject();
            parser.close();
            PrivateKey privKeyStruct = converter.getPrivateKey((PrivateKeyInfo) obj);

            String jsonString = "{"
                              + "\"TLS.account_type\":\"" + 0 +"\","
                              + "\"TLS.identifier\":\"" + identifier +"\","
                              + "\"TLS.appid_at_3rd\":\"" + 0 +"\","
                              + "\"TLS.sdk_appid\":\"" + skdAppid +"\","
                              + "\"TLS.expire_after\":\"" + expire +"\","
                              + "\"TLS.version\": \"201512300000\""
                              + "}";

            String time = String.valueOf(System.currentTimeMillis() / 1000);

            String SerialString = "TLS.appid_at_3rd:" + 0 + "\n"
                                + "TLS.account_type:" + 0 + "\n"
                                + "TLS.identifier:" + identifier + "\n"
                                + "TLS.sdk_appid:" + skdAppid + "\n"
                                + "TLS.time:" + time + "\n"
                                + "TLS.expire_after:" + expire +"\n";

            Signature signature = Signature.getInstance("SHA256withECDSA", "BC");
            signature.initSign(privKeyStruct);
            signature.update(SerialString.getBytes(DEFAULT_CHARSET));
            byte[] signatureBytes = signature.sign();

            String sigTLS = Base64.encodeBase64String(signatureBytes);

            JSONObject jsonObject= new JSONObject(jsonString);
            jsonObject.put("TLS.sig", (Object) sigTLS);
            jsonObject.put("TLS.time", (Object) time);
            jsonString = jsonObject.toString();

            Deflater compresser = new Deflater();
            compresser.setInput(jsonString.getBytes(Charset.forName("UTF-8")));

            compresser.finish();
            byte[] compressBytes = new byte[512];
            int compressBytesLength = compresser.deflate(compressBytes);
            compresser.end();
            String userSig = new String(Base64UrlUtils.base64EncodeUrl(Arrays.copyOfRange(compressBytes,0,compressBytesLength)));

            result.setUrlSig(userSig);
            return result;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }

    private static CheckTLSSignatureResult checkTLSSignatureEx(String urlSig, long sdkAppid, String identifier, String publicKey){
        try{
            CheckTLSSignatureResult result = new CheckTLSSignatureResult();
            Security.addProvider(new BouncyCastleProvider());

            Base64 decoder = new Base64();

            byte[] compressBytes = Base64UrlUtils.base64DecodeUrl(urlSig.getBytes(DEFAULT_CHARSET));

            Inflater decompression =  new Inflater();
            decompression.setInput(compressBytes, 0, compressBytes.length);
            byte[] decompressBytes = new byte[1024];
            int decompressLength = decompression.inflate(decompressBytes);
            decompression.end();

            String jsonString = new String(Arrays.copyOfRange(decompressBytes, 0, decompressLength));

            JSONObject jsonObject= new JSONObject(jsonString);
            String sigTLS = jsonObject.getString("TLS.sig");

            byte[] signatureBytes = decoder.decode(sigTLS.getBytes(DEFAULT_CHARSET));

            String strSdkAppid = jsonObject.getString("TLS.sdk_appid");
            String sigTime = jsonObject.getString("TLS.time");
            String sigExpire = jsonObject.getString("TLS.expire_after");

            if(Integer.parseInt(strSdkAppid) != sdkAppid){
                result.setErrMessage(MessageFormat.format("sdkappid {0} in tls sig not equal sdkappid {1} in request", strSdkAppid, sdkAppid));
                return result;
            }

            if(System.currentTimeMillis() / 1000 - Long.parseLong(sigTime) > Long.parseLong(sigExpire)){
                result.setErrMessage("TLS sig is out of date");
                return result;
            }

            String SerialString = "TLS.appid_at_3rd:" + 0 + "\n"
                                + "TLS.account_type:" + 0 + "\n"
                                + "TLS.identifier:" + identifier + "\n"
                                + "TLS.sdk_appid:" + sdkAppid + "\n"
                                + "TLS.time:" + sigTime + "\n"
                                + "TLS.expire_after:" + sigExpire + "\n";

            Reader reader = new CharArrayReader(publicKey.toCharArray());
            PEMParser  parser = new PEMParser(reader);
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            Object obj = parser.readObject();
            parser.close();
            PublicKey pubKeyStruct  = converter.getPublicKey((SubjectPublicKeyInfo) obj);

            Signature signature = Signature.getInstance("SHA256withECDSA","BC");
            signature.initVerify(pubKeyStruct);
            signature.update(SerialString.getBytes(DEFAULT_CHARSET));

            result.setVerifyResult(signature.verify(signatureBytes));
            result.setExpireTime(Integer.parseInt(sigExpire));
            result.setInitTime(Integer.parseInt(sigTime));
            return result;
        }catch(Exception e){
            throw new UtilsException(e.getMessage(), e);
        }
    }
}
