package com.tirsen.nanning.samples;

import com.tirsen.nanning.Interceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.Attributes;
import org.apache.commons.jexl.JexlHelper;
import org.apache.commons.jexl.JexlContext;
import org.apache.commons.jexl.Expression;
import org.apache.commons.jexl.ExpressionFactory;

import java.text.MessageFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

/**
 * TODO document ContractInterceptor.
 * Tip: use <code>Class.desiredAssertionStatus()</code> to check wheather to add this interceptor or not, that way
 * you can enable and disable contract-checking in the same way you enable and disable assertions (java -ea and so on).
 *
 * @author <a href="mailto:jon_tirsen@yahoo.com">Jon Tirsén</a>
 * @version $Revision: 1.3 $
 */
public class ContractInterceptor implements Interceptor {
    private static final Pattern oldPattern =
            Pattern.compile("(.*)\\{old (.*?)}(.*)");

    /**
     * If this is non-null don't execute contracts, used when executing the expressions. 
     */
    private ThreadLocal checkContracts = new ThreadLocal();

    public Object invoke(Invocation invocation) throws Throwable {
        String ensures = Attributes.getAttribute(invocation.getMethod(), "ensures");;
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
                while(matcher.matches()) {
                    String head = matcher.group(1);
                    String old = matcher.group(2);
                    String tail = matcher.group(3);
                    oldValues.add(executeExpression(invocation, old));
                    String oldRef = getOldReference(oldValues.size() - 1);
                    parsedEnsure.append(head + oldRef + tail);
                    matcher = oldPattern.matcher(tail);
                }
                // if there wasn't any old-references just add all of the expression
                if (oldValues.size() == 0) {
                    parsedEnsure.append(ensures);
                }
            }
        }

        Object result = invocation.invokeNext();

        if (checkContracts.get() == null) {

            // check ensures with old-references
            if(parsedEnsure != null) {
                Expression expression = parseExpression(parsedEnsure.toString());
                JexlContext context = createContext(invocation);
                for (ListIterator iterator = oldValues.listIterator(); iterator.hasNext();) {
                    Object oldValue = iterator.next();
                    context.getVars().put(getOldReference(iterator.previousIndex()), oldValue);
                }

                assertExpressionTrue(expression, context, "postcondition violated: " + ensures);
            }

            assertExpressionTrue(invocation, invariant, "invariant violated: {0}");
        }

        return result;
    }

    private String getOldReference(int i) {
        return "$old" + i;
    }

    private Object executeExpression(Invocation invocation, String expressionString) {
        Expression expression = null;
        expression = parseExpression(expressionString);
        JexlContext jc = createContext(invocation);
        try {
            return executeExpression(expression, jc);
        } catch (Exception e) {
            throw new RuntimeException("Could not execute: " + expressionString, e);
        }
    }

    private Expression parseExpression(String expressionString) {
        try {
            return ExpressionFactory.createExpression(expressionString);
        } catch (Exception e) {
            throw new RuntimeException("Could not parse: " + expressionString, e);
        }
    }

    private void assertExpressionTrue(Invocation invocation, String expressionString, String message) {
        if (expressionString != null) {
            Expression expression = parseExpression(expressionString);
            JexlContext jc = createContext(invocation);
            assertExpressionTrue(expression, jc, MessageFormat.format(message, new Object[] { expressionString }));
        }
    }

    private JexlContext createContext(Invocation invocation) {
        JexlContext jc = JexlHelper.createContext();
        jc.getVars().put("", invocation.getProxy());
        jc.getVars().put("this", invocation.getProxy());
        Object[] args = invocation.getArgs();
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                jc.getVars().put("$" + i, arg);
            }
        }
        return jc;
    }

    private void assertExpressionTrue(Expression expression, JexlContext jc, String message) {
        try {
            boolean result;
            Boolean aBoolean = (Boolean) executeExpression(expression, jc);
            result = aBoolean.booleanValue();
            if(!result) {
                throw new AssertionError(message);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not execute: " + expression, e);
        }
    }

    private Object executeExpression(Expression expression, JexlContext jc) throws Exception {
        checkContracts.set(checkContracts);
        try {
            return expression.evaluate(jc);
        } finally {
            checkContracts.set(null);
        }
    }
}
