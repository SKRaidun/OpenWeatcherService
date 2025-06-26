package ru.OpenWeather.DAO;

import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.OpenWeather.models.Sessions;
import ru.OpenWeather.models.User;

import java.sql.Timestamp;
import java.util.List;

@Component
public class UserDAO {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(readOnly = true)
    public boolean findLogin(String login) {
        Session session = sessionFactory.getCurrentSession();
        String hql = "From User u WHERE u.login = :login";
        TypedQuery<User> query = session.createQuery(hql, User.class);
        query.setParameter("login", login);
        List<User> users = query.getResultList();
        return users.isEmpty();
    }

    @Transactional(readOnly = true)
    public User findUserByID(int id) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "From User u WHERE u.id = :id";
        TypedQuery<User> query = session.createQuery(hql, User.class);
        query.setParameter("id", id);
        List<User> users = query.getResultList();

        if (!users.isEmpty()) {
            System.out.println(users.get(0).getLocations());
                return users.get(0);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public User findUser(String userLogin, String password) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "From User u WHERE u.login = :login";
        TypedQuery<User> query = session.createQuery(hql, User.class);
        query.setParameter("login", userLogin);
        List<User> users = query.getResultList();

        if (!users.isEmpty()) {
            if (users.get(0).getPassword().equals(password)) {
                return users.get(0);
            }
        }
        return null;
    }

    @Transactional
    public void createUser(User user) {
        Session session = sessionFactory.getCurrentSession();
        try {
            session.persist(user);
        } catch (ConstraintViolationException e) {}
    }

    @Transactional(readOnly = true)
    public Sessions findSessionByID(User user) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "From Sessions s WHERE s.user.id = :userID";
        TypedQuery<Sessions> query = session.createQuery(hql, Sessions.class);
        query.setParameter("userID", user.getId());
        List<Sessions> sessions = query.getResultList();

        if (sessions.isEmpty()) {
            return null;
        }

        return sessions.get(0);
    }

}
