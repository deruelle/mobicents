package org.openxdm.xcap.common.uri;

/**
 * 
 * terminal-selector = attribute-selector | namespace-selector | extension-selector
 * attribute-selector = "@" att-name
 * namespace-selector = "namespace::*"
 * extension-selector = 1*( %x00-2e / %x30-ff )
 * 
 * @author Eduardo Martins
 *
 */
public interface TerminalSelector {

}
