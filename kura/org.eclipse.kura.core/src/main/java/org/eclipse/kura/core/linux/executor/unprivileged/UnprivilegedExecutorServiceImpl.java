/*******************************************************************************
 * Copyright (c) 2019, 2021 Eurotech and/or its affiliates and others
 * 
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 *  Eurotech
 *******************************************************************************/
package org.eclipse.kura.core.linux.executor.unprivileged;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.kura.annotation.ServicePid;
import org.eclipse.kura.core.internal.linux.executor.ExecutorUtil;
import org.eclipse.kura.core.linux.executor.LinuxExitStatus;
import org.eclipse.kura.core.linux.executor.LinuxSignal;
import org.eclipse.kura.executor.Command;
import org.eclipse.kura.executor.CommandStatus;
import org.eclipse.kura.executor.Pid;
import org.eclipse.kura.executor.Signal;
import org.eclipse.kura.executor.UnprivilegedExecutorService;
import org.eclipse.kura.system.SystemService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServicePid("org.eclipse.kura.executor.UnprivilegedExecutorService")
@Component(service = UnprivilegedExecutorService.class, immediate = true, name = "org.eclipse.kura.executor.UnprivilegedExecutorService")
public class UnprivilegedExecutorServiceImpl implements UnprivilegedExecutorService {

    private static final Logger logger = LoggerFactory.getLogger(UnprivilegedExecutorServiceImpl.class);
    private static final LinuxSignal DEFAULT_SIGNAL = LinuxSignal.SIGTERM;
    private SystemService systemService;
    private ExecutorUtil executorUtil;

    @Reference(cardinality = ReferenceCardinality.MANDATORY, policy = ReferencePolicy.STATIC)
    public void setSystemService(SystemService systemService) {
        this.systemService = systemService;
    }

    public void unsetSystemService(SystemService systemService) {
        if (this.systemService == systemService) {
            this.systemService = null;
        }
    }

    @Activate
    protected void activate() {
        logger.info("activate...");

        String user = this.systemService.getCommandUser();
        if (user == null || user.equals("unknown")) {
            this.executorUtil = new ExecutorUtil();
        } else {
            this.executorUtil = new ExecutorUtil(user);
        }
    }

    @Deactivate
    protected void deactivate() {
        logger.info("deactivate...");
    }

    @Override
    public CommandStatus execute(Command command) {
        if (command.getCommandLine() == null || command.getCommandLine().length == 0) {
            return buildErrorStatus(command);
        }
        if (command.getSignal() == null) {
            command.setSignal(DEFAULT_SIGNAL);
        }
        return this.executorUtil.executeUnprivileged(command);
    }

    @Override
    public void execute(Command command, Consumer<CommandStatus> callback) {
        if (command.getCommandLine() == null || command.getCommandLine().length == 0) {
            callback.accept(buildErrorStatus(command));
            return;
        }
        if (command.getSignal() == null) {
            command.setSignal(DEFAULT_SIGNAL);
        }
        this.executorUtil.executeUnprivileged(command, callback);
    }

    @Override
    public boolean stop(Pid pid, Signal signal) {
        boolean isStopped = false;
        if (signal == null) {
            isStopped = this.executorUtil.stopUnprivileged(pid, DEFAULT_SIGNAL);
        } else {
            isStopped = this.executorUtil.stopUnprivileged(pid, signal);
        }
        return isStopped;
    }

    @Override
    public boolean kill(String[] commandLine, Signal signal) {
        boolean isKilled = false;
        if (signal == null) {
            isKilled = this.executorUtil.killUnprivileged(commandLine, DEFAULT_SIGNAL);
        } else {
            isKilled = this.executorUtil.killUnprivileged(commandLine, signal);
        }
        return isKilled;
    }

    @Override
    public boolean isRunning(Pid pid) {
        return this.executorUtil.isRunning(pid);
    }

    @Override
    public boolean isRunning(String[] commandLine) {
        return this.executorUtil.isRunning(commandLine);
    }

    @Override
    public Map<String, Pid> getPids(String[] commandLine) {
        return this.executorUtil.getPids(commandLine);
    }

    private CommandStatus buildErrorStatus(Command command) {
        CommandStatus status = new CommandStatus(command, new LinuxExitStatus(1));
        ByteArrayOutputStream err = new ByteArrayOutputStream();
        try {
            err.write("The commandLine cannot be empty or not defined".getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            logger.error("Cannot write to error stream", e);
        }
        status.setErrorStream(err);
        return status;
    }

}
