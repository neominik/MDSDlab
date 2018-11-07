/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package edu.mdsd.mpl.mpl.resource.mpl.analysis;

import java.util.Map;
import org.eclipse.emf.ecore.EReference;

public class OperationExpressionOperationReferenceResolver implements edu.mdsd.mpl.mpl.resource.mpl.IMplReferenceResolver<edu.mdsd.mpl.mpl.OperationExpression, edu.mdsd.mpl.mpl.Operation> {
	
	private edu.mdsd.mpl.mpl.resource.mpl.analysis.MplDefaultResolverDelegate<edu.mdsd.mpl.mpl.OperationExpression, edu.mdsd.mpl.mpl.Operation> delegate = new edu.mdsd.mpl.mpl.resource.mpl.analysis.MplDefaultResolverDelegate<edu.mdsd.mpl.mpl.OperationExpression, edu.mdsd.mpl.mpl.Operation>();
	
	public void resolve(String identifier, edu.mdsd.mpl.mpl.OperationExpression container, EReference reference, int position, boolean resolveFuzzy, final edu.mdsd.mpl.mpl.resource.mpl.IMplReferenceResolveResult<edu.mdsd.mpl.mpl.Operation> result) {
		delegate.resolve(identifier, container, reference, position, resolveFuzzy, result);
	}
	
	public String deResolve(edu.mdsd.mpl.mpl.Operation element, edu.mdsd.mpl.mpl.OperationExpression container, EReference reference) {
		return delegate.deResolve(element, container, reference);
	}
	
	public void setOptions(Map<?,?> options) {
		// save options in a field or leave method empty if this resolver does not depend
		// on any option
	}
	
}
