SUMMARY = "An implementation of Scalable service-Oriented MiddlewarE over IP"
SECTION = "base"
LICENSE = "MPLv2"
LIC_FILES_CHKSUM = "file://LICENSE;md5=815ca599c9df247a0c7f619bab123dad"

DEPENDS = "boost"
# FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRCREV = "17cc55f24d1c56f6a5dcca6065a227ca91d01c90"
SRC_URI = "git://github.com/COVESA/${BPN}.git;protocol=https \
    "
S = "${WORKDIR}/git"

inherit cmake lib_package gitpkgv

PACKAGES:remove += "${PN}-bin"
FILES:${PN} += "${bindir}/vsomeipd ${sysconfdir}/${BPN}"
FILES:${PN}-dev += "${libdir}/cmake"

BBCLASSEXTEND = "nativesdk"

do_install:append() {
    mv ${D}/usr/etc ${D}
}