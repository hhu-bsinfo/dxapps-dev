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

import de.hhu.bsinfo.dxmem.data.ChunkID;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierID;
import de.hhu.bsinfo.dxram.lookup.overlay.storage.BarrierStatus;
import de.hhu.bsinfo.dxram.sync.SynchronizationService;
import de.hhu.bsinfo.dxterm.*;
import de.hhu.bsinfo.dxterm.server.AbstractTerminalCommand;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdin;
import de.hhu.bsinfo.dxterm.server.TerminalServerStdout;
import de.hhu.bsinfo.dxterm.server.TerminalServiceAccessor;
import de.hhu.bsinfo.dxutils.NodeID;

import java.util.Collections;
import java.util.List;

/**
 * Get the current status of a barrier
 *
 * @author Stefan Nothaas, stefan.nothaas@hhu.de, 03.04.2017
 */
public class TcmdBarrierstatus extends AbstractTerminalCommand {
    public TcmdBarrierstatus() {
        super("barrierstatus");
    }

    @Override
    public String getHelp() {
        return "Get the current status of a barrier\n" + "Usage: barrierstatus <bid>\n" + "  bid: Id of an allocated barrier to get the status of";
    }

    @Override
    public void exec(final TerminalCommandString p_cmd, final TerminalServerStdout p_stdout, final TerminalServerStdin p_stdin,
                     final TerminalServiceAccessor p_services) {
        int bid = p_cmd.getArgument(0, Integer::valueOf, BarrierID.INVALID_ID);

        if (bid == BarrierID.INVALID_ID) {
            p_stdout.printlnErr("No bid specified");
            return;
        }

        SynchronizationService sync = p_services.getService(SynchronizationService.class);

        BarrierStatus status = sync.barrierGetStatus(bid);

        if (status == null) {
            p_stdout.printflnErr("Getting status of barrier 0x%X failed", bid);
            return;
        }

        String peers = "";
        for (int i = 1; i < status.getSignedOnNodeIDs().length; i++) {
            peers += '\n' + NodeID.toHexString(status.getSignedOnNodeIDs()[i]) + ": " + ChunkID.toHexString(status.getCustomData()[i]);
        }

        p_stdout.printfln("Barrier status 0x%X, %d/%d: %s", bid, status.getNumberOfSignedOnPeers(), status.getSignedOnNodeIDs().length, peers);
    }

    @Override
    public List<String> getArgumentCompletionSuggestions(final int p_argumentPos, final TerminalCommandString p_cmdStr,
            final TerminalServiceAccessor p_services) {
        return Collections.emptyList();
    }
}
