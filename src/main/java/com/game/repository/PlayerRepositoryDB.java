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

import jakarta.annotation.PreDestroy;

import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
//        properties.put(Environment.DRIVER, "com.mysql.cj.jdbc.Driver");
//        properties.put(Environment.URL, "jdbc:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.USER, "root");
        properties.put(Environment.PASS, "root");
        properties.put(Environment.HBM2DDL_AUTO, "update");
//        properties.put(Environment.SHOW_SQL, true);

        sessionFactory = new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                NativeQuery<Player> query = session.createNativeQuery("SELECT * FROM player", Player.class);
                query.setFirstResult(pageNumber * pageSize);
                query.setMaxResults(pageSize);
                List<Player> playerList = query.list();

                tx.commit();
                return playerList;
            } catch (Exception e) {
                tx.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                Query<Long> playerCountAll = session.createNamedQuery("playerCountAll", Long.class);
                int count = Math.toIntExact(playerCountAll.uniqueResult());

                tx.commit();
                return count;
            } catch (Exception exception) {
                tx.rollback();
                throw new RuntimeException(exception);
            }
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                Long newPlayerID = (Long) session.save(player);
                session.flush();
                Player newPlayer = session.find(Player.class, newPlayerID);
                tx.commit();
                return newPlayer;
            } catch (Exception e) {
                tx.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                session.update(player);
                tx.commit();
                return player;
            } catch (Exception e) {
                tx.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                Player player = session.find(Player.class, id);
                tx.commit();
                return Optional.of(player);
            } catch (Exception e) {
                tx.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            try {
                session.remove(player);
                tx.commit();
            } catch (Exception e) {
                tx.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}