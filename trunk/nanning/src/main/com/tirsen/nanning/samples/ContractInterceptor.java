package com.tirsen.nanning.samples;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.attribute.Attributes;
import ognl.MethodFailedException;
import ognl.Ognl;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * TODO document ContractInterceptor.
 * Tip: use <code>Class.desiredAssertionStatus()</code> to check wheather to add this interceptor or not, that way
 * you can enable and disable contract-checking in the same way you enable and disable assertions (java -ea and so on).
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirs?n</a>
 * @version $Revision: 1.12 $
 */
public class ContractInterceptor implements MethodInterceptor {
    private static final Log logger = LogFactory.getLog(ContractInterceptor.class);
    private static final Pattern oldPattern =
            Pattern.compile("(.*)\\{old (.*?)}(.*)");

    /**
     * If this is non-null don't execute contracts, used when executing the expressions.
     */
    private ThreadLocal checkContracts = new ThreadLocal();

    
    public Object invoke(Invocation invocation) throws Throwable {
        String ensures = Attributes.getAttribute(invocation.getMethod(), "ensures");
        String requires = Attributes.getAttribute(invocation.getMethod(), "requires");
        String invariant = Attributes.getAttribute(invocation.getMethod().getDeclaringClass(), "invariant");

        StringBuffer parsedEnsure = null;
        List oldValues = null;

        if (checkContracts.get() == null) {
            assertExpressionTrue(invocation, requires,
                                 "precondition violated: {0}");

            // execute and remove the old-references
            if (ensures != null) {
                oldValues = new ArrayList();
                parsedEnsure = new StringBuffer();
                Matcher matcher = oldPattern.matcher(ensures);
                while (matcher.matches()) {
                    String head = matcher.group(1);
                    String old = matcher.group(2);
                    String tail = matcher.group(3);
                    oldValues.add(executeExpression(invocation, old));
                    String oldRef = "#" + getOldReference(oldValues.size() - 1);
                    parsedEnsure.append(head + oldRef + tail);
                    matcher = oldPattern.matcher(tail);
                }
                // if there wasn't any old-references just addLink all of the expression
                if (oldValues.size() == 0) {
                    parsedEnsure.append(ensures);
                }
            }
        }

        Object result = invocation.invokeNext();

        if (checkContracts.get() == null) {

            // check ensures with old-references
            if (parsedEnsure != null) {
                Map context = createContext(invocation);
                for (ListIterator iterator = oldValues.listIterator(); iterator.hasNext();) {
                    Object oldValue = iterator.next();
                    context.put(getOldReference(iterator.previousIndex()), oldValue);
                }

                assertExpressionTrue(parsedEnsure.toString(),
                                     invocation.getProxy(), context, "postcondition violated: " + ensures);
            }

            assertExpressionTrue(invocation, invariant, "invariant violated: {0}");
        }

        return result;
    }

    private static String getOldReference(int i) {
        return "old" + i;
    }

    private Object executeExpression(Invocation invocation, String expression) {
        Map context = createContext(invocation);
        try {
            return executeExpression(expression, invocation.getProxy(), context);
        } catch (Exception e) {
            throw new RuntimeException("Could not execute: " + expression, e);
        }
    }

    private void assertExpressionTrue(Invocation invocation, String expression, String message) {
        if (expression != null) {
            Map context = createContext(invocation);
            assertExpressionTrue(expression, invocation.getProxy(), context, MessageFormat.format(message, new Object[]{expression}));
        }
    }

    private static Map createContext(Invocation invocation) {
        Map variables = Ognl.createDefaultContext(invocation.getProxy());
        variables.put("this", invocation.getProxy());
        Object[] args = invocation.getArgs();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                variables.put("arg" + i, arg);
            }
        }
        return variables;
    }

    private void assertExpressionTrue(String expression, Object root, Map context, String message) {
        // disable execution of contracts when contracts are executed (to avoid looping)
        if (checkContracts.get() == null) {
            checkContracts.set(checkContracts);
            try {
                boolean result;
                Boolean aBoolean = (Boolean) executeExpression(expression, root, context);
                result = aBoolean.booleanValue();
                if (!result) {
                    throw new AssertionError(message);
                }
            } catch (MethodFailedException e) {
                if (e.getReason() instanceof Error) {
                    throw (Error) e.getReason();
                } else {
                    logger.error("Could not execute expression: " + expression);
                    throw new AssertionError("Could not execute expression: " + expression);
                }
            } catch (Exception e) {
                logger.error("Could not execute expression: " + expression);
                throw new AssertionError("Could not execute expression: " + expression);
            } finally {
                checkContracts.set(null);
            }
        }
    }

    private static Object executeExpression(String expression, Object root, Map context) throws Exception {
        return Ognl.getValue(Ognl.parseExpression(expression), context, root);
    }
}
