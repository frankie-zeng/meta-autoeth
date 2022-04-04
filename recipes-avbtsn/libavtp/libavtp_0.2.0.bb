SUMMARY = "Open source implementation of Audio Video Transport Protocol (AVTP) specified in IEEE 1722-2016 spec"
SECTION = "base"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7fcb4331e23e45e171cd5693c1ff7d3e"

# DEPENDS = "boost"
# FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRCREV = "3599a5bf2d18fc3ae89b64f208d8380e6ee3a866"
SRC_URI = "git://github.com/Avnu/${BPN}.git;protocol=https \
    "
S = "${WORKDIR}/git"

inherit meson