package utils;

import java.util.RandomAccess;

;

public class FloatArrayList implements RandomAccess, Cloneable, java.io.Serializable
{
	protected transient int modCount = 0;

	private static final long serialVersionUID = 1L;

	private transient float[] elementData;

	private int size;

	public FloatArrayList(int initialCapacity)
	{
		super();
		if( initialCapacity < 0 )
		{
			throw new IllegalArgumentException("Illegal Capacity: " + initialCapacity);
		}
		this.elementData = new float[initialCapacity];
	}

	public FloatArrayList()
	{
		this(10);
	}

	public void trimToSize()
	{
		modCount++;
		int oldCapacity = elementData.length;
		if( size < oldCapacity )
		{
			float oldData[] = elementData;
			elementData = new float[size];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	public void ensureCapacity(int minCapacity)
	{
		modCount++;
		int oldCapacity = elementData.length;
		if( minCapacity > oldCapacity )
		{
			float oldData[] = elementData;
			int newCapacity = (oldCapacity * 3) / 2 + 1;
			if( newCapacity < minCapacity )
			{
				newCapacity = minCapacity;
			}
			elementData = new float[newCapacity];
			System.arraycopy(oldData, 0, elementData, 0, size);
		}
	}

	public int size()
	{
		return size;
	}

	public boolean isEmpty()
	{
		return size == 0;
	}

	public boolean contains(Object elem)
	{
		return indexOf(elem) >= 0;
	}

	public int indexOf(Object elem)
	{
		return -1;
	}

	public int lastIndexOf(Object elem)
	{
		return -1;
	}

	public Object clone()
	{
		try
		{
			FloatArrayList v = (FloatArrayList) super.clone();
			v.elementData = new float[size];
			System.arraycopy(elementData, 0, v.elementData, 0, size);
			v.modCount = 0;
			return v;
		}
		catch( CloneNotSupportedException e )
		{
			// this shouldn't happen, since we are Cloneable
			throw new InternalError();
		}
	}

	public Object[] toArray()
	{
		Object[] result = new Object[size];
		System.arraycopy(elementData, 0, result, 0, size);
		return result;
	}

	public float[] getFloatArray()
	{
		trimToSize();
		return elementData;
	}

	public float get(int index)
	{
		RangeCheck(index);
		return elementData[index];
	}

	public void set(int index, float element)
	{
		RangeCheck(index);
		elementData[index] = element;
	}

	public boolean add(float o)
	{
		ensureCapacity(size + 1); // Increments modCount!!
		elementData[size++] = o;
		return true;
	}

	public boolean add(float[] p)
	{
		ensureCapacity(size + p.length);
		for (int i = 0; i < p.length; i++)
		{
			elementData[size + i] = p[i];
		}
		size += p.length;
		return true;
	}

	public void add(int index, float element)
	{
		if( index > size || index < 0 )
		{
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}

		ensureCapacity(size + 1); // Increments modCount!!
		System.arraycopy(elementData, index, elementData, index + 1, size - index);
		elementData[index] = element;
		size++;
	}

	public Float remove(int index)
	{
		RangeCheck(index);

		modCount++;
		float oldValue = elementData[index];

		int numMoved = size - index - 1;
		if( numMoved > 0 )
		{
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		}
		elementData[--size] = Float.NaN; // Let gc do its work

		return new Float(oldValue);
	}

	public boolean remove(Object o)
	{
		if( o == null )
		{
			for (int index = 0; index < size; index++)
			{
				if( elementData[index] == Float.NaN )
				{
					fastRemove(index);
					return true;
				}
			}
		}
		else
		{
			for (int index = 0; index < size; index++)
			{
				if( o.equals(elementData[index]) )
				{
					fastRemove(index);
					return true;
				}
			}
		}
		return false;
	}

	private void fastRemove(int index)
	{
		modCount++;
		int numMoved = size - index - 1;
		if( numMoved > 0 )
		{
			System.arraycopy(elementData, index + 1, elementData, index, numMoved);
		}
		elementData[--size] = Float.NaN; // Let gc do its work
	}

	public void clear()
	{
		modCount++;

		// Let gc do its work
		for (int i = 0; i < size; i++)
		{
			elementData[i] = Float.NaN;
		}

		size = 0;
	}

	protected void removeRange(int fromIndex, int toIndex)
	{
		modCount++;
		int numMoved = size - toIndex;
		System.arraycopy(elementData, toIndex, elementData, fromIndex, numMoved);

		// Let gc do its work
		int newSize = size - (toIndex - fromIndex);
		while( size != newSize )
		{
			elementData[--size] = Float.NaN;
		}
	}

	private void RangeCheck(int index)
	{
		if( index >= size )
		{
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
		}
	}

}
