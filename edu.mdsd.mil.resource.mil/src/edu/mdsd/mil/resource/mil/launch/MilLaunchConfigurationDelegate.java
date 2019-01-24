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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

import clojure.java.api.Clojure;
import clojure.lang.Keyword;
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
		launch.terminate();
	}

	private static void debug(MilLaunchConfigurationHelper helper, ILaunchConfiguration configuration,
			PrintStream out) {
		try {
			URI uri = helper.getURI(configuration);
			IResource resource = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(uri.toPlatformString(true)));
			resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);

			String file = new URL(uri.toString()).toString();
			@SuppressWarnings("unchecked")
			List<Map<Keyword, ?>> errors = Symbolic.verify(file);

			errors.forEach(error -> {
				createMarker(resource, extractMessage(error), extractLineNumber(error));
				out.println(error);
			});
		} catch (MalformedURLException | CoreException e) {// ignore
		}
	}

	@SuppressWarnings("unchecked")
	private static String extractMessage(Map<Keyword, ?> error) {
		String message = (String) error.get(Clojure.read(":message"));
		Map<Keyword, ?> model = new HashMap<>((Map<Keyword, ?>) error.get(Clojure.read(":model")));
		message = message.replaceAll("\n", "") + " when " + model;
		return message;
	}

	@SuppressWarnings("unchecked")
	private static int extractLineNumber(Map<Keyword, ?> error) {
		Map<Keyword, ?> state = (Map<Keyword, ?>) error.get(Clojure.read(":state"));
		int line = (int) state.get(Clojure.read(":pc")) + 1;
		return line;
	}

	private static void createMarker(IResource resource, String message, int line) {
		try {
			IMarker m;
			m = resource.createMarker(IMarker.PROBLEM);
			m.setAttribute(IMarker.LINE_NUMBER, line);
			m.setAttribute(IMarker.MESSAGE, message);
			m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_HIGH);
			m.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
		} catch (CoreException e) {// ignore
		}
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
