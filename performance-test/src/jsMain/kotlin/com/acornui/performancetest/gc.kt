package com.acornui.performancetest

/**
 * Suggests a garbage collection.  Only works in Chrome, Opera, and Edge.
 * Chrome: must launch from a command line/terminal with --js-flags="--expose-gc"
 */
actual fun gc() {
	js("""
		try {
			if (window.gc !== undefined) 
				window.gc();
			if (window.opera !== undefined) 
				window.opera.collect();
			if (window.CollectGarbage !== undefined)
				window.CollectGarbage();
		} catch(e) {}
	""")
}