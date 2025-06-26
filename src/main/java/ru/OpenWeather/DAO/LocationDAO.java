package ru.OpenWeather.DAO;

import jakarta.persistence.Transient;
import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.OpenWeather.models.Location;
import ru.OpenWeather.models.Sessions;

import java.util.List;
import java.util.UUID;

@Transactional
@Component
public class LocationDAO {
    private final SessionFactory sessionFactory;


    public LocationDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void createLocation(Location location) {
        Session session = sessionFactory.getCurrentSession();

        Location existingLocation = findLocationByName(location.getName(), location.getUser().getId());

        try {
        if (existingLocation != null) {
            existingLocation.setLatitude(location.getLatitude());
            existingLocation.setLongitude(location.getLongitude());
            existingLocation.setTemperature(location.getTemperature());
            existingLocation.setFeelsLike(location.getFeelsLike());
            existingLocation.setDescription(location.getDescription());
            existingLocation.setHumidity(location.getHumidity());
            session.merge(existingLocation);
        } else {
            session.persist(location);
        }
        } catch (ConstraintViolationException e) {}
    }

    @Transactional(readOnly = true)
    public boolean findLocationByNameAndId(String name, int id) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "From Location l WHERE l.user.id = :id";
        TypedQuery<Location> query = session.createQuery(hql, Location.class);
        query.setParameter("id", id);
        List<Location> locations = query.getResultList();
        for (Location location : locations) {
            if (location.getName().equals(name)) return true;
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Location findLocationByName(String name, int id) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "From Location l WHERE l.user.id = :id";
        TypedQuery<Location> query = session.createQuery(hql, Location.class);
        query.setParameter("id", id);
        List<Location> locations = query.getResultList();
        for (Location location : locations) {
            if (location.getName().equals(name)) return location;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<Location> findLocationsByUserId(int id) {
        Session session = sessionFactory.getCurrentSession();

        String hql = "From Location l WHERE l.user.id = :id";
        TypedQuery<Location> query = session.createQuery(hql, Location.class);
        query.setParameter("id", id);
        List<Location> locations = query.getResultList();
        return locations;
    }

    @Transactional
    public void deleteLocationById(int locationId) {
        Session session = sessionFactory.getCurrentSession();

        Location location = session.find(Location.class, locationId);
        if (location != null) {
            session.remove(location);
        }
    }
}
