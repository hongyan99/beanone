package org.beanone;

/**
 * A callback interface of {@link BeanHistory} which allows clients of
 * {@link BeanHistory} to plug-in a bean update logic.
 *
 * @author Hongyan Li
 *
 * @param <T>
 *            the type of bean to be updated.
 */
@FunctionalInterface
public interface BeanUpdater<T> {
	/**
	 * Updates the passed in bean.
	 *
	 * @param bean
	 *            the bean to be updated.
	 */
	void update(T bean);
}
