SUMMARY = "An implementation of Scalable service-Oriented MiddlewarE over IP"
SECTION = "base"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

DEPENDS = "vsomeip"
# FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRCREV = "89720d3c63bbd22cbccc80cdc92c2f2dd20193ba"
SRC_URI = "git://github.com/COVESA/${BPN}.git;protocol=https \
    "
S = "${WORKDIR}/git"

# EXTRA_OECMAKE:= "-DUSE_INSTALLED_COMMONAPI=ON"
# inherit cmake lib_package gitpkgv

inherit cmake

# PACKAGES:remove += "${PN}-bin"
# FILES:${PN} += "${bindir}/vsomeipd ${sysconfdir}/${BPN}"
# FILES:${PN}-dev += "${libdir}/cmake"

BBCLASSEXTEND = "nativesdk"

# do_install:append() {
#     mv ${D}/usr/etc ${D}
# }