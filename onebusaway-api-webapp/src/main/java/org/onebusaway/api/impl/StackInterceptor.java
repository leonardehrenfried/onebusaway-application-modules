package org.onebusaway.api.impl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ModelDrivenInterceptor;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Similar to {@link ModelDrivenInterceptor}, this allows us to push additional
 * objects onto the stack as configured through action annotations. Useful when
 * we're already using {@link ModelDriven} and we want to additionally modify
 * the stack.
 * 
 * @author bdferris
 * 
 */
public class StackInterceptor extends AbstractInterceptor {

  private static final long serialVersionUID = 1L;

  @Override
  public String intercept(ActionInvocation invocation) throws Exception {

    Object action = invocation.getAction();

    Class<?> actionType = action.getClass();

    AddToStack addToStackAnnotation = actionType.getAnnotation(AddToStack.class);

    if (addToStackAnnotation != null) {

      ValueStack stack = invocation.getStack();
      List<Object> toPush = new ArrayList<Object>();

      for (String name : addToStackAnnotation.value()) {
        Object value = stack.findValue(name);
        if (value != null)
          toPush.add(value);
      }

      /**
       * We push the values onto the stack after finding ALL the values first
       */
      for (Object value : toPush)
        stack.push(value);
    }

    return invocation.invoke();
  }

  @Retention(value = RetentionPolicy.RUNTIME)
  @Target(value = ElementType.TYPE)
  public @interface AddToStack {
    String[] value();
  }
}
