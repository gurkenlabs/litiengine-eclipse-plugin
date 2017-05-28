package de.gurkenlabs.litiengine.newproject.wizards;

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

public class LITIengineNewGameWizardPage extends WizardPage {
  private Text projectText;
  private Text packageText;
  private Text gameClassText;
  private Text locationText;

  private ISelection selection;
  private Canvas canvas;

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
    GridLayout layout = new GridLayout();
    container.setLayout(layout);
    layout.numColumns = 3;
    layout.verticalSpacing = 9;
    new Label(container, SWT.NONE);
    new Label(container, SWT.NONE);

    canvas = new Canvas(container, SWT.NONE);
    GridData gd_canvas = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
    gd_canvas.heightHint = 86;
    gd_canvas.widthHint = 299;
    canvas.setLayoutData(gd_canvas);
    canvas.addPaintListener(new PaintListener() {
      public void paintControl(PaintEvent arg0) {
        Image ideaImage = new Image(canvas.getDisplay(), getClass().getResourceAsStream("/icons/litiengine-logo.png"));
        arg0.gc.drawImage(ideaImage, 0, 0);
      }
    });
    Label label = new Label(container, SWT.NONE);

    label = new Label(container, SWT.NULL);
    label.setText("&Project name:");

    projectText = new Text(container, SWT.BORDER | SWT.SINGLE);
    projectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    projectText.setText("litiengine-game");
    projectText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });
    Label labelPackage = new Label(container, SWT.NULL);
    labelPackage = new Label(container, SWT.NULL);
    labelPackage.setText("&Base package:");

    packageText = new Text(container, SWT.BORDER | SWT.SINGLE);
    packageText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    packageText.setText("de.gurkenlabs.litienginegame");
    packageText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    Label lableGameClass = new Label(container, SWT.NULL);
    lableGameClass = new Label(container, SWT.NULL);
    lableGameClass.setText("&Game Class:");

    gameClassText = new Text(container, SWT.BORDER | SWT.SINGLE);
    gameClassText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    gameClassText.setText("LITIgame");
    gameClassText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    Label lableLocation = new Label(container, SWT.NULL);
    lableLocation = new Label(container, SWT.NULL);
    lableLocation.setText("&Location:");

    locationText = new Text(container, SWT.BORDER | SWT.SINGLE);
    locationText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    locationText.setText(ResourcesPlugin.getWorkspace().getRoot().getLocation().toString());
    locationText.addModifyListener(new ModifyListener() {
      public void modifyText(ModifyEvent e) {
        dialogChanged();
      }
    });

    initialize();
    dialogChanged();
    setControl(container);
  }

  /**
   * Tests if the current workbench selection is a suitable container to use.
   */

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

    if (projectName.length() == 0) {
      updateStatus("Project name must be specified");
      return;
    }
    if (projectName.replace('\\', '/').indexOf('/', 1) > 0) {
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
    return projectText.getText();
  }

  public String getBasePackage() {
    return packageText.getText();
  }

  public String getGameClassName() {
    return gameClassText.getText();
  }

  public String getLocation() {
    return locationText.getText();
  }
}