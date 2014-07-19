package org.ObjectLayout;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import org.junit.Test;

public class ReferenceArrayTest {

    public static class Stack extends ReferenceArray<Object> {
        private int size = 0;
        private final long capacity;
        
        public Stack() {
            this.capacity = getLength();
        }
        
        public void push(Object o) {
            if (size == capacity) {
                throw new IndexOutOfBoundsException();
            }
            
            set(size, o);
            size++;
        }
        
        public Object pop() {
            if (size == 0) {
                throw new IndexOutOfBoundsException();
            }
            
            Object o = get(--size);
            return o;
        }
        
        public int size() {
            return size;
        }
    }
    
    @Test
    public void pushesAndPops() throws Exception {
        Stack s = ReferenceArray.newSubclassInstance(Stack.class, 10);
        
        String foo = "foo";
        
        s.push(foo);
        
        assertThat(s.size(), is(1));
        assertThat(s.pop(), is((Object) foo));
        assertThat(s.size(), is(0));
    }
}