package no.jenjon13.eeexam.ejb;

import no.jenjon13.eeexam.entities.User;
import org.apache.commons.codec.digest.DigestUtils;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.SecureRandom;

@Stateless
public class UserEJB implements Serializable {

    @PersistenceContext(name = "DefaultUnit")
    private EntityManager em;

    public UserEJB(){
    }

    public boolean createUser(User user, String password) {
        if (password == null || password.isEmpty() || getUser(user.getUserId()) != null) {
            return false;
        }

        String salt = getSalt();
        user.setSalt(salt);

        String hash = computeHash(password, salt);
        user.setHash(hash);

        em.persist(user);
        return true;
    }


    public boolean login(String userId, String password) {
        if (userId == null || userId.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        User User = getUser(userId);
        if (User == null) {
            return false;
        }

        String hash = computeHash(password, User.getSalt());
        return hash.equals(User.getHash());
    }


    public User getUser(String userId){
        return em.find(User.class, userId);
    }

    public int deleteAllUsers() {
        return em.createNamedQuery(User.DELETE_ALL).executeUpdate();
    }
    
    @NotNull
    protected String computeHash(String password, String salt){
        String combined = password + salt;
        return DigestUtils.sha256Hex(combined);
    }

    @NotNull
    protected String getSalt(){
        SecureRandom random = new SecureRandom();
        final int bitsPerChar = 5;
        final int twoPowerOfBits = 32;
        final int n = 26;

        BigInteger bigInteger = new BigInteger(n * bitsPerChar, random);
        return bigInteger.toString(twoPowerOfBits);
    }
}
