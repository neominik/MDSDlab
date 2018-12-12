/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package edu.mdsd.mpl.mpl.resource.mpl.mopp;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.util.URI;

import edu.mdsd.mpl.compiler.MPL2MILCompiler;

public class MplBuilder implements edu.mdsd.mpl.mpl.resource.mpl.IMplBuilder {
	private MPL2MILCompiler compiler = new MPL2MILCompiler();

	public boolean isBuildingNeeded(URI uri) {
		// Change this to return true to enable building of all resources.
		return true;
	}

	public IStatus build(edu.mdsd.mpl.mpl.resource.mpl.mopp.MplResource resource, IProgressMonitor monitor) {
		compiler.compileAndSave	(resource);
		return Status.OK_STATUS;
	}

	/**
	 * Handles the deletion of the given resource.
	 */
	public IStatus handleDeletion(URI uri, IProgressMonitor monitor) {
		// by default nothing is done when a resource is deleted
		return Status.OK_STATUS;
	}

}
