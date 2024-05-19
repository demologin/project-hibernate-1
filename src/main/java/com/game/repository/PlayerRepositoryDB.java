package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PreDestroy;

import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        sessionFactory = new Configuration()
                .configure()
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        int offset = pageNumber * pageSize;
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                NativeQuery<Player> nativeQuery = session.createNativeQuery("SELECT * FROM player", Player.class);
                nativeQuery.setFirstResult(offset);
                nativeQuery.setMaxResults(pageSize);
                List<Player> playerList = nativeQuery.list();
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
                Query<Long> query = session.createNamedQuery("allCount", Long.class);
                int count = query.uniqueResult().intValue();
                tx.commit();
                return count;
            } catch (Exception e) {
                tx.rollback();
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Long playerId = (Long) session.save(player);
                session.flush();
                Player savedPlayer = session.find(Player.class, playerId);
                tx.commit();
                return savedPlayer;
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
                Player updatedPlayer = (Player) session.merge(player);
                tx.commit();
                return updatedPlayer;
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
                Player player = session.get(Player.class, id);
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