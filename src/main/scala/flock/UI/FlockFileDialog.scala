package flock.UI

import scalafx.stage.{FileChooser, Stage}
import scalafx.stage.FileChooser.ExtensionFilter
import java.io.File

object FlockFileDialog:

  private def makeChooser(dialogTitle: String): FileChooser = new FileChooser:
    this.title = dialogTitle
    extensionFilters.add(new ExtensionFilter("JSON Files", "*.json"))

  def showOpen(owner: Stage): Option[File] =
    Option(makeChooser("Open Flock").showOpenDialog(owner))

  def showSaveAs(owner: Stage): Option[File] =
    val fc = makeChooser("Save Flock As")
    fc.initialFileName = "save.json"
    Option(fc.showSaveDialog(owner))

end FlockFileDialog