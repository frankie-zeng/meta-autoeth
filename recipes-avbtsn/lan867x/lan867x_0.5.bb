SUMMARY = "Microchip 10BASE-T1S lan867x Phy driver"
SECTION = "base"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

S = "${WORKDIR}"


SRC_URI = "file://lan867x_phy.c \
    file://GPLv2.patch \
    file://cfg/plca-8-0.bin \
    file://cfg/plca-8-1.bin \
    file://cfg/plca-8-2.bin \
    file://cfg/plca-8-3.bin \
    file://cfg/plca-8-4.bin \
    file://cfg/plca-8-5.bin \
    file://cfg/plca-8-6.bin \
    file://cfg/plca-8-7.bin \
    file://t1s.sh \
	file://Makefile \
"

inherit module

MAKE_TARGETS = "module"

FILES:${PN} += "/lib/firmware/t1scfg/plca-8-0.bin \
    /lib/firmware/t1scfg/plca-8-1.bin \
    /lib/firmware/t1scfg/plca-8-2.bin \
    /lib/firmware/t1scfg/plca-8-3.bin \
    /lib/firmware/t1scfg/plca-8-4.bin \
    /lib/firmware/t1scfg/plca-8-5.bin \
    /lib/firmware/t1scfg/plca-8-6.bin \
    /lib/firmware/t1scfg/plca-8-7.bin \
    ${sbindir}/t1s.sh \
"

do_install:append() {
	install -d  ${D}/lib/firmware/t1scfg/
	# Libertas 
	install -c -m 0666 cfg/plca-8-0.bin ${D}/lib/firmware/t1scfg/plca-8-0.bin
    install -c -m 0666 cfg/plca-8-1.bin ${D}/lib/firmware/t1scfg/plca-8-1.bin
    install -c -m 0666 cfg/plca-8-2.bin ${D}/lib/firmware/t1scfg/plca-8-2.bin
    install -c -m 0666 cfg/plca-8-3.bin ${D}/lib/firmware/t1scfg/plca-8-3.bin
    install -c -m 0666 cfg/plca-8-4.bin ${D}/lib/firmware/t1scfg/plca-8-4.bin
    install -c -m 0666 cfg/plca-8-5.bin ${D}/lib/firmware/t1scfg/plca-8-5.bin
    install -c -m 0666 cfg/plca-8-6.bin ${D}/lib/firmware/t1scfg/plca-8-6.bin
    install -c -m 0666 cfg/plca-8-7.bin ${D}/lib/firmware/t1scfg/plca-8-7.bin

    install -d  ${D}${sbindir}/
    install -c -m 0666 t1s.sh ${D}${sbindir}/t1s.sh
	
}


# do_compile() {
#     cd kmod
# 	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
# 	oe_runmake \
# 		   CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
# 		   AR="${KERNEL_AR}" \
# 		   KSRC=${STAGING_KERNEL_DIR} \
# 		   KOBJ=${STAGING_KERNEL_BUILDDIR} \
# 	           O=${STAGING_KERNEL_BUILDDIR} 
# 	cd ../lib
# 	oe_runmake
# }

# do_install() {
# 	unset CFLAGS CPPFLAGS CXXFLAGS LDFLAGS
# 	cd kmod
# 	oe_runmake DEPMOD=echo INSTALL_MOD_PATH="${D}${nonarch_base_libdir}/.." \
# 	           CC="${KERNEL_CC}" LD="${KERNEL_LD}" \
# 			   KSRC=${STAGING_KERNEL_DIR} \
# 		   	   KOBJ=${STAGING_KERNEL_BUILDDIR} \
# 	           O=${STAGING_KERNEL_BUILDDIR} \
# 	           ${MODULES_INSTALL_TARGET}

# 	if [ ! -e "${B}/kmod/${MODULES_MODULE_SYMVERS_LOCATION}/Module.symvers" ] ; then
# 		bbwarn "Module.symvers not found in ${B}/${MODULES_MODULE_SYMVERS_LOCATION}"
# 		bbwarn "Please consider setting MODULES_MODULE_SYMVERS_LOCATION to a"
# 		bbwarn "directory below B to get correct inter-module dependencies"
# 	else
# 		install -Dm0644 "${B}/kmod/${MODULES_MODULE_SYMVERS_LOCATION}"/Module.symvers ${D}${includedir}/${BPN}/Module.symvers
# 		# Module.symvers contains absolute path to the build directory.
# 		# While it doesn't actually seem to matter which path is specified,
# 		# clear them out to avoid confusion
# 		sed -e 's:${B}/::g' -i ${D}${includedir}/${BPN}/Module.symvers
# 	fi
# 	install -d ${D}${includedir}
# 	install -c -m 0644 ${B}/lib/igb.h ${D}${includedir}/igb.h
	
# 	install -d ${D}${libdir}
# 	install -c -m 0644 ${B}/lib/libigb.a ${D}${libdir}/libigb.a
	
# }


# # MAKE_TARGETS = "kmod"

# # EXTRA_OEMAKE = "--include-dir=${WORKDIR}/kmod"
