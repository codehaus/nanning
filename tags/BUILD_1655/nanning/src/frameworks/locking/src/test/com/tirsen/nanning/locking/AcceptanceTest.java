package com.tirsen.nanning.locking;

import com.tirsen.nanning.attribute.AbstractAttributesTest;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.config.AttributePointcut;
import com.tirsen.nanning.config.FindTargetMixinAspect;

public class AcceptanceTest extends AbstractAttributesTest {
    public static interface Book {
        /**
         * @transaction
         */
        public void setTitle(String title);

        String getTitle();
    }

    public static class BookImpl implements Book {
        private String title;

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTitle() {
            return title;
        }
    }

    public void testPessimisticLocking() {
        AspectSystem aspectSystem = new AspectSystem();
        aspectSystem.addAspect(new FindTargetMixinAspect());
        aspectSystem.addAspect(new PessimisticLockingAspect(new AttributePointcut("transaction")));

        Book book = (Book) aspectSystem.newInstance(Book.class);
        book.setTitle("Crime and Punishment");
        assertEquals("Crime and Punishment", book.getTitle());

        ((Lockable) book).lock();
        try {
            book.setTitle("Remembrance of Things Past");
            fail();
        } catch (LockedException shouldHappen) {
        }
        assertEquals("Crime and Punishment", book.getTitle());
    }

}
