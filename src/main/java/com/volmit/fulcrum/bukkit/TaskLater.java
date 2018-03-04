package com.volmit.fulcrum.bukkit;

import com.volmit.fulcrum.Fulcrum;

public abstract class TaskLater implements ITask, ICancellable
{
	private int id;
	private String name;
	private boolean repeating;
	private double computeTime;
	private double totalComputeTime;
	private double activeTime;
	private boolean completed;
	protected int ticks;

	public TaskLater(String name)
	{
		this(name, 0);
	}

	public TaskLater(String name, int delay)
	{
		setup(name, true);

		id = Fulcrum.instance.startTask(delay, new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					TaskLater.this.run();
					ticks++;
				}

				catch(Exception e)
				{
				}
			}
		});
	}

	private void setup(String n, boolean r)
	{
		repeating = r;
		name = n;
		completed = false;
		computeTime = 0;
		activeTime = 0;
		totalComputeTime = 0;
		ticks = 0;
	}

	@Override
	public void cancel()
	{
		Fulcrum.instance.stopTask(id);
		completed = true;
	}

	@Override
	public int getId()
	{
		return id;
	}

	@Override
	public boolean isRepeating()
	{
		return repeating;
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public double getComputeTime()
	{
		return computeTime;
	}

	@Override
	public boolean hasCompleted()
	{
		return completed;
	}

	@Override
	public double getTotalComputeTime()
	{
		return totalComputeTime;
	}

	@Override
	public double getActiveTime()
	{
		return activeTime;
	}
}
