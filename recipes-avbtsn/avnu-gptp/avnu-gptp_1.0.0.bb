SUMMARY = "The Avnu Alliance Gptp."
HOMEPAGE = "https://github.com/Avnu/gptp"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=b3e2a6e620763288bcbc3190f6cb1704"

DEPENDS = "igb-avb pciutils"

SRCREV_machine = "0baef8a36a13105112862919aac0f1eed21a44ea"

SRC_URI = "git://github.com/Avnu/gptp.git;name=machine;branch=master;protocol=https \
           "
S = "${WORKDIR}/git"

FILES:${PN} += "${bindir}/daemon_cl"

inherit useradd

USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM:${PN} = "ptp"



TARGET_CC_ARCH += "${LDFLAGS}"


do_clean(){
	cd ${S}/linux/build
	oe_runmake ARCH=I210 clean
}

do_compile() {
	cd ${S}/linux/build
	oe_runmake ARCH=I210 all
}

do_install() {
	install -d ${D}${bindir}/avnu
	install -c -m 0755 ${S}/linux/build/obj/daemon_cl ${D}${bindir}/avnu/daemon_cl
}


