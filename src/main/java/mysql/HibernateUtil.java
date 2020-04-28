package mysql;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import javax.persistence.NoResultException;

public class HibernateUtil {
    static final Logger rootLogger = LogManager.getRootLogger();
    Session session = null;
    SessionFactory sessionFactory = new Configuration()
            .addAnnotatedClass(Main.class)
            .addAnnotatedClass(Blocklist.class)
            .buildSessionFactory();

    public String getNickByLoginAndPass(String login, String password) {
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            Main query = session.createQuery("SELECT i FROM Main i WHERE i.login = :login AND i.password = :password", Main.class)
                    .setParameter("login", login)
                    .setParameter("password", password)
                    .getSingleResult();
            session.getTransaction().commit();
            return query.getNickname();
        } catch (NoResultException e) {
            session.getTransaction().commit();
            return null;
        }
    }

    public void insertTable(Main who, Main whom) {
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            Blocklist blocklist = new Blocklist(who, whom);
            session.save(blocklist);
            session.getTransaction().commit();
        } catch (NoResultException e) {
            session.getTransaction().commit();
        }
    }

    public Main getMainByNicknameFromMain(String nickname) {
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            Main query = session.createQuery("SELECT i FROM Main i WHERE i.nickname = :nickname", Main.class)
                    .setParameter("nickname", nickname)
                    .getSingleResult();
            session.getTransaction().commit();
            return query;
        } catch (NoResultException e) {
            session.getTransaction().commit();
            return null;
        }
    }

    public String getIdByNick1AndNick2FromBlocklist(Main who, Main whom) {
        try {
            session = sessionFactory.getCurrentSession();
            session.beginTransaction();
            Blocklist query = session.createQuery("SELECT i FROM Blocklist i WHERE i.who = :who AND i.whom = :whom", Blocklist.class)
                    .setParameter("who", who)
                    .setParameter("whom", whom)
                    .getSingleResult();
            session.getTransaction().commit();
            return query.getId().toString();
        } catch (Exception e) {
            session.getTransaction().commit();
            return null;
        }

    }
}
