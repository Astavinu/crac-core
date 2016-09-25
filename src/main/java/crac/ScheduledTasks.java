package crac;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import crac.sync.SyncManager;

@Configuration
/**
 * all regularly occurring tasks, such as the external db synchronization/import
 * task, go in here
 */
public class ScheduledTasks implements SchedulingConfigurer {

	@Bean(destroyMethod = "shutdown")
	public Executor taskExecutor() {
		return Executors.newScheduledThreadPool(2);
	}

	@Bean
	public SyncManager syncManager() {
		return new SyncManager();
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
		taskRegistrar.addTriggerTask(new Runnable() {
			@Override
			public void run() {
				// execute task
				syncManager().sync();
			}
		}, new Trigger() {
			@Override
			public Date nextExecutionTime(TriggerContext triggerContext) {
				Calendar nextExecutionTime = new GregorianCalendar();
				Date lastActualExecutionTime = triggerContext
						.lastActualExecutionTime();
				nextExecutionTime.setTime(lastActualExecutionTime != null
						? lastActualExecutionTime : new Date());

				// set next interval
				nextExecutionTime.add(Calendar.MILLISECOND,
						syncManager().getInterval());
				return nextExecutionTime.getTime();
			}
		});

		// example, the trigger allows live runtime task execution time changes
		// taskRegistrar.addTriggerTask(new Runnable() {
		// @Override
		// public void run() {
		// example().doSomething();
		// }
		// }, new Trigger() {
		// @Override
		// public Date nextExecutionTime(TriggerContext triggerContext) {
		// Calendar nextExecutionTime = new GregorianCalendar();
		// Date lastActualExecutionTime = triggerContext
		// .lastActualExecutionTime();
		// nextExecutionTime.setTime(lastActualExecutionTime != null
		// ? lastActualExecutionTime : new Date());
		//
		// nextExecutionTime.add(Calendar.MILLISECOND, 1000);
		// return nextExecutionTime.getTime();
		// }
		// });
	}
}
