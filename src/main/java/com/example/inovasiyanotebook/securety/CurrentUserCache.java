package com.example.inovasiyanotebook.securety;

import com.example.inovasiyanotebook.model.user.User;
import com.vaadin.flow.server.VaadinSession;

import java.util.function.Function;

/**
 * Кэш текущего пользователя в {@link VaadinSession}.
 * <p>
 * Проверки прав вызываются десятки раз при построении одной страницы (меню, шапка, кнопки),
 * и каждая читала пользователя из БД. Кэш живёт в сессии Vaadin, устаревает через {@link #TTL_MILLIS}
 * и сбрасывается при сохранении пользователя ({@link #clear()}), чтобы смена роли или
 * переключатель «Admin funksiyaları» применялись сразу.
 */
public final class CurrentUserCache {

    static final long TTL_MILLIS = 30_000;

    private CurrentUserCache() {
    }

    private record Entry(User user, long loadedAt) {
    }

    /**
     * Возвращает пользователя из кэша сессии или загружает его через {@code loader} и кладёт в кэш.
     * Вне Vaadin-сессии (шедулер, тесты) просто вызывает {@code loader}.
     */
    public static User resolve(String username, Function<String, User> loader) {
        VaadinSession session = VaadinSession.getCurrent();
        if (session == null || username == null) {
            return loader.apply(username);
        }
        Entry entry = session.getAttribute(Entry.class);
        long now = System.currentTimeMillis();
        if (entry != null && entry.user() != null
                && username.equals(entry.user().getUsername())
                && now - entry.loadedAt() < TTL_MILLIS) {
            return entry.user();
        }
        User user = loader.apply(username);
        session.setAttribute(Entry.class, user == null ? null : new Entry(user, now));
        return user;
    }

    /**
     * Сбрасывает кэш текущей сессии. Вызывать после сохранения пользователя.
     */
    public static void clear() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            session.setAttribute(Entry.class, null);
        }
    }
}
