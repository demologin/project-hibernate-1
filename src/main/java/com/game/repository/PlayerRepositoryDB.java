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

@SuppressWarnings("SqlNoDataSourceInspection")
@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        Properties properties = new Properties();
        properties.put(Environment.DRIVER, "com.p6spy.engine.spy.P6SpyDriver");
        properties.put(Environment.URL, "jdbc:p6spy:mysql://localhost:3306/rpg");
        properties.put(Environment.DIALECT,"org.hibernate.dialect.MySQL8Dialect");
        properties.put(Environment.USER,"root");
        properties.put(Environment.PASS,"admin");
        properties.put(Environment.HBM2DDL_AUTO,"update");

        sessionFactory = new Configuration()
                .addProperties(properties)
                .addAnnotatedClass(Player.class)
                .buildSessionFactory();
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try(Session session = sessionFactory.openSession())
        {
            NativeQuery<Player> nativeQuery = session.createNativeQuery("select * from rpg.player", Player.class);
            nativeQuery.setFirstResult(pageNumber*pageSize);
            nativeQuery.setMaxResults(pageSize);
            return nativeQuery.list();

        }
    }

    @Override
    public int getAllCount() {
        try(Session session = sessionFactory.openSession()){
            Query <Long> getAllCount = session.createNamedQuery("getAllCount",Long.class);
            return Math.toIntExact(getAllCount.getSingleResult());
        }
    }

    @Override
    public Player save(Player player) {
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.save(player);
            transaction.commit();
            return player;
        }
    }

    @Override
    public Player update(Player player) {
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.update(player);
            transaction.commit();
        }
        return player;
    }

    @Override
    public Optional<Player> findById(long id) {
        Player player;
        try(Session session = sessionFactory.openSession()){

            player = session.find(Player.class, id);

        }
        return Optional.of(player);
    }

    @Override
    public void delete(Player player) {
        try(Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            session.delete(player);
            transaction.commit();
        }
    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}