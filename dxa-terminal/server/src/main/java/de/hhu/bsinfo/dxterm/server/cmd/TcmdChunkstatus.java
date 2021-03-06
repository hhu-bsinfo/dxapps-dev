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

import de.hhu.bsinfo.dxram.chunk.ChunkService;
import de.hhu.bsinfo.dxram.chunk.data.ChunkServiceStatus;
import de.hhu.bsinfo.dxterm.*;

import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;
import de.hhu.bsinfo.dxutils.NodeID;

import java.util.Collections;
import java.util.List;

/**
 * Get the status of the chunk service/memory from a remote node
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 03.04.2017
 */
public class TcmdChunkstatus extends AbstractTerminalCommand {
    public TcmdChunkstatus() {
        super("chunkstatus");
    }

    @Override
    public String getHelp() {
        return "Get the status of the chunk service/memory from a remote node\n" + "Usage: chunkstatus <nid>\n" +
                "  nid: Node ID of the remote peer to get the status from";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout, final TerminalServerStdin p_stdin,
                     final TerminalServiceAccessor p_services) {
        short nid = p_cmd.getArgument(0, NodeID::parse, NodeID.INVALID_ID);
        boolean migrated = p_cmd.getArgument(1, Boolean::valueOf, false);

        if (nid == NodeID.INVALID_ID) {
            p_stdout.printlnErr("No nid specified");
            return;
        }

        ChunkService chunk = p_services.getService(ChunkService.class);

        ChunkServiceStatus status = chunk.status().getStatus(nid);

        if (status == null) {
            p_stdout.printlnErr("Getting status failed");
            return;
        }

        p_stdout.println(status.toString());
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        switch (p_argumentPos) {
            case 0:
                return TcmdUtils.getAllOnlinePeerNodeIDsCompSuggestions(p_services);
            default:
                return Collections.emptyList();
        }
    }
}
