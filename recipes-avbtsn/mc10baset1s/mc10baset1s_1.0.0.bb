SUMMARY = "Frankie Zeng 10Base T1S board control script."
HOMEPAGE = "https://github.com/frankie-zeng/mc10baset1s"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=751419260aa954499f7abaabaa882bbe"

DEPENDS = "python3-pyusb"

SRCREV_machine = "f30f6efc2c53267a30e6b8011a90284a73936589"

SRC_URI = "git://github.com/frankie-zeng/mc10baset1s.git;name=machine;branch=master;protocol=https \
		   file://GPLv2.patch \
           "
S = "${WORKDIR}/git"


FILES:${PN} += "${bindir}/mc10baset1s.py"



do_install() {
	install -d ${D}${bindir}
	install -c -m 0755 ${S}/mc10baset1s.py ${D}${bindir}/mc10baset1s.py
}


