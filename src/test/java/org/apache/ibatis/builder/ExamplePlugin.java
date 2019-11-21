/**
 *    Copyright 2009-2018 the original author or authors.
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
package org.apache.ibatis.builder;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Properties;

@Intercepts({
  @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class}),
  @Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class}),
  @Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
  @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})
})
public class ExamplePlugin implements Interceptor {
  private Properties properties;

  private static final Log LOG = LogFactory.getLog(ExamplePlugin.class);

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    LOG.error(invocation.getTarget().getClass().getName() + "." + invocation.getMethod().getName() + "(" + getParamClass(invocation.getArgs()) + ")");
    return invocation.proceed();
  }

  private String getParamClass(Object[] args) {
    StringBuilder sb = new StringBuilder("");
    if (args != null && args.length > 0) {
      for (Object arg : args) {
        if (arg != null && arg.getClass() != null) {
          sb.append(arg.getClass().getSimpleName()).append(",");
        }
      }
      sb.deleteCharAt(sb.length() - 1);
    }
    return sb.toString();
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {
    this.properties = properties;
  }

  public Properties getProperties() {
    return properties;
  }

}
