package id.net.iconpln.apps.ito.storage;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import id.net.iconpln.apps.ito.model.Model;
import id.net.iconpln.apps.ito.model.WorkOrder;

/**
 * Created by Ozcan on 17/04/2017.
 */

public class StorageTransaction<T> implements CrudTransaction<T> {

    @Override
    public T save(Class className, T entity) {
        String tobeSaved = new Gson().toJson(entity);
        Hawk.put(className.getSimpleName(), tobeSaved);
        return entity;
    }

    @Override
    public void saveList(Class className, List<T> entity) {
        String tobeSaved = new Gson().toJson(entity);
        Hawk.put(className.getSimpleName(), tobeSaved);
    }

    @Override
    public T update(Class className, T entity) {
        List<T> listObject = findAll(className);
        for (T object : listObject) {
            if (object.equals(entity)) {
                listObject.remove(object);
                listObject.add(entity);
            }
        }
        String objectToBeSave = new Gson().toJson(listObject);
        Hawk.put(className.getSimpleName(), objectToBeSave);
        return entity;
    }

    @Override
    public T delete(Class className, T entity) {
        List<T> listObject = findAll(className);
        if (listObject.contains(entity)) {
            listObject.remove(entity);
        }
        String objectToBeSave = new Gson().toJson(listObject);
        Hawk.put(className.getSimpleName(), objectToBeSave);
        return null;
    }

    public T find(Class className) {
        String raw    = Hawk.get(className.getSimpleName(), "");
        T      object = (T) new Gson().fromJson(raw, className);
        return object;
    }

    @Override
    public List<T> findAll(Class className) {
        String raw = Hawk.get(className.getSimpleName(), "");
        if (raw.isEmpty()) return new ArrayList<>();

        if (className.equals(WorkOrder.class)) {
            WorkOrder[] woList = new Gson().fromJson(raw, WorkOrder[].class);
            return (List<T>) Arrays.asList(woList);
        }
        List<T> listObject = (List<T>) new Gson().fromJson(raw, className);
        return listObject;
    }

    public List<T> findAll() {
        String raw = Hawk.get(WorkOrder.class.getSimpleName(), "");
        if (raw.isEmpty()) return new ArrayList<>();

        WorkOrder[] arr    = new Gson().fromJson(raw, WorkOrder[].class);
        List        newArr = Arrays.asList(arr);
        return newArr;
    }

    @Override
    public void truncate(Class className) {
        Hawk.delete(className.getSimpleName());
    }
}
