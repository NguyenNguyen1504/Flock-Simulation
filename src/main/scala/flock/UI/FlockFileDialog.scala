package flock.UI

import scalafx.stage.{FileChooser, Stage}
import scalafx.stage.FileChooser.ExtensionFilter
import java.io.File

/** Utility object for showing native file-chooser dialogs scoped to JSON flock files.
 *
 *  Both dialogs open in the `data/` directory if it exists, falling back to the current
 *  working directory. Results are wrapped in [[Option]] so callers handle cancellation
 *  without dealing with null.
 */
object FlockFileDialog:

  private val dataDirectory = new File("data")

  /** Creates a [[FileChooser]] pre-configured with a title and a JSON extension filter. */
  private def makeChooser(dialogTitle: String): FileChooser = new FileChooser:
    this.title = dialogTitle
    initialDirectory = if dataDirectory.exists() then dataDirectory else new File(".")
    extensionFilters.add(new ExtensionFilter("JSON Files", "*.json"))

  /** Shows a native open-file dialog. Returns the chosen [[File]], or [[None]] if cancelled. */
  def showOpen(owner: Stage): Option[File] =
    Option(makeChooser("Open Flock").showOpenDialog(owner))

  /** Shows a native save-file dialog with a default filename of `save.json`.
   *  Returns the chosen [[File]], or [[None]] if cancelled.
   */
  def showSaveAs(owner: Stage): Option[File] =
    val fc = makeChooser("Save Flock As")
    fc.initialFileName = "save.json"
    Option(fc.showSaveDialog(owner))

end FlockFileDialog