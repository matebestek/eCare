/***
 * Copyright (c) 2013.
 * University of Primorska, Andrej Marušič Institute. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met: 
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer. 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution. 
 * 
 * Project eOskrba (http://eOskrba.si) was financed by Slovenian Research
 * Agency and Ministry of Health of Republic of Slovenia.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package si.pint.upr.eoskrba.proces.client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.SystemEventListenerFactory;
import org.jbpm.process.workitem.wsht.BlockingAddTaskResponseHandler;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.userprofile.User;

public class EOskrbaTaskClient {
	public static void main(String[] args) {
		TaskClient client = new TaskClient(new MinaTaskClientConnector(
				"eOskrba clienst", new MinaTaskClientHandler(
						SystemEventListenerFactory.getSystemEventListener())));
		client.connect("127.0.0.1", 9123);

		// adding a task

		BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();

		Task task = null;// TODO
		client.addTask(task, null, addTaskResponseHandler);
		long taskId = addTaskResponseHandler.getTaskId();

		// getting tasks for user "bobba"

		BlockingTaskSummaryResponseHandler taskSummaryResponseHandler =

		new BlockingTaskSummaryResponseHandler();

		client.getTasksAssignedAsPotentialOwner("bobba", "en-UK",
				taskSummaryResponseHandler);

		List<TaskSummary> tasks = taskSummaryResponseHandler.getResults();

		// starting a task

		BlockingTaskOperationResponseHandler responseHandler =

		new BlockingTaskOperationResponseHandler();

		client.start(taskId, "bobba", responseHandler);

		// completing a task

		responseHandler = new BlockingTaskOperationResponseHandler();
		Map<String,User> users = new HashMap<String,User>();
		client.complete(taskId, users.get("bobba").getId(), null,
				responseHandler);

	}
}
