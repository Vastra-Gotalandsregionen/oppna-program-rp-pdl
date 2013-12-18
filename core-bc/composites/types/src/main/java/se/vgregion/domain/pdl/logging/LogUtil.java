package se.vgregion.domain.pdl.logging;

import org.apache.commons.collections.BeanMap;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by clalu4 on 2013-12-15.
 */
public class LogUtil {

    private IdentityHashMap processedNodes = new IdentityHashMap();

    public Set<String> findAnnotatedLogValues(Object inObjGraph, UserAction... withAnyOfTheseActions) {
        final Set<String> result = new TreeSet<String>();
        processedNodes.clear();
        findLogValues(inObjGraph, result, withAnyOfTheseActions);
        return result;
    }

    private void findLogValues(Object inObjGraph, Set<String> result, UserAction... withAnyOfTheseActions) {
        if (processedNodes.containsKey(inObjGraph) || inObjGraph == null || inObjGraph.getClass().isPrimitive()) {
            return;
        }
        processedNodes.put(inObjGraph, inObjGraph);
        Class type = inObjGraph.getClass();
        Field[] fields = type.getDeclaredFields();

        MyBeanMap bm = new MyBeanMap(inObjGraph);

        for (Object key : bm.keySet()) {
            String name = (String) key;
            LogThisField logThisField = bm.getLogThisField(name);
            if (logThisField != null) {
                if (withAnyOfTheseActions == null || withAnyOfTheseActions.length == 0
                        || new ArrayList(Arrays.asList(withAnyOfTheseActions)).removeAll(
                        Arrays.asList(logThisField.onActions()))) {

                    String namePart = inObjGraph.getClass().getSimpleName() + "." + name;
                    result.add("'" + namePart + ":" + bm.get(name) + "'");
                }
            }
            if (inObjGraph instanceof Collection) {
                for (Object item : ((Collection) inObjGraph)) {
                    findLogValues(item, result, withAnyOfTheseActions);
                }
            } else if (inObjGraph instanceof Map) {
                Map map = (Map) inObjGraph;
                for (Object entry : map.entrySet()) {
                    findLogValues(entry, result, withAnyOfTheseActions);
                }
            } else {
                findLogValues(bm.get(name), result, withAnyOfTheseActions);
            }
        }
    }

    public static class MyBeanMap extends BeanMap {

        private final Map<String, Field> key2fields = new HashMap<String, Field>() {
            @Override
            public Field get(Object key) {
                if (containsKey(key)) {
                    return super.get(key);
                }
                try {
                    Field field = getBean().getClass().getField((String) key);
                    put((String) key, field);
                    return field;
                } catch (NoSuchFieldException e) {
                    return null;
                }
            }
        };


        public MyBeanMap(Object obj) {
            super(obj);
            getPublicFieldNamesMinusGetterVersions();
        }

        LogThisField getLogThisField(String forProperty) {
            Field field = null;
            field = key2fields.get(forProperty);
            if (field == null) {
                Method getter = getReadMethod(LogThisField.class);
                if (getter != null) {
                    return getter.getAnnotation(LogThisField.class);
                }
                return null;
            }
            LogThisField logThisField = field.getAnnotation(LogThisField.class);
            return logThisField;
        }

        public Object getPublicFieldValue(String key) {
            try {
                Field field = key2fields.get(key);
                field.setAccessible(true);
                return field.get(getBean());
            } catch (Exception e) {
                return null;
            }
        }

        public Collection<Field> getPublicFieldNamesMinusGetterVersions() {
            for (Field field : getBean().getClass().getDeclaredFields()) {
                if (field.isAccessible() && !containsKey(field.getName())) {
                    key2fields.put(field.getName(), field);
                }
            }
            return key2fields.values();
        }


        @Override
        public Set keySet() {
            Set result = new HashSet(super.keySet());
            result.addAll(key2fields.keySet());
            return result;
        }

        @Override
        public Object get(Object name) {
            if (containsKey(name)) {
                return super.get(name);
            }
            return getPublicFieldValue((String) name);
        }
    }

}
