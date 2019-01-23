/**
 * <copyright>
 * </copyright>
 *
 * 
 */
package edu.mdsd.mil.resource.mil.launch;

import java.io.InputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.emf.common.util.URI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;

import edu.mdsd.mil.MILModel;
import edu.mdsd.mil.interpreter.MILInterpreter;
import edu.mdsd.symbolic.Symbolic;

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

		switch (mode) {
		case "debug":
			debug(helper, configuration, out);
			break;
		default: // run
			InputStream in = console.getInputStream();
			new MILInterpreter(out, in).interpretAndPrintResult(model);
		}

		out.close();
		monitor.done();
	}

	private static void debug(MilLaunchConfigurationHelper helper, ILaunchConfiguration configuration,
			PrintStream out) {
		try {
			URI uri = helper.getURI(configuration);
			String file = new URL(uri.toString()).toString();
			@SuppressWarnings("unchecked")
			Map<Integer, List<String>> errors = Symbolic.verify(file);
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true)));
			resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			errors.forEach(createMarkers(resource));
			out.println(errors);
		} catch (MalformedURLException | CoreException e) {// ignore
		}
	}

	private static BiConsumer<Integer, List<String>> createMarkers(IResource resource) {
		return (line, messages) -> {
			messages.forEach(message -> {
				IMarker m;
				try {
					m = resource.createMarker(IMarker.PROBLEM);
					m.setAttribute(IMarker.LINE_NUMBER, line);
					m.setAttribute(IMarker.MESSAGE, message);
					m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
					m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
				} catch (CoreException e) {// ignore
				}
			});
		};
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
