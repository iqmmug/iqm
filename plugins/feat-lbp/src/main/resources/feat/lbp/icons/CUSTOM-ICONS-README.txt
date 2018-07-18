README: Custom Menu Icons
#########################

Any operator can use two distinct images/icons for enabled and disabled state, respectively.

The folder /src/main/resources/<operator-package-name>/icons may contain two files named
	- menu-item-enabled.png, and
	- menu-item-disabled.png.

If one of these files do not exist, or both are missing, the application substitutes them using default icons from the iqm-api module.
