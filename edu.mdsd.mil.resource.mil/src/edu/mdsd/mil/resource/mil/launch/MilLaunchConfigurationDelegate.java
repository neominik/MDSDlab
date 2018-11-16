/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package edu.mdsd.mil.resource.mil.launch;

import java.io.PrintStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;

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
		IOConsole console = findConsole("Interpreter");
		PrintStream out = new PrintStream(console.newOutputStream());
		new MILInterpreter(out).interpretAndPrintResult(model);
		out.close();
	}

	private static IOConsole findConsole(String name) {
		ConsolePlugin plugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = plugin.getConsoleManager();
		IConsole[] existing = conMan.getConsoles();
		for (int i = 0; i < existing.length; i++)
			if (name.equals(existing[i].getName()))
				return (IOConsole) existing[i];
		// no console found, so create a new one
		IOConsole myConsole = new IOConsole(name, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		return myConsole;
	}
}
