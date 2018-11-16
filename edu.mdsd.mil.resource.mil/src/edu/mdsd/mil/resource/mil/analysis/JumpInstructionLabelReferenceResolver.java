/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package edu.mdsd.mil.resource.mil.analysis;

import java.util.Map;
import org.eclipse.emf.ecore.EReference;

public class JumpInstructionLabelReferenceResolver implements edu.mdsd.mil.resource.mil.IMilReferenceResolver<edu.mdsd.mil.JumpInstruction, edu.mdsd.mil.LabelInstruction> {
	
	private edu.mdsd.mil.resource.mil.analysis.MilDefaultResolverDelegate<edu.mdsd.mil.JumpInstruction, edu.mdsd.mil.LabelInstruction> delegate = new edu.mdsd.mil.resource.mil.analysis.MilDefaultResolverDelegate<edu.mdsd.mil.JumpInstruction, edu.mdsd.mil.LabelInstruction>();
	
	public void resolve(String identifier, edu.mdsd.mil.JumpInstruction container, EReference reference, int position, boolean resolveFuzzy, final edu.mdsd.mil.resource.mil.IMilReferenceResolveResult<edu.mdsd.mil.LabelInstruction> result) {
		delegate.resolve(identifier, container, reference, position, resolveFuzzy, result);
	}
	
	public String deResolve(edu.mdsd.mil.LabelInstruction element, edu.mdsd.mil.JumpInstruction container, EReference reference) {
		return delegate.deResolve(element, container, reference);
	}
	
	public void setOptions(Map<?,?> options) {
		// save options in a field or leave method empty if this resolver does not depend
		// on any option
	}
	
}
