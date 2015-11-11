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
package si.pint.upr.eoskrba.proces.server;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.AccessType;
import org.jbpm.task.AllowedToDelegate;
import org.jbpm.task.Attachment;
import org.jbpm.task.BooleanExpression;
import org.jbpm.task.Comment;
import org.jbpm.task.Deadline;
import org.jbpm.task.Deadlines;
import org.jbpm.task.Delegation;
import org.jbpm.task.Escalation;
import org.jbpm.task.Group;
import org.jbpm.task.I18NText;
import org.jbpm.task.Notification;
import org.jbpm.task.OrganizationalEntity;
import org.jbpm.task.PeopleAssignments;
import org.jbpm.task.Reassignment;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskData;
import org.jbpm.task.User;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.drools.SystemEventListenerFactory;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExpressionCompiler;

public class DemoTaskService {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("org.jbpm.task");
		TaskService taskService = new TaskService(emf,
				SystemEventListenerFactory.getSystemEventListener());
		TaskServiceSession taskSession = taskService.createSession();
		// Add users
		Map vars = new HashMap();
		Reader reader = new InputStreamReader(
				DemoTaskService.class.getResourceAsStream("LoadUsers.mvel"));
		Map<String, User> users = (Map<String, User>) eval(reader, vars);
		for (User user : users.values()) {
			taskSession.addUser(user);
		}
		reader = new InputStreamReader(
				DemoTaskService.class.getResourceAsStream("LoadGroups.mvel"));
		Map<String, Group> groups = (Map<String, Group>) eval(reader, vars);
		for (Group group : groups.values()) {
			taskSession.addGroup(group);
		}
		// start server
		MinaTaskServer server = new MinaTaskServer(taskService);
		Thread thread = new Thread(server);
		thread.start();
		taskSession.dispose();
		System.out.println("Task service started correctly !");
		System.out.println("Task service running ...");
	}

	public static Object eval(Reader reader, Map vars) {
		try {
			return eval(readerToString(reader), vars);
		} catch (IOException e) {
			throw new RuntimeException("Exception Thrown", e);
		}
	}

	public static String readerToString(Reader reader) throws IOException {
		int charValue = 0;
		StringBuffer sb = new StringBuffer(1024);
		while ((charValue = reader.read()) != -1) {
			// result = result + (char) charValue;
			sb.append((char) charValue);
		}
		return sb.toString();
	}

	public static Object eval(String str, Map vars) {
		ExpressionCompiler compiler = new ExpressionCompiler(str.trim());

		ParserContext context = new ParserContext();
		context.addPackageImport("org.jbpm.task");
		context.addPackageImport("java.util");

		context.addImport("AccessType", AccessType.class);
		context.addImport("AllowedToDelegate", AllowedToDelegate.class);
		context.addImport("Attachment", Attachment.class);
		context.addImport("BooleanExpression", BooleanExpression.class);
		context.addImport("Comment", Comment.class);
		context.addImport("Deadline", Deadline.class);
		context.addImport("Deadlines", Deadlines.class);
		context.addImport("Delegation", Delegation.class);
		context.addImport("Escalation", Escalation.class);
		context.addImport("Group", Group.class);
		context.addImport("I18NText", I18NText.class);
		context.addImport("Notification", Notification.class);
		context.addImport("OrganizationalEntity", OrganizationalEntity.class);
		context.addImport("PeopleAssignments", PeopleAssignments.class);
		context.addImport("Reassignment", Reassignment.class);
		context.addImport("Status", Status.class);
		context.addImport("Task", Task.class);
		context.addImport("TaskData", TaskData.class);
		context.addImport("TaskSummary", TaskSummary.class);
		context.addImport("User", User.class);

		return MVEL.executeExpression(compiler.compile(context), vars);
	}

}
