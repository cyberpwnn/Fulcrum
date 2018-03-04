package com.volmit.fulcrum.bukkit;

import com.volmit.fulcrum.Fulcrum;

public abstract class Task implements ITask, ICancellable
{
	private int id;
	private String name;
	private boolean repeating;
	private double computeTime;
	private double totalComputeTime;
	private double activeTime;
	private boolean completed;
	protected int ticks;

	public Task(String name)
	{
		setup(name, false);

		id = Fulcrum.instance.startTask(0, new Runnable()
		{
			@Override
			public void run()
			{
				Task.this.run();
				completed = true;
			}
		});
	}

	public Task(String name, int interval)
	{
		setup(name, true);

		id = Fulcrum.instance.startRepeatingTask(0, interval, new Runnable()
		{
			@Override
			public void run()
			{
				Task.this.run();
				ticks++;
			}
		});
	}

	public Task(int interval)
	{
		setup("", true);

		id = Fulcrum.instance.startRepeatingTask(0, interval, new Runnable()
		{
			@Override
			public void run()
			{
				Task.this.run();
				ticks++;
			}
		});
	}

	public Task(String name, int interval, int total)
	{
		setup(name, true);

		id = Fulcrum.instance.startRepeatingTask(0, interval, new Runnable()
		{
			@Override
			public void run()
			{
				Task.this.run();
				ticks++;

				if(ticks >= total)
				{
					cancel();
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
