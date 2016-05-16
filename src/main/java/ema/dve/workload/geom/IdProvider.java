package ema.dve.workload.geom;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class IdProvider 
{
	private Stack<Integer> stack;
	private int COUNTER;
	private int prefix;	
	
	public IdProvider(int prefix)
	{
		this.stack = new Stack<Integer>();
		this.COUNTER = 0;
		this.prefix = prefix;
	}
	
	public List<Integer> add(int amount)
	{
		List<Integer> added = new ArrayList<Integer>();
		
		for (int i=0; i<amount; i++)
		{
			COUNTER ++;
			int id = Integer.parseInt(prefix + "" + COUNTER + "");
			added.add(stack.push(id));
		}
		
		return added;
	}
	
	public List<Integer> remove(int amount)
	{
		List<Integer> removed = new ArrayList<Integer>();
		
		for (int i=0; i<amount; i++)
		{
			if (COUNTER <= 0)
			{
				return removed;
			}
			removed.add(stack.pop());
			COUNTER --;
		}
		
		return removed;
	}
	
	public Integer addOne()
	{
		return add(1).get(0);
	}
	
	public int getSize()
	{
		return stack.size();
	}

	public Set<Integer> getCurrent()
	{
		Integer[] array = new Integer[stack.size()];
		array = stack.toArray(array);
		Set<Integer> set = new HashSet<Integer>();
		set.addAll(Arrays.asList(array));
		return set;
	}
}
