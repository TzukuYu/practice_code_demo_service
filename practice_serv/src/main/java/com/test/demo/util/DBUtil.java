package com.test.demo.util;

import com.test.demo.meta.CodeTest;
import com.test.demo.meta.QuestionEntity;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class DBUtil {

    private static final SessionFactory factory;

    static {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        factory = new MetadataSources(ssr).buildMetadata().buildSessionFactory();
    }

    private DBUtil() {

    }

    public static void saveOne(String codeId, String codeTitle, String codeAns, String pwd) {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(new CodeTest(
                    encode(codeId, pwd),
                    encode(codeTitle, pwd),
                    encode(codeAns, pwd)
            ));
            tx.commit();
        }
    }

    public static QuestionEntity selectOne(String codeId, String pwd) {
        try (Session session = factory.openSession()) {
            String hql = "from CodeTest where codeId = :codeId";
            Query<CodeTest> query = session.createQuery(hql, CodeTest.class);
            query.setParameter("codeId", codeId);

            CodeTest ct = query.uniqueResult();

            return new QuestionEntity(decode(ct.getCodeTitle(), pwd), decode(ct.getCodeAns(), pwd));
        }

    }

    private static String encode(String str, String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(pwd.getBytes());
            String aesKey = Base64.getEncoder().encodeToString(digest).substring(0, 16);
            byte[] bytes = aesKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(bytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
            byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }

    private static String decode(String str, String pwd) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(pwd.getBytes());
            String aesKey = Base64.getEncoder().encodeToString(digest).substring(0, 16);
            byte[] bytes = aesKey.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec skeySpec = new SecretKeySpec(bytes, "AES");
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");

            cipher.init(Cipher.DECRYPT_MODE, skeySpec);

            byte[] decodeBase64 = Base64.getDecoder().decode(str);

            byte[] decrypted = cipher.doFinal(decodeBase64);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

    }
}
