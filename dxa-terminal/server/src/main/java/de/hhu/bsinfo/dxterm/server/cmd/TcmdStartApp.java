/*
 * Copyright (C) 2018 Heinrich-Heine-Universitaet Duesseldorf, Institute of Computer Science,
 * Department Operating Systems
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package de.hhu.bsinfo.dxterm.server.cmd;

import de.hhu.bsinfo.dxram.app.ApplicationService;
import de.hhu.bsinfo.dxterm.TerminalCommandString;
import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Starts an application.
 *
 * @author Filip Krakowski, Filip.Krakowski@Uni-Duesseldorf.de, 22.08.2018
 */
public class TcmdStartApp extends AbstractTerminalCommand {
    public TcmdStartApp() {
        super("startapp");
    }

    private static final String ARG_NAME = "name";

    @Override
    public String getHelp() {
        return "Starts an application\n" + "Usage: startapp <name> [args]\n" +
                "  name: The application's name";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout, final TerminalServerStdin p_stdin,
                     final TerminalServiceAccessor p_services) {
        String name = p_cmd.getNamedArgument(ARG_NAME);

        if (name.isEmpty()) {
            p_stdout.printlnErr("No application name specified");
            return;
        }

        ApplicationService appService = p_services.getService(ApplicationService.class);

        int argCount = p_cmd.getArgc();

        String args[] = null;
        if (argCount > 1) {
            args = Arrays.copyOfRange(p_cmd.getArgs(), 1, argCount);
        }

        if (!appService.startApplication(name, args)) {
            p_stdout.printlnErr("The application could not be started");
        }
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        return Collections.emptyList();
    }
}