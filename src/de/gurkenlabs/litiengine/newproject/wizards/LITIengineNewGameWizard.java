package de.gurkenlabs.litiengine.newproject.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.core.runtime.*;
import org.eclipse.jface.operation.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;

import java.io.*;
import org.eclipse.ui.*;
import org.eclipse.ui.ide.IDE;

public class LITIengineNewGameWizard extends Wizard implements INewWizard {
  private LITIengineNewGameWizardPage page;
  private ISelection selection;

  /**
   * Constructor for LITIengineNewGameWizard.
   */
  public LITIengineNewGameWizard() {
    super();
    setNeedsProgressMonitor(true);
  }

  /**
   * Adding the page to the wizard.
   */

  public void addPages() {
    page = new LITIengineNewGameWizardPage(selection);
    addPage(page);
  }

  /**
   * This method is called when 'Finish' button is pressed in
   * the wizard. We will create an operation and run it
   * using wizard as execution context.
   */
  public boolean performFinish() {
    final String projectName = page.getProjectName();
    final String packageName = page.getBasePackage();
    final String gameClassName = page.getGameClassName();
    final String location = page.getLocation();

    IRunnableWithProgress op = new IRunnableWithProgress() {
      public void run(IProgressMonitor monitor) throws InvocationTargetException {
        try {
          doFinish(projectName, packageName, gameClassName, location, monitor);
        } catch (CoreException e) {
          throw new InvocationTargetException(e);
        } finally {
          monitor.done();
        }
      }
    };
    try {
      getContainer().run(true, false, op);
    } catch (InterruptedException e) {
      return false;
    } catch (InvocationTargetException e) {
      Throwable realException = e.getTargetException();
      MessageDialog.openError(getShell(), "Error", realException.getMessage());
      return false;
    }
    return true;
  }

  private void doFinish(final String projectName, final String packageName, final String gameClassName, final String location, IProgressMonitor monitor)
      throws CoreException {
    // create a sample file
    monitor.beginTask("Creating " + projectName, 2);

    // First create a simple project of type org.eclipse.core.resources.IProject:
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    IProject project = root.getProject(projectName);
    project.create(null);
    project.open(null);

    // Because we need a java project, we have to set the Java nature to the created project:
    IProjectDescription description = project.getDescription();
    description.setNatureIds(new String[] { JavaCore.NATURE_ID });
    project.setDescription(description, null);

    // Now we can create our Java project
    IJavaProject javaProject = JavaCore.create(project);

    //However, it's not enough if we want to add Java source code to the project. We have to set the Java build path:
    // (1) We first specify the output location of the compiler (the bin folder):
    IFolder binFolder = project.getFolder("bin");
    binFolder.create(false, true, null);
    javaProject.setOutputLocation(binFolder.getFullPath(), null);

    // (2) Define the class path entries. Class path entries define the roots of package fragments.
    List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
    IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
    LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
    for (LibraryLocation element : locations) {
      entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
    }

    //add libs to project class path
    javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);

    // (3) We have not yet the source folder created:
    IFolder sourceFolder = project.getFolder("src");
    sourceFolder.create(false, true, null);

    // (4) Now the created source folder should be added to the class entries of the project, otherwise compilation will fail:
    IPackageFragmentRoot frRoot = javaProject.getPackageFragmentRoot(sourceFolder);
    IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
    IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
    System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
    newEntries[oldEntries.length] = JavaCore.newSourceEntry(frRoot.getPath());
    javaProject.setRawClasspath(newEntries, null);

    // Now the Java project is ready for use. We can generate java classes as follows:
    // We can first create a Java package:
    IPackageFragment pack = javaProject.getPackageFragmentRoot(sourceFolder).createPackageFragment(packageName, false, null);

    StringBuffer buffer = new StringBuffer();
    buffer.append("package " + pack.getElementName() + ";\n");
    buffer.append("\n");
    buffer.append("import de.gurkenlabs.litiengine.Game;");
    buffer.append("\n\n");
    buffer.append("public class SampleMain {\n");
    buffer.append("  public static void main(String[] args) {\n");
    buffer.append("    Game.init();\n");
    buffer.append("    Game.start();\n");
    buffer.append("  }\n");
    buffer.append("}\n");

    ICompilationUnit cu = pack.createCompilationUnit(gameClassName + ".java", buffer.toString(), false, null);

    monitor.worked(1);
    monitor.setTaskName("Opening file for editing...");
    getShell().getDisplay().asyncExec(new Runnable() {
      public void run() {
        IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        /*
         * try {
         * IDE.openEditor(page, file, true);
         * } catch (PartInitException e) {
         * }
         */
      }
    });
    monitor.worked(1);
  }

  /**
   * We will initialize file contents with a sample text.
   */

  private InputStream openContentStream() {
    String contents = "This is the initial file contents for *.env file that should be word-sorted in the Preview page of the multi-page editor";
    return new ByteArrayInputStream(contents.getBytes());
  }

  private void throwCoreException(String message) throws CoreException {
    IStatus status = new Status(IStatus.ERROR, "de.gurkenlabs.litiengine.newproject", IStatus.OK, message, null);
    throw new CoreException(status);
  }

  /**
   * We will accept the selection in the workbench to see if
   * we can initialize from it.
   * 
   * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
   */
  public void init(IWorkbench workbench, IStructuredSelection selection) {
    this.selection = selection;
  }
}