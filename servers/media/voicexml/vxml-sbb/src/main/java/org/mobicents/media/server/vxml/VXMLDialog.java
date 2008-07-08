/*
 * Mobicents Media Gateway
 *
 * The source code contained in this file is in in the public domain.
 * It can be used in any project or product without prior permission,
 * license or royalty payments. There is  NO WARRANTY OF ANY KIND,
 * EXPRESS, IMPLIED OR STATUTORY, INCLUDING, WITHOUT LIMITATION,
 * THE IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE,
 * AND DATA ACCURACY.  We do not warrant or make any representations
 * regarding the use of the software or the  results thereof, including
 * but not limited to the correctness, accuracy, reliability or
 * usefulness of the software.
 */

package org.mobicents.media.server.vxml;

/**
 * Represents the Voice XML dialog.
 * 
 * There are two kinds of dialogs: forms and menus. Forms define an interaction 
 * that collects values for a set of form item variables. Each field may specify 
 * a grammar that defines the allowable inputs for that field. If a form-level 
 * grammar is present, it can be used to fill several fields from one utterance. 
 * A menu presents the user with a choice of options and then transitions to 
 * another dialog based on that choice.
 *
 *
 * @author Oleg Kulikov
 */
public interface VXMLDialog extends VXMLItem {
}
