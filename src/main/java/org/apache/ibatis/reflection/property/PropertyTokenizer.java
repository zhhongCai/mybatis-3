/**
 *    Copyright 2009-2017 the original author or authors.
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
package org.apache.ibatis.reflection.property;

import java.util.Iterator;

/**
 * 属性分词：
 * 比如：new PropertyTokenizer("RichType.richList[1]").getName() == "richType";
 *      new PropertyTokenizer("RichType.richList[1]").getChildren() == "richList[1];
 *      new PropertyTokenizer("RichType.richList[1]").hasNext() == true;
 *      PropertyTokenizer a = new PropertyTokenizer("RichType.richList[1]").next() == new PropertyTokenizer("richList[1]");
 *      a.getName() == "richList";
 *      a.getChildren() == null;
 *      a.hasNext() == false;
 *      a.getIndex() == 1;
 * RichType.richProperty
 * @author Clinton Begin
 */
public class PropertyTokenizer implements Iterator<PropertyTokenizer> {
  /**
   * 一级属性名,比如：richType，richList
   */
  private String name;
  /**
   * 索引的属性名，比如：richType,richList[1]
   */
  private final String indexedName;
  /**
   * 如果属性是集合对象，表示集合的索引
   */
  private String index;
  /**
   * 子属性，比如: richList[1]
   */
  private final String children;

  public PropertyTokenizer(String fullname) {
    int delim = fullname.indexOf('.');
    if (delim > -1) {
      name = fullname.substring(0, delim);
      children = fullname.substring(delim + 1);
    } else {
      name = fullname;
      children = null;
    }
    indexedName = name;
    delim = name.indexOf('[');
    if (delim > -1) {
      index = name.substring(delim + 1, name.length() - 1);
      name = name.substring(0, delim);
    }
  }

  public String getName() {
    return name;
  }

  public String getIndex() {
    return index;
  }

  public String getIndexedName() {
    return indexedName;
  }

  public String getChildren() {
    return children;
  }

  @Override
  public boolean hasNext() {
    return children != null;
  }

  @Override
  public PropertyTokenizer next() {
    return new PropertyTokenizer(children);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException("Remove is not supported, as it has no meaning in the context of properties.");
  }
}
