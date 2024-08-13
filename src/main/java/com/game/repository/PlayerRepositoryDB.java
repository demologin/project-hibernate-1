package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.HBM2DDL_AUTO, "update");

        sessionFactory = new Configuration().addAnnotatedClass(Player.class)
                .addProperties(properties).buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            String sql = "select * from player";
            NativeQuery<Player> nativeQuery = session.createNativeQuery(sql, Player.class);
            nativeQuery.setFirstResult(pageNumber * pageSize);
            nativeQuery.setMaxResults(pageSize);
            List<Player> players = nativeQuery.getResultList();
            transaction.commit();
            return players;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public int getAllCount() {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            Query<Long> query = session.createNamedQuery("SELECT_COUNT_PLAYERS", Long.class);
            Long allCount =  query.uniqueResult();
            transaction.commit();
            return Math.toIntExact(allCount);
        }catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public Player save(Player player) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.save(player);
            transaction.commit();
            return player;
        }catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public Player update(Player player) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try {
            session.update(player);
            transaction.commit();
            return player;
        } catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        try{
            Player player = session.find(Player.class, id);
            transaction.commit();
            return Optional.of(player);
        }catch (Exception e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public void delete(Player player) {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        try{
            session.delete(player);
            tx.commit();
        }catch (Exception e) {
            tx.rollback();
            throw e;
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();

    }
}