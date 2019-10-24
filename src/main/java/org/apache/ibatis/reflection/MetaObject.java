/**
 *    Copyright 2009-2019 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.reflection;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;
import org.apache.ibatis.reflection.wrapper.BeanWrapper;
import org.apache.ibatis.reflection.wrapper.CollectionWrapper;
import org.apache.ibatis.reflection.wrapper.MapWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;

/**
 * @author Clinton Begin
 */
public class MetaObject {
  /**
   * 原始对象
   */
  private final Object originalObject;
  /**
   * 原始对象的包装对象
   */
  private final ObjectWrapper objectWrapper;
  /**
   * 原始对象的工厂
   */
  private final ObjectFactory objectFactory;
  /**
   * 原始对象的包装对象的工厂
   */
  private final ObjectWrapperFactory objectWrapperFactory;
  /**
   * 反射工厂
   */
  private final ReflectorFactory reflectorFactory;

  private MetaObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
    this.originalObject = object;
    this.objectFactory = objectFactory;
    this.objectWrapperFactory = objectWrapperFactory;
    this.reflectorFactory = reflectorFactory;

    if (object instanceof ObjectWrapper) {
      this.objectWrapper = (ObjectWrapper) object;
    } else if (objectWrapperFactory.hasWrapperFor(object)) {
      this.objectWrapper = objectWrapperFactory.getWrapperFor(this, object);
    } else if (object instanceof Map) {
      this.objectWrapper = new MapWrapper(this, (Map) object);
    } else if (object instanceof Collection) {
      this.objectWrapper = new CollectionWrapper(this, (Collection) object);
    } else {
      this.objectWrapper = new BeanWrapper(this, object);
    }
  }

  public static MetaObject forObject(Object object, ObjectFactory objectFactory, ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
    if (object == null) {
      return SystemMetaObject.NULL_META_OBJECT;
    } else {
      return new MetaObject(object, objectFactory, objectWrapperFactory, reflectorFactory);
    }
  }

  public ObjectFactory getObjectFactory() {
    return objectFactory;
  }

  public ObjectWrapperFactory getObjectWrapperFactory() {
    return objectWrapperFactory;
  }

  public ReflectorFactory getReflectorFactory() {
    return reflectorFactory;
  }

  public Object getOriginalObject() {
    return originalObject;
  }

  /************使用引用的objectWrapper实现ObjectWrapper中定义的方法*************/
  /**
   * 查找属性
   * @param propName
   * @param useCamelCaseMapping
   * @return
   */
  public String findProperty(String propName, boolean useCamelCaseMapping) {
    return objectWrapper.findProperty(propName, useCamelCaseMapping);
  }

  /**
   * 获取所有getter方法的名称
   * @return
   */
  public String[] getGetterNames() {
    return objectWrapper.getGetterNames();
  }

  /**
   * 获取所有setter方法的名称
   * @return
   */
  public String[] getSetterNames() {
    return objectWrapper.getSetterNames();
  }

  /**
   * 获取给定属性的setter方法的返回值类型
   * @param name
   * @return
   */
  public Class<?> getSetterType(String name) {
    return objectWrapper.getSetterType(name);
  }

  /**
   * 获取给定属性的getter方法的参数类型
   * @param name
   * @return
   */
  public Class<?> getGetterType(String name) {
    return objectWrapper.getGetterType(name);
  }

  /**
   * 属性是否有setter方法
   * @param name
   * @return
   */
  public boolean hasSetter(String name) {
    return objectWrapper.hasSetter(name);
  }

  /**
   * 属性是否有getter方法
   * @param name
   * @return
   */
  public boolean hasGetter(String name) {
    return objectWrapper.hasGetter(name);
  }

  /**
   * 根据属性获取值
   * @param name
   * @return
   */
  public Object getValue(String name) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    // 有多级属性的情况
    if (prop.hasNext()) {
      // 根据索引属性名取对应的metaObject
      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
        return null;
      } else {
        return metaValue.getValue(prop.getChildren());
      }
    } else {
      return objectWrapper.get(prop);
    }
  }

  /**
   * 设置属性值
   * @param name
   * @param value
   */
  public void setValue(String name, Object value) {
    PropertyTokenizer prop = new PropertyTokenizer(name);
    if (prop.hasNext()) {
      MetaObject metaValue = metaObjectForProperty(prop.getIndexedName());
      if (metaValue == SystemMetaObject.NULL_META_OBJECT) {
        if (value == null) {
          // don't instantiate child path if value is null
          return;
        } else {
          metaValue = objectWrapper.instantiatePropertyValue(name, prop, objectFactory);
        }
      }
      metaValue.setValue(prop.getChildren(), value);
    } else {
      objectWrapper.set(prop, value);
    }
  }

  /**
   * 获取属性对应的metaObject
   * @param name
   * @return
   */
  public MetaObject metaObjectForProperty(String name) {
    Object value = getValue(name);
    return MetaObject.forObject(value, objectFactory, objectWrapperFactory, reflectorFactory);
  }

  public ObjectWrapper getObjectWrapper() {
    return objectWrapper;
  }

  public boolean isCollection() {
    return objectWrapper.isCollection();
  }

  public void add(Object element) {
    objectWrapper.add(element);
  }

  public <E> void addAll(List<E> list) {
    objectWrapper.addAll(list);
  }

}
