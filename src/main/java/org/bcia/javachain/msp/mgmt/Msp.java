/**
 * Copyright DingXuan. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bcia.javachain.msp.mgmt;

import com.google.protobuf.InvalidProtocolBufferException;
import org.bcia.javachain.common.exception.JavaChainException;
import org.bcia.javachain.common.log.JavaChainLog;
import org.bcia.javachain.common.log.JavaChainLogFactory;
import org.bcia.javachain.csp.gm.GmCsp;
import org.bcia.javachain.csp.gm.sm2.SM2KeyExport;
import org.bcia.javachain.csp.intfs.IKey;
import org.bcia.javachain.msp.IIdentity;
import org.bcia.javachain.msp.IMsp;
import org.bcia.javachain.msp.ISigningIdentity;
import org.bcia.javachain.msp.entity.IdentityIdentifier;
import org.bcia.javachain.msp.entity.OUIdentifier;
import org.bcia.javachain.msp.entity.VerifyOptions;
import org.bcia.javachain.msp.signer.Signer;
import org.bcia.javachain.protos.common.MspPrincipal;
import org.bcia.javachain.protos.msp.Identities;
import org.bcia.javachain.protos.msp.MspConfigPackage;
import org.bouncycastle.asn1.x509.CertificateList;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.HashMap;
import java.util.Map;

import static org.bcia.javachain.msp.mgmt.MspManager.defaultCspValue;

/**
 * @author zhangmingyang
 * @Date: 2018/3/27
 * @company Dingxuan
 */
public class Msp implements IMsp {
    private static final String CERT = "cert";
    private static JavaChainLog log = JavaChainLogFactory.getLog(Msp.class);
    public int mspVersion;
    public IIdentity[] rootCerts;
    public IIdentity[] intermediateCerts;
    public byte[][] tlsRootCerts;
    public byte[][] tlsIntermediateCerts;
    public HashMap<String, Boolean> certificationTreeInternalNodesMap;
    public Identity signer;
    public IIdentity[] admins;
    public String name;
    public GmCsp csp;
    public VerifyOptions verifyOptions;
    public CertificateList CRL[];
    public Map<String, Byte[][]> ouIdentifiers;
    public MspConfigPackage.FabricCryptoConfig fabricCryptoConfig;
    public boolean ouEnforcement;
    public OUIdentifier clientOU;
    public OUIdentifier peerOU;
    public OUIdentifier orderOU;


    private MspConfigPackage.MSPConfig mspConfig;


    public Msp() {
    }

    public Msp(MspConfigPackage.MSPConfig config) {
        this.mspConfig = config;
    }

    public Msp(int mspVersion, IIdentity[] rootCerts, IIdentity[] intermediateCerts, byte[][] tlsRootCerts, byte[][] tlsIntermediateCerts,
               Identity signer, IIdentity[] admins, String name, GmCsp csp, VerifyOptions verifyOptions, CertificateList[] CRL,
               Map<String, Byte[][]> ouIdentifiers, MspConfigPackage.FabricCryptoConfig fabricCryptoConfig, boolean ouEnforcement,
               OUIdentifier clientOU, OUIdentifier peerOU, OUIdentifier orderOU) {
        this.mspVersion = mspVersion;
        this.rootCerts = rootCerts;
        this.intermediateCerts = intermediateCerts;
        this.tlsRootCerts = tlsRootCerts;
        this.tlsIntermediateCerts = tlsIntermediateCerts;
        this.signer = signer;
        this.admins = admins;
        this.name = name;
        this.csp = csp;
        this.verifyOptions = verifyOptions;
        this.CRL = CRL;
        this.ouIdentifiers = ouIdentifiers;
        this.fabricCryptoConfig = fabricCryptoConfig;
        this.ouEnforcement = ouEnforcement;
        this.clientOU = clientOU;
        this.peerOU = peerOU;
        this.orderOU = orderOU;
    }

    @Override
    public IMsp setup(MspConfigPackage.MSPConfig config) {

        try {
            MspConfigPackage.FabricMSPConfig fabricMSPConfig = MspConfigPackage.FabricMSPConfig.parseFrom(config.getConfig());
            this.name = fabricMSPConfig.getName();
          return   internalSetupFunc(fabricMSPConfig);
        } catch (InvalidProtocolBufferException e) {
            e.printStackTrace();
        }
        return null;
        //return new Msp(config);
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public String getIdentifier() {
        return null;
    }

    @Override
    public ISigningIdentity getSigningIdentity(IdentityIdentifier identityIdentifier) {
        return null;
    }

    @Override
    public ISigningIdentity getDefaultSigningIdentity() {
        // 返回基于国密的默认签名身份实现
        if (defaultCspValue.equalsIgnoreCase("gm")) {


           // return new SigningIdentity(mspConfig);

            return  this.signer;

        } else if (defaultCspValue.equalsIgnoreCase("gmt0016")) {
            return null;
        }
        return null;
    }

    @Override
    public byte[][] getTLSRootCerts() {
        return new byte[0][];
    }

    @Override
    public byte[][] getTLSIntermediateCerts() {
        return new byte[0][];
    }

    @Override
    public void validate(IIdentity id) {

    }

    @Override
    public void satisfiesPrincipal(IIdentity id, MspPrincipal.MSPPrincipal principal) {

    }

    @Override
    public IIdentity deserializeIdentity(byte[] serializedIdentity) {
        return null;
    }

    @Override
    public void isWellFormed(Identities.SerializedIdentity identity) {

    }


    public Msp internalSetupFunc(MspConfigPackage.FabricMSPConfig mspConfig) {
        log.info("通过internalSetupFunc装载配置");
        preSetup(mspConfig);
        setupNodeOus(mspConfig);
        //  postSetup();

        return new Msp(mspVersion, rootCerts, intermediateCerts, tlsRootCerts, tlsIntermediateCerts,
                signer, admins, name, csp, verifyOptions, CRL,
                ouIdentifiers, fabricCryptoConfig, ouEnforcement,
                clientOU, peerOU, orderOU);
    }

    private Msp internalValidateIdentityOusFunc() {
        return null;
    }

    public void preSetup(MspConfigPackage.FabricMSPConfig mspConfig) {
        setupCrypto(mspConfig);
        setupCAs(mspConfig);
        setupAdmins(mspConfig);
        setupCRLs(mspConfig);
        finalizeSetupCAs(mspConfig);
        setupSigningIdentity(mspConfig);
        setupTLSCAs(mspConfig);
        setupOUs(mspConfig);
    }

    public void setupNodeOus(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public static void postSetup(MspConfigPackage.FabricMSPConfig mspConfig) {


    }


    public void setupCrypto(MspConfigPackage.FabricMSPConfig mspConfig) {
        this.fabricCryptoConfig = mspConfig.getCryptoConfig();

    }

    public void setupCAs(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public void setupAdmins(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public void setupCRLs(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public void finalizeSetupCAs(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public void setupSigningIdentity(MspConfigPackage.FabricMSPConfig mspConfig) {
        //通过配置获取签名者,并且赋值
        this.signer = getSigningIdentityFromConf(mspConfig.getSigningIdentity());


    }

    public void setupTLSCAs(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public void setupOUs(MspConfigPackage.FabricMSPConfig mspConfig) {

    }

    public Identity getSigningIdentityFromConf(MspConfigPackage.SigningIdentityInfo signingIdentityInfo) {
        //通过配置获取sidinfo的publicSigner获取公钥和一个身份实例

        try {
            HashMap<String, Object> map = getIdentityFromConf(signingIdentityInfo.getPublicSigner().toByteArray());
            IKey publicKey = (IKey) map.get("publickey");
            //通过ski值获取到私钥,暂时为了测试,先传入公钥
            // Ikey privateKey=this.csp.getKey(publicKey.ski());
            Identity id = (Identity) map.get("Identity");
            Signer signer = new Signer();
            signer.newSigner(this.csp, publicKey);
            Identity identity = new Identity();
            return identity.newSigningIdentity(id.certificate, id.pk, signer.newSigner(this.csp, publicKey), this);

        } catch (JavaChainException e) {
            e.printStackTrace();
        }
        return null;
    }


    public HashMap<String, Object> getIdentityFromConf(byte[] idBytes) throws JavaChainException {
        HashMap<String, Object> map = new HashMap<String, Object>();
        java.security.cert.Certificate certificate = getCertFromPem(idBytes);
        certificate.getPublicKey();

//        IKey certPubK = (SM2Key) this.csp.keyImport(certificate, new GmKeyImportOpts());
        SM2KeyExport certPubK=new SM2KeyExport();
        map.put("publickey", certPubK);

        Identity identity = new Identity(certificate, certPubK.getPublicKey(), this);
        map.put("Identity", identity);
        return map;
    }

    public java.security.cert.Certificate getCertFromPem(byte[] idBytes) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream inputStream = new ByteArrayInputStream(idBytes);
            java.security.cert.Certificate certificate = certificateFactory.generateCertificate(inputStream);
            return certificate;
        } catch (CertificateException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Certificate sanitizeCert(Certificate certificate) {


        return certificate;
    }
}