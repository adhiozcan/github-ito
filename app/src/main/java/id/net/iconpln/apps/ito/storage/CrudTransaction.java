package id.net.iconpln.apps.ito.storage;

import java.util.List;

/**
 * Created by Ozcan on 17/04/2017.
 */

interface CrudTransaction<T> {
    /**
     * save data with parameter
     * <p>You can include the parameter using ${@link T} class
     *
     * @return
     */
    T save(Class className, T entity);

    /**
     * save data with parameter
     * <p>You can include the parameter using ${@link T} class
     *
     * @return
     */
    void saveList(Class className, List<T> entity);


    /**
     * update data with parameter
     * <p>You can include the parameter using ${@link T} class
     *
     * @return
     */
    T update(Class className, T entity);

    /**
     * delete with parameter
     * <p>You can include the parameter using ${@link T} class
     *
     * @return
     */
    T delete(Class className, T entity);

    /**
     * find All
     * <p>You can include the parameter using ${@link T} class
     *
     * @return
     */
    List<T> findAll(Class className);

    /**
     * Truncate
     * <p>Delete all data</p>
     *
     * @return
     */
    void truncate(Class className);
}
