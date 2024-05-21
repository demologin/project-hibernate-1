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
        properties.put(Environment.URL, "jdbc:p6spy:postgresql://localhost:7432/postgres");
        properties.put(Environment.USER, "postgres");
        properties.put(Environment.PASS, "postgres");
        properties.put(Environment.SHOW_SQL, "true");
        properties.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQLDialect");
        properties.put(Environment.HBM2DDL_AUTO, "update");
        sessionFactory = new Configuration().setProperties(properties).addAnnotatedClass(com.game.entity.Player.class).buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        Session session = sessionFactory.openSession();
        NativeQuery<Player> query = null;
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                query = session.createNativeQuery("select * from rpg.player" , Player.class);
                query.setFirstResult(pageNumber * pageSize);
                query.setMaxResults(pageSize);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
            assert query != null;
            return query.list();
        }
    }


    @Override
    public int getAllCount() {
        Session session = sessionFactory.openSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            Query<Player> query = null;
            try {
                query = session.createNamedQuery("allCount" , Player.class);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
            assert query != null;
            return query.list().size();
        }
    }

    @Override
    public Player save(Player player) {
        Session session = sessionFactory.openSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                session.persist(player);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        Session session = sessionFactory.openSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                session.merge(player);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
            return player;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        Session session = sessionFactory.openSession();
        Player player = new Player();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                player = session.get(Player.class, id);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
        return Optional.ofNullable(player);
    }

    @Override
    public void delete(Player player) {
        Session session = sessionFactory.openSession();
        try (session) {
            Transaction transaction = session.beginTransaction();
            try {
                session.remove(player);
                transaction.commit();
            } catch (Exception e) {
                transaction.rollback();
            }
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}