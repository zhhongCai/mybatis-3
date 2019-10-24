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
package org.apache.ibatis.reflection.wrapper;

import java.util.List;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.property.PropertyTokenizer;

/**
 * 对象包装类
 * @author Clinton Begin
 */
public interface ObjectWrapper {

  /**
   * 根据属性获取值
   * @param prop
   * @return
   */
  Object get(PropertyTokenizer prop);

  /**
   * 设置属性值
   * @param prop
   * @param value
   */
  void set(PropertyTokenizer prop, Object value);

  /**
   * 查找对象的属性
   * @param name
   * @param useCamelCaseMapping
   * @return
   */
  String findProperty(String name, boolean useCamelCaseMapping);

  /**
   * 获取所有getter方法的名称
   * @return
   */
  String[] getGetterNames();

  /**
   * 获取所有setter方法的名称
   * @return
   */
  String[] getSetterNames();

  /**
   * 获取给定属性的setter方法的返回值类型
   * @param name
   * @return
   */
  Class<?> getSetterType(String name);

  /**
   * 获取给定属性的getter方法的参数类型
   * @param name
   * @return
   */
  Class<?> getGetterType(String name);

  /**
   * 属性是否有setter方法
   * @param name
   * @return
   */
  boolean hasSetter(String name);

  /**
   * 属性是否有getter方法
   * @param name
   * @return
   */
  boolean hasGetter(String name);

  /**
   * TODO caizh 初始化属性值？？？
   * @param name
   * @param prop
   * @param objectFactory
   * @return
   */
  MetaObject instantiatePropertyValue(String name, PropertyTokenizer prop, ObjectFactory objectFactory);

  /**
   * 是否集合
   * @return
   */
  boolean isCollection();

  /**
   * 添加单个元素
   * @param element
   */
  void add(Object element);

  /**
   * 批量添加元素
   * @param element
   * @param <E>
   */
  <E> void addAll(List<E> element);

}
