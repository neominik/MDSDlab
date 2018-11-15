/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package edu.mdsd.mil.resource.mil.launch;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.interpreter.MILInterpreter;

/**
 * A class that handles launch configurations.
 */
public class MilLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	/**
	 * The URI of the resource that shall be launched.
	 */
	public final static String ATTR_RESOURCE_URI = "uri";

	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		MilLaunchConfigurationHelper helper = new MilLaunchConfigurationHelper();
		MILModel model = (MILModel) helper.getModelRoot(configuration);
		new MILInterpreter().interpretAndPrintResult(model);
	}

}
