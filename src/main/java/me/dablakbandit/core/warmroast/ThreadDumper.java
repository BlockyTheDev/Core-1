/*
 * WarmRoast
 * Copyright (C) 2013 Albert Pham <http://www.sk89q.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.dablakbandit.core.warmroast;

import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;

/**
 * Uses the {@link ThreadMXBean} to generate {@link ThreadInfo} instances for the threads being
 * sampled.
 */
@FunctionalInterface
public interface ThreadDumper{
	
	/**
	 * Generates {@link ThreadInfo} data for the sampled threads.
	 *
	 * @param threadBean the thread bean instance to obtain the data from
	 * @return an array of generated thread info instances
	 */
	ThreadInfo[] dumpThreads(ThreadMXBean threadBean);
	
	/**
	 * Implementation of {@link ThreadDumper} that generates data for all threads.
	 */
	final class All implements ThreadDumper{
		@Override
		public ThreadInfo[] dumpThreads(ThreadMXBean threadBean){
			return threadBean.dumpAllThreads(false, false);
		}
	}
	
	/**
	 * Implementation of {@link ThreadDumper} that generates data for a specific set of threads.
	 */
	final class Specific implements ThreadDumper{
		private final long[] ids;
		
		public Specific(long[] ids){
			this.ids = ids;
		}
		
		public Specific(String name){
			this.ids = Thread.getAllStackTraces().keySet().stream().filter(t -> {
				return t.getName().equalsIgnoreCase(name);
			}).mapToLong(Thread::getId).toArray();
		}
		
		@Override
		public ThreadInfo[] dumpThreads(ThreadMXBean threadBean){
			return threadBean.getThreadInfo(this.ids, Integer.MAX_VALUE);
		}
	}
	
}
