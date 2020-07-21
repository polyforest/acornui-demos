import com.acornui.application
import com.acornui.runMain

/**
 * `main` is our main entry point.
 *
 * This method is wrapped in a [runMain] block to set up the main loop and context.
 */
fun main() = runMain {

	application("acornUiRoot") {
		// Create and add our main component to the stage:
		+inputsDemo()
	}
}