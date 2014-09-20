/******************************************************************************
 *
 * Sugar for Java 1.3.0
 * Copyright (C) 2001-2004  Dipl.-Inf. (FH) Johann Nepomuk Loefflmann,
 * All Rights Reserved, http://www.jonelo.de
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author jonelo@jonelo.de
 *
 *****************************************************************************/
package jonelo.sugar.util;

public class GeneralProgram {

	/** Creates new GeneralUtil */
	public GeneralProgram() {
	}

	/**
	 * which Java Version is required?
	 * 
	 * @param version
	 *            Java version (e. g. "1.3.1")
	 */
	public final static void requiresMinimumJavaVersion(final String version) {
		try {
			String ver = System.getProperty("java.version");
			// no java check under the Kaffe Java VM for example

			// http://www.hp.com/products1/unix/java/faq/#prod3
			if ((System.getProperty("java.vm.vendor").startsWith(
					"Sun Microsystems")
					|| System.getProperty("java.vm.vendor").startsWith(
							"IBM Corporation") || System.getProperty(
					"java.vm.vendor").startsWith("Hewlett-Packard"))
					&& (ver.compareTo(version) < 0)) {
				System.out
						.println("ERROR: a newer Java VM is required.\nVersion of your Java VM: "
								+ ver
								+ "\nRequired minimum version: "
								+ version);
				System.exit(1);
			}
		} catch (Throwable t) {
			System.out.println("uncaught exception: " + t);
			t.printStackTrace();
		}
	}

	public static boolean isSupportFor(String version) {
		return (System.getProperty("java.version").compareTo(version) >= 0);
	}

}
