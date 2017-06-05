package de.gurkenlabs.litiengine.newproject.wizards;

import java.util.List;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;

public class LITIengineNewGameWizardPage extends WizardPage {

  private ISelection selection;
  public static List<EngineRelease> releases;
  private Text text_4;
  private Text text_5;
  private Text txtLocation;
  private Text txtGameClass;
  private Text txtBasePackage;
  private Text txtProjectName;
  private Combo combo;

  /**
   * Constructor for SampleNewWizardPage.
   * 
   * @param pageName
   */
  public LITIengineNewGameWizardPage(ISelection selection) {
    super("wizardPage");
    setTitle("Create New LITIengine Game");
    setDescription("This wizard creates the project structure for a new game created with the LITIengine.");
    this.selection = selection;
  }

  /**
   * @see IDialogPage#createControl(Composite)
   */
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NULL);
    container.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent arg0) {
        if (combo == null) {
          return;
        }

        if (releases == null) {
          releases = ReleaseBuildManager.getLitiEngineReleases();
        }

        combo.removeAll();
        for (EngineRelease release : releases) {
          combo.add(release.getName());
        }

      }
    });

    initialize();
    dialogChanged();
    setControl(container);
    container.setLayout(new GridLayout(1, false));

    Composite composite = new Composite(container, SWT.NONE);

    Canvas canvas = new Canvas(composite, SWT.NONE);
    canvas.setBounds(0, 0, 299, 86);

    Group group = new Group(container, SWT.NONE);
    GridData gd_group = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
    gd_group.widthHint = 580;
    group.setLayoutData(gd_group);
    group.setText("Engine");
    group.setLayout(new GridLayout(3, false));

    Button btnRadioButton = new Button(group, SWT.RADIO);
    btnRadioButton.setText("Select Project from workspace");

    text_5 = new Text(group, SWT.BORDER);
    text_5.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Button btnNewButton = new Button(group, SWT.NONE);
    btnNewButton.setText("...");

    Button btnSelectLibraryFrom = new Button(group, SWT.RADIO);
    btnSelectLibraryFrom.setText("Select library from local machine");

    text_4 = new Text(group, SWT.BORDER);
    text_4.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Button btnNewButton_1 = new Button(group, SWT.NONE);
    btnNewButton_1.setText("...");

    Button btnDownloadAvailableRelease = new Button(group, SWT.RADIO);
    btnDownloadAvailableRelease.setText("Download available release");

    combo = new Combo(group, SWT.NONE);
    combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    new Label(group, SWT.NONE);

    Group grpGameProject = new Group(container, SWT.NONE);
    GridData gd_grpGameProject = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gd_grpGameProject.widthHint = 580;
    grpGameProject.setLayoutData(gd_grpGameProject);
    grpGameProject.setText("Game Project");
    grpGameProject.setLayout(new GridLayout(3, false));

    Label label_1 = new Label(grpGameProject, SWT.NONE);
    GridData gd_label_1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1);
    gd_label_1.widthHint = 195;
    label_1.setLayoutData(gd_label_1);
    label_1.setText("&Project name:");

    txtProjectName = new Text(grpGameProject, SWT.BORDER);
    txtProjectName.setText("litiengine-game");
    txtProjectName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    new Label(grpGameProject, SWT.NONE);

    Label label_3 = new Label(grpGameProject, SWT.NONE);
    label_3.setText("&Base package:");

    txtBasePackage = new Text(grpGameProject, SWT.BORDER);
    txtBasePackage.setText("de.gurkenlabs.litienginegame");
    txtBasePackage.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    new Label(grpGameProject, SWT.NONE);

    Label label_5 = new Label(grpGameProject, SWT.NONE);
    label_5.setText("&Game Class:");

    txtGameClass = new Text(grpGameProject, SWT.BORDER);
    txtGameClass.setText("LITIgame");
    txtGameClass.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
    new Label(grpGameProject, SWT.NONE);

    Label label_7 = new Label(grpGameProject, SWT.NONE);
    label_7.setText("&Location:");

    txtLocation = new Text(grpGameProject, SWT.BORDER);
    txtLocation.setText("F:/workspaces/gurkenlabs");
    txtLocation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

    Button btnNewButton_2 = new Button(grpGameProject, SWT.NONE);
    btnNewButton_2.setText("...");
  }

  private void initialize() {
    if (selection != null && selection.isEmpty() == false
        && selection instanceof IStructuredSelection) {
      IStructuredSelection ssel = (IStructuredSelection) selection;
      if (ssel.size() > 1)
        return;
    }
  }

  /**
   * Ensures that both text fields are set.
   */

  private void dialogChanged() {
    String projectName = getProjectName();

    if (projectName == null || projectName.length() == 0) {
      updateStatus("Project name must be specified");
      return;
    }
    if (projectName != null && projectName.replace('\\', '/').indexOf('/', 1) > 0) {
      updateStatus("Project name must be valid");
      return;
    }
    updateStatus(null);
  }

  private void updateStatus(String message) {
    setErrorMessage(message);
    setPageComplete(message == null);
  }

  public String getProjectName() {
    return this.txtProjectName != null ? this.txtProjectName.getText() : null;
  }

  public String getBasePackage() {
    return this.txtBasePackage != null ? this.txtBasePackage.getText() : null;
  }

  public String getGameClassName() {
    return this.txtGameClass != null ? this.txtGameClass.getText() : null;
  }

  public String getLocation() {
    return this.txtLocation != null ? this.txtLocation.getText() : null;
  }
}