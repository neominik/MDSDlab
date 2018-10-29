/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package edu.mdsd.mpl.mpl.resource.mpl.analysis;

import java.util.Map;
import org.eclipse.emf.ecore.EReference;

public class VariableReferenceVariableReferenceResolver implements edu.mdsd.mpl.mpl.resource.mpl.IMplReferenceResolver<edu.mdsd.mpl.mpl.VariableReference, edu.mdsd.mpl.mpl.Variable> {
	
	private edu.mdsd.mpl.mpl.resource.mpl.analysis.MplDefaultResolverDelegate<edu.mdsd.mpl.mpl.VariableReference, edu.mdsd.mpl.mpl.Variable> delegate = new edu.mdsd.mpl.mpl.resource.mpl.analysis.MplDefaultResolverDelegate<edu.mdsd.mpl.mpl.VariableReference, edu.mdsd.mpl.mpl.Variable>();
	
	public void resolve(String identifier, edu.mdsd.mpl.mpl.VariableReference container, EReference reference, int position, boolean resolveFuzzy, final edu.mdsd.mpl.mpl.resource.mpl.IMplReferenceResolveResult<edu.mdsd.mpl.mpl.Variable> result) {
		delegate.resolve(identifier, container, reference, position, resolveFuzzy, result);
	}
	
	public String deResolve(edu.mdsd.mpl.mpl.Variable element, edu.mdsd.mpl.mpl.VariableReference container, EReference reference) {
		return delegate.deResolve(element, container, reference);
	}
	
	public void setOptions(Map<?,?> options) {
		// save options in a field or leave method empty if this resolver does not depend
		// on any option
	}
	
}
