package org.beanone;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.beanutils.ConversionException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.integration.support.json.JsonObjectMapper;
import org.springframework.integration.support.json.JsonObjectMapperProvider;
import org.springframework.util.StringUtils;
import org.springframework.validation.DataBinder;

/**
 * Maps a JavaBean to a flattened Map structure and vise versa.
 * <p/>
 * Issues:
 * <p/>
 * The JavaBean cannot have properties of type Set.
 *
 * @author Hongyan Li
 *
 */
public class BeanMapper {
	private static final JsonObjectMapper<?, ?> JSON_OBJECT_MAPPER = JsonObjectMapperProvider
	        .newInstance();

	private static void doFlatten(final String propertyPrefix,
	        Map<String, Object> inputMap, Map<String, Object> resultMap) {
		String prefix = propertyPrefix;
		if (StringUtils.hasText(propertyPrefix)) {
			prefix = propertyPrefix + ".";
		}
		for (final Entry<String, Object> entry : inputMap.entrySet()) {
			doProcessElement(prefix + entry.getKey(), entry.getValue(),
			        resultMap);
		}
	}

	private static void doProcessCollection(String propertyPrefix,
	        Collection<?> list, Map<String, Object> resultMap) {
		int counter = 0;
		for (final Object element : list) {
			doProcessElement(propertyPrefix + "[" + counter + "]", element,
			        resultMap);
			counter++;
		}
	}

	@SuppressWarnings("unchecked")
	private static void doProcessElement(String propertyPrefix, Object element,
	        Map<String, Object> resultMap) {
		if (element instanceof Map) {
			doFlatten(propertyPrefix, (Map<String, Object>) element, resultMap);
		} else if (element instanceof Collection) {
			doProcessCollection(propertyPrefix, (Collection<?>) element,
			        resultMap);
		} else {
			resultMap.put(propertyPrefix, element);
		}
	}

	private static Map<String, Object> flattenMap(Map<String, Object> result) {
		final Map<String, Object> resultMap = new HashMap<>();
		doFlatten("", result, resultMap);
		removeNullValues(resultMap);
		return resultMap;
	}

	/**
	 * un-flattens the passed in Map of primitive values into a JavaBean of the
	 * passed in class type.
	 *
	 * @param map
	 *            a Map that contains all the primitive attributes of the bean
	 *            with dotted format to represent the keys for attributes of
	 *            non-primitive attributes.
	 * @param clazz
	 *            the class type of the JavaBean the passed in map is
	 *            un-flattened to.
	 * @return a JavaBean of the passed in class type.
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 */
	public static <T> T fromMap(Map<String, Object> map, Class<T> clazz) {
		try {
			final T target = clazz.newInstance();

			final DataBinder binder = new DataBinder(target);
			final DefaultConversionService conversionService = new DefaultConversionService();

			binder.setConversionService(conversionService);
			binder.bind(new MutablePropertyValues(map));

			return target;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new ConversionException(e.getMessage(), e);
		}
	}

	private static void removeNullValues(final Map<String, Object> resultMap) {
		for (final Entry<String, Object> entry : resultMap.entrySet()) {
			if (entry.getValue() == null) {
				resultMap.remove(entry.getKey());
			}
		}
	}

	/**
	 * Flattens the passed in bean into a Map of primitive values.
	 *
	 * @param bean
	 *            a JavaBean object.
	 * @return all primitive attributes of the bean are flatten into a map with
	 *         the keys represent the attributes in a dotted format to represent
	 *         the object structure.
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toMap(Serializable bean) {
		if (bean == null) {
			throw new IllegalArgumentException("The passed in bean is null!");
		}
		try {
			final Map<String, Object> result = JSON_OBJECT_MAPPER
			        .fromJson(JSON_OBJECT_MAPPER.toJson(bean), Map.class);
			return flattenMap(result);
		} catch (final Exception e) {
			throw new ConversionException(e.getMessage(), e);
		}
	}

	private BeanMapper() {
		// private for utility
	}
}
