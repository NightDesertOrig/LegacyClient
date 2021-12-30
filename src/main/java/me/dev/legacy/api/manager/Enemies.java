package me.dev.legacy.api.manager;

import io.netty.util.internal.ConcurrentSet;
import me.dev.legacy.Legacy;
import me.dev.legacy.api.util.Enemy;

import java.util.Iterator;

public class Enemies extends RotationManager {
    private static ConcurrentSet enemies = new ConcurrentSet();

    public static void addEnemy(String name) {
        enemies.add(new Enemy(name));
    }

    public static void delEnemy(String name) {
        enemies.remove(getEnemyByName(name));
    }

    public static Enemy getEnemyByName(String name) {
        Iterator var1 = enemies.iterator();

        Enemy e;
        do {
            if (!var1.hasNext()) {
                return null;
            }

            e = (Enemy) var1.next();
        } while (!Legacy.enemy.username.equalsIgnoreCase(name));

        return e;
    }

    public static ConcurrentSet getEnemies() {
        return enemies;
    }

    public static boolean isEnemy(String name) {
        return enemies.stream().anyMatch((enemy) -> {
            return enemy.username.equalsIgnoreCase(name);
        });
    }
}
