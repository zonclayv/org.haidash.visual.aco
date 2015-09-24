package org.haidash.visual.aco.model;

import com.carrotsearch.hppc.IntArrayList;
import org.haidash.visual.aco.model.entity.Link;
import org.haidash.visual.aco.model.entity.Node;

import java.util.List;

/**
 * Интерфейс определяющий поведение муравья
 */
public interface Agent {

    /**
     * Получение пройденных {@link Link} агентом
     *
     * @return {@link java.util.List} Link
     */
    public List<Link> getPath();

    /**
     * Получение {@link IntArrayList} содержащий остаток топлива в вершинах
     *
     * @return {@link IntArrayList} содержащий остаток топлива в вершинах
     */
    public IntArrayList getSpentFuelLevel();

    /**
     * Получение общей стоимости пути
     *
     * @return стоимость пути
     */
    public int getTotalCost();

    /**
     * Закончилось ли топливо у агента
     *
     * @return true, если закончилось топливо
     */
    public boolean isOutOfFuel();

    /**
     * Запуск процесса поиска пути
     */
    public void run();
}
