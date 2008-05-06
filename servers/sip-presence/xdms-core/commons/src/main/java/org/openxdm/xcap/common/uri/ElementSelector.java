package org.openxdm.xcap.common.uri;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * 
 * A element selector points to an element in a document resource.
 * 
 * It's defined in the XCAP protocol specs by the regular expression
 * element-selector = step *( "/" step)
 * 
 * Usage Examples:
 * 
 * 1) Select 'list' element, with name 'friends', being child of the root document element 'resource-lists'.
 * 
 * LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
 * ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
 * ElementSelectorStep step2 = new ElementSelectorStepByAttr("list","name","friends");
 * elementSelectorSteps.add(step1);
 * elementSelectorSteps.addLast(step2);
 * ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
 * 
 * 2) Select first 'list' element, being child of the root document element 'resource-lists'.
 * 
 * LinkedList<ElementSelectorStep> elementSelectorSteps = new LinkedList<ElementSelectorStep>();
 * ElementSelectorStep step1 = new ElementSelectorStep("resource-lists");
 * ElementSelectorStep step2 = new ElementSelectorStepByPos("list",1);
 * elementSelectorSteps.add(step1);
 * elementSelectorSteps.addLast(step2);
 * ElementSelector elementSelector = new ElementSelector(elementSelectorSteps);
 * 
 * @author Eduardo Martins
 *
 */
public class ElementSelector {
		
	private LinkedList<ElementSelectorStep> steps;
	
	/**
	 * Creates a new instance of an element selector from the specified linked list of steps.
	 * @param elementSelectorSteps the linked list with the element selector steps.
	 */
	public ElementSelector(LinkedList<ElementSelectorStep> elementSelectorSteps) {		
		this.steps = elementSelectorSteps;
	}
	
	/**
	 * Retreives a element selector step by position.
	 * @param index the index of the step to retreive.
	 * @return
	 * @throws IndexOutOfBoundsException thrown when the index is invalid. 
	 */
	public ElementSelectorStep getStep(int index) throws IndexOutOfBoundsException {
		return steps.get(index);		
	}
		
	/**
	 * Retrieves the last step of the element selector.
	 * @return
	 */
	public ElementSelectorStep getLastStep() {
		return steps.getLast();
	}	
	
	/**
	 * Retrieves the number of steps in this element selector.
	 * @return
	 */
	public int getStepsSize() {
		return steps.size();
	}
	
	/**
	 * Checks if the element selector has steps with unbinded namespaces prefixes, considering the provided map of namespace bindings.
	 * @param namespaceBindings the namespace bindings map.
	 * @return
	 */
	public boolean hasUnbindedPrefixes(Map<String,String> namespaceBindings) {
		for(Iterator<ElementSelectorStep> i=steps.iterator();i.hasNext();) {
			ElementSelectorStep step = i.next();			
			String prefix = step.getPrefix();
			if (!namespaceBindings.containsKey(prefix)) {
				return false;
			}			
		}
		return true;
	}
	
	public String toString() {
		if (toString == null) {
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<getStepsSize();i++){
				ElementSelectorStep step = getStep(i);									
				if (step instanceof ElementSelectorStepByPosAttr) {
					sb = appendElementSelectorStepByPosAttr(sb,(ElementSelectorStepByPosAttr)step);
				}
				else if (step instanceof ElementSelectorStepByAttr) {
					sb = appendElementSelectorStepByAttr(sb,(ElementSelectorStepByAttr)step);
				}
				else if (step instanceof ElementSelectorStepByPos) {
					sb = appendElementSelectorStepByPos(sb,(ElementSelectorStepByPos)step);
				}
				else {
					sb = appendElementSelectorStep(sb,step);
				}
			}
			toString = sb.toString();
		}
		return toString;
	}
	
	private String toString = null;
	
	private static StringBuilder appendElementSelectorStepByAttr(StringBuilder sb, ElementSelectorStepByAttr ess) {
		return sb.append("/").append(ess.getName()).append("[@").append(ess.getAttrName()).append("='").append(ess.getAttrValue()).append("']");
	}
	
	private static StringBuilder appendElementSelectorStep(StringBuilder sb, ElementSelectorStep e) {
		return sb.append("/").append(e.getName());
	}
	
	private static StringBuilder appendElementSelectorStepByPos(StringBuilder sb, ElementSelectorStepByPos e) {
		return sb.append("/").append(e.getName()).append('[').append(e.getPos()).append(']');
	}
	
	private static StringBuilder appendElementSelectorStepByPosAttr(StringBuilder sb, ElementSelectorStepByPosAttr e) {
		return sb.append("/").append(e.getName()).append('[').append(e.getPos()).append("][@").append(e.getAttrName()).append("='").append(e.getAttrValue()).append("']");
	}
}
