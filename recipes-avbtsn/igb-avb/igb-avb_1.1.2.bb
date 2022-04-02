SUMMARY = "daemon that sends updates when your IP changes"
HOMEPAGE = "https://github.com/frankie-zeng/igb-avb"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://kmod/LICENSE;md5=e2c0cd0820d168b0b26e19f13df4dc56"
DEPENDS = "virtual/kernel"

SRCREV_machine = "f403012627524a21fd0cf463ecdb351ae0291014"

SRC_URI = "git://github.com/frankie-zeng/igb-avb.git;name=machine;branch=master;protocol=https \
		  file://0001-change-vsp-csp.patch \
		  "
S = "${WORKDIR}/git"

SRC_URI[md5sum] = "96d9ea1578162929c30b48e6813227ff"

inherit module


do_configure() {
	cd kmod
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake \
		   CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
		   AR="${KERNEL_AR}" \
		   KSRC=${STAGING_KERNEL_DIR} \
		   KOBJ=${STAGING_KERNEL_BUILDDIR} \
	           O=${STAGING_KERNEL_BUILDDIR} \
			clean
}


do_compile() {
    cd kmod
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	oe_runmake \
		   CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
		   AR="${KERNEL_AR}" \
		   KSRC=${STAGING_KERNEL_DIR} \
		   KOBJ=${STAGING_KERNEL_BUILDDIR} \
	           O=${STAGING_KERNEL_BUILDDIR} 
	cd ../lib
	oe_runmake
}

do_install() {
	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
	cd kmod
	oe_runmake DEPMOD=echo INSTALL_MOD_PATH="${D}${nonarch_base_libdir}/.." \
	           CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
			   KSRC=${STAGING_KERNEL_DIR} \
		   	   KOBJ=${STAGING_KERNEL_BUILDDIR} \
	           O=${STAGING_KERNEL_BUILDDIR} \
	           ${MODULES_INSTALL_TARGET}

	if [ ! -e "${B}/kmod/${MODULES_MODULE_SYMVERS_LOCATION}/Module.symvers" ] ; then
		bbwarn "Module.symvers not found in ${B}/${MODULES_MODULE_SYMVERS_LOCATION}"
		bbwarn "Please consider setting MODULES_MODULE_SYMVERS_LOCATION to a"
		bbwarn "directory below B to get correct inter-module dependencies"
	else
		install -Dm0644 "${B}/kmod/${MODULES_MODULE_SYMVERS_LOCATION}"/Module.symvers ${D}${includedir}/${BPN}/Module.symvers
		# Module.symvers contains absolute path to the build directory.
		# While it doesn't actually seem to matter which path is specified,
		# clear them out to avoid confusion
		sed -e 's:${B}/::g' -i ${D}${includedir}/${BPN}/Module.symvers
	fi
	install -d ${D}${includedir}
	install -c -m 0644 ${B}/lib/igb.h ${D}${includedir}/igb.h
	
	install -d ${D}${libdir}
	install -c -m 0644 ${B}/lib/libigb.a ${D}${libdir}/libigb.a
	
}


# MAKE_TARGETS = "kmod"

# EXTRA_OEMAKE = "--include-dir=${WORKDIR}/kmod"
