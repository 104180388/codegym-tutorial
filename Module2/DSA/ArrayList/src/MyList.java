import java.util.Arrays;

public class MyList<E> {
    private int size = 0;
    private static final int DEFAULT_CAPACITY = 10;
    private Object[] elements;

    public MyList() {
        elements = new Object[DEFAULT_CAPACITY];
    }

    public MyList(int capacity) {
        if (capacity > 0) {
            elements = new Object[capacity];
        } else {
            throw new IllegalArgumentException("Capacity must be greater than 0");
        }
    }

    public boolean add(E e) {
        ensureCapacity(size + 1);
        elements[size++] = e;
        return true;
    }

    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        ensureCapacity(size + 1);

        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = element;
        size++;
    }

    public E remove(int index) {
        checkIndex(index);
        E oldValue = (E) elements[index];

        int numMoved = size - index - 1;
        if (numMoved > 0) {
            System.arraycopy(elements, index + 1, elements, index, numMoved);
        }
        elements[--size] = null; // Tránh rò rỉ bộ nhớ
        return oldValue;
    }

    public int size() {
        return size;
    }

    public MyList<E> clone() {
        try {
            MyList<E> v = (MyList<E>) super.clone();
            v.elements = Arrays.copyOf(elements, size);
            return v;
        } catch (CloneNotSupportedException e) {
            // Vì Object.clone() ném ngoại lệ này, nhưng MyList không implement Cloneable
            // Cách thủ công:
            MyList<E> cloneList = new MyList<>(elements.length);
            cloneList.elements = Arrays.copyOf(elements, size);
            cloneList.size = size;
            return cloneList;
        }
    }

    public boolean contains(E o) {
        return indexOf(o) >= 0;
    }

    public int indexOf(E o) {
        if (o == null) {
            for (int i = 0; i < size; i++)
                if (elements[i] == null) return i;
        } else {
            for (int i = 0; i < size; i++)
                if (o.equals(elements[i])) return i;
        }
        return -1;
    }

    public void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newSize = elements.length * 2;
            if (newSize < minCapacity) newSize = minCapacity;
            elements = Arrays.copyOf(elements, newSize);
        }
    }

    public E get(int i) {
        checkIndex(i);
        return (E) elements[i];
    }

    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    private void checkIndex(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
    }
}